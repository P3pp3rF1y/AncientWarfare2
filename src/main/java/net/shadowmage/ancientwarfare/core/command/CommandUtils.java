package net.shadowmage.ancientwarfare.core.command;

import com.google.common.collect.AbstractIterator;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketManualReload;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils extends RootCommand {
	public CommandUtils() {
		registerSubCommand(new EntityListCommand());
		registerSubCommand(new EntityListCommand());
		registerSubCommand(new BiomeListCommand());
		registerSubCommand(new BlockListCommand());
		registerSubCommand(new ReloadManualCommand());
		registerSubCommand(new LootTableListCommand());
		registerSubCommand(new ChunkLoadCommand());
	}

	@Override
	public String getName() {
		return "awutils";
	}

	private abstract static class ExportCommand implements ISubCommand {
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			List<String> lines = getLines();
			String fileName = args.length > 0 ? args[0] : getDefaultFileName();
			String filePath = AWCoreStatics.utilsExportPath;
			File file = new File(filePath, fileName);
			exportToFile(file, getHeader(), lines);
			notifyPlayer(sender, file);
		}

		protected abstract String getHeader();

		protected abstract String getDefaultFileName();

		protected abstract List<String> getLines();

		private static void exportToFile(File exportFile, String header, List<String> data) {
			ArrayList<String> rows = new ArrayList<>();
			rows.add(header);
			rows.addAll(data);
			FileUtils.exportToFile(exportFile, rows);
		}

		private static void notifyPlayer(ICommandSender sender, File exportFile) {
			sender.sendMessage(new TextComponentString("File exported to " + exportFile.getAbsoluteFile()));
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}
	}

	private static class EntityListCommand extends ExportCommand {
		@Override
		protected String getHeader() {
			return "Registry Name,Entity Name,Entity Class";
		}

		@Override
		protected String getDefaultFileName() {
			return "entitylist.csv";
		}

		@Override
		protected List<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.ENTITIES.getValuesCollection().stream()
					.map(e -> String.join(",", e.getRegistryName().toString(), e.getName(), e.getEntityClass().toString()))
					.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		}

		@Override
		public String getName() {
			return "exportentities";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName() + " [fileName - defaults to \"entitylist.csv\"]";
		}
	}

	private static final Field BIOME_NAME = ObfuscationReflectionHelper.findField(Biome.class, "field_76791_y");

	private static class BiomeListCommand extends ExportCommand {
		@Override
		protected String getHeader() {
			return "Registry Name,Biome Name,Temperature Category,High Humidity,Height Variation,Top Block,Biome Types,Biome Class";
		}

		@Override
		protected String getDefaultFileName() {
			return "biomelist.csv";
		}

		@Override
		protected List<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.BIOMES.getValuesCollection().stream()
					.map(b -> String.join(",", b.getRegistryName().toString(), getBiomeName(b), b.getTempCategory().name()
							, Boolean.toString(b.isHighHumidity()), Float.toString(b.getHeightVariation()), b.topBlock.getBlock().getRegistryName().toString()
							, BiomeDictionary.getTypes(b).stream().map(BiomeDictionary.Type::getName).collect(Collectors.joining("|")),
							b.getBiomeClass().toString()))
					.sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		}

		private String getBiomeName(Biome b) {
			try {
				return (String) BIOME_NAME.get(b);
			}
			catch (IllegalAccessException e) {
				AncientWarfareCore.LOG.error(e);
			}
			return "";
		}

		@Override
		public String getName() {
			return "exportbiomes";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName() + " [fileName - defaults to \"biomelist.csv\"]";
		}
	}

	private static class BlockListCommand extends ExportCommand {

		@Override
		protected String getHeader() {
			return "Registry Name,Block Name,Skippable,Skippable Material,Target,Target Material";
		}

		@Override
		protected String getDefaultFileName() {
			return "blocklist.csv";
		}

		@Override
		protected List<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.BLOCKS.getValuesCollection().stream()
					.map(b -> String.join(",", b.getRegistryName().toString(), b.getLocalizedName(),
							AWStructureStatics.isSkippable(b.getDefaultState()) ? "Y" : "N",
							AWStructureStatics.isSkippableMaterial(b.getDefaultState().getMaterial()) ? "Y" : "N",
							AWStructureStatics.isValidTargetBlock(b.getDefaultState()) ? "Y" : "N",
							AWStructureStatics.isValidTargetMaterial(b.getDefaultState().getMaterial()) ? "Y" : "N"
					)).sorted(Comparator.naturalOrder()).collect(Collectors.toList());
		}

		@Override
		public String getName() {
			return "exportblocks";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName() + " [fileName - defaults to \"blocklist.csv\"]";
		}
	}

	private static class ReloadManualCommand implements ISubCommand {
		@Override
		public String getName() {
			return "reloadmanual";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
			Entity senderEntity = sender.getCommandSenderEntity();
			if (senderEntity instanceof EntityPlayer) {
				NetworkHandler.sendToPlayer((EntityPlayerMP) senderEntity, new PacketManualReload());
			}
		}

		@Override
		public int getMaxArgs() {
			return 0;
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName();
		}
	}

	private static class LootTableListCommand extends ExportCommand {
		@Override
		protected String getHeader() {
			return "Registry Name";
		}

		@Override
		protected String getDefaultFileName() {
			return "loottablelist.csv";
		}

		@Override
		protected List<String> getLines() {
			return LootTableList.getAll().stream().map(ResourceLocation::toString).collect(Collectors.toList());
		}

		@Override
		public String getName() {
			return "exportloottables";
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName() + " [fileName - defaults to \"loottablelist.csv\"]";
		}
	}

	private static class ChunkLoadCommand implements ISubCommand {
		private PlayerMover playerMover = new PlayerMover();

		private ChunkLoadCommand() {
			MinecraftForge.EVENT_BUS.register(playerMover);
		}

		@Override
		public String getName() {
			return "loadChunks";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 1) {
				throw new WrongUsageException(getUsage(sender));
			}
			if (!(sender instanceof EntityPlayerMP)) {
				return;
			}
			int chunkLoadRadius = server.getPlayerList().getViewDistance();
			int range = Integer.parseInt(args[0]);

			playerMover.startMoving((EntityPlayerMP) sender, sender.getEntityWorld(), chunkLoadRadius, range);
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}

		@Override
		public String getUsage(ICommandSender sender) {
			return getName() + " <diameterInChunks>";
		}

		//this is not made for multiple players using it on server as there's likely no need for that
		private static class PlayerMover {
			private EntityPlayerMP player;
			private int chunkLoadRadius;
			private int range;
			private BlockPos originalPosition;
			private ChunkPos originalChunkPos;
			private boolean finishedMoving = true;
			private Iterator<ChunkPos> iterator;
			private int timeout = 0;

			@SuppressWarnings("unused") // used in event listener reflection
			@SubscribeEvent
			public void serverTick(TickEvent.ServerTickEvent evt) {
				if (evt.phase == TickEvent.Phase.END) {
					if (timeout <= 0) {
						movePlayer();
						timeout = 200;
					} else {
						timeout--;
					}
				}
			}

			private void startMoving(EntityPlayerMP player, World world, int chunkLoadRadius, int range) {
				this.player = player;
				originalPosition = player.getPosition();
				originalChunkPos = world.getChunkFromBlockCoords(originalPosition).getPos();
				this.chunkLoadRadius = chunkLoadRadius;
				this.range = range;
				finishedMoving = false;
				iterator = getAllChunkPosStops();
			}

			private Iterator<ChunkPos> getAllChunkPosStops() {
				return new AbstractIterator<ChunkPos>() {
					private boolean first = true;
					private int currentX;
					private int currentZ;

					@Override
					protected ChunkPos computeNext() {
						if (first) {
							currentX = getInitialX();
							currentZ = getInitialZ();
							first = false;
						} else if (currentX + chunkLoadRadius >= originalChunkPos.x + range && currentZ + chunkLoadRadius >= originalChunkPos.z + range) {
							return endOfData();
						} else {
							if (currentX + chunkLoadRadius < originalChunkPos.x + range) {
								currentX += 2 * chunkLoadRadius;
							} else if (currentZ + chunkLoadRadius < originalChunkPos.z + range) {
								currentX = getInitialX();
								currentZ += 2 * chunkLoadRadius;
							}
						}
						return new ChunkPos(currentX, currentZ);
					}

					private int getInitialZ() {
						return originalChunkPos.z - range + chunkLoadRadius;
					}

					private int getInitialX() {
						return originalChunkPos.x - range + chunkLoadRadius;
					}
				};
			}

			private void movePlayer() {
				if (!finishedMoving) {
					if (!iterator.hasNext()) {
						player.connection.setPlayerLocation(originalPosition.getX(), originalPosition.getY(), originalPosition.getZ(), player.rotationYaw, player.rotationPitch);
						finishedMoving = true;
						return;
					}
					ChunkPos chunkPos = iterator.next();
					player.connection.setPlayerLocation(chunkPos.getXStart() + 8d, 255, chunkPos.getZStart() + 8d, player.rotationYaw, player.rotationPitch);
				}
			}
		}
	}
}
