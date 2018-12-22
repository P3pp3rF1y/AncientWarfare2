package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.shadowmage.ancientwarfare.automation.AncientWarfareAutomation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketManualReload;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils extends ParentCommand {
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

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.aw.utils.usage";
	}

	private abstract static class ExportCommand implements ISubCommand {
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			List<String> lines = getLines();
			String fileName = args.length > 0 ? args[0] : getDefaultFileName();
			File file = new File(AWCoreStatics.utilsExportPath, fileName);
			exportToFile(file, getHeader(), lines);
			notifyPlayer(sender, file);
		}

		protected abstract String getHeader();

		protected abstract String getDefaultFileName();

		protected abstract List<String> getLines();

		private static void exportToFile(File exportFile, String header, List<String> rows) {

			if (!exportFile.exists()) {
				try {
					if (!exportFile.getParentFile().mkdirs()) {
						AncientWarfareCore.LOG.error("Unable to create folders for file : " + exportFile.getAbsolutePath());
					}
					if (!exportFile.createNewFile()) {
						AncientWarfareCore.LOG.error("Unable to open new file : " + exportFile.getAbsolutePath());
					}
				}
				catch (IOException e) {
					AncientWarfareCore.LOG.error("Error opening file : " + exportFile.getAbsolutePath(), e);
					return;
				}
			}
			try (FileWriter fileWriter = new FileWriter(exportFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
				writer.write(header);
				writer.newLine();
				for (String row : rows) {
					writer.write(row);
					writer.newLine();
				}
			}
			catch (IOException e) {
				AncientWarfareCore.LOG.error("Error exporting file: " + exportFile.getAbsolutePath(), e);
			}
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
	}

	private static final Field BIOME_NAME = ReflectionHelper.findField(Biome.class, "biomeName", "field_76791_y");

	private class BiomeListCommand extends ExportCommand {
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
	}

	private class BlockListCommand extends ExportCommand {

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
	}

	private class ReloadManualCommand implements ISubCommand {
		@Override
		public String getName() {
			return "reloadmanual";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			Entity senderEntity = sender.getCommandSenderEntity();
			if (senderEntity instanceof EntityPlayer) {
				NetworkHandler.sendToPlayer((EntityPlayerMP) senderEntity, new PacketManualReload());
			}
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}
	}

	private class LootTableListCommand extends ExportCommand {
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
	}

	private class ChunkLoadCommand implements ISubCommand {
		@Override
		public String getName() {
			return "loadChunks";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (args.length < 1) {
				throw new WrongUsageException(getUsage(sender));
			}
			ForgeChunkManager.Ticket chunkTicket = ForgeChunkManager.requestTicket(AncientWarfareAutomation.instance, sender.getEntityWorld(), ForgeChunkManager.Type.NORMAL);
			int range = Integer.parseInt(args[0]);
			World world = sender.getEntityWorld();
			ChunkPos senderChunkPos = world.getChunkFromBlockCoords(sender.getPosition()).getPos();
			for (int x = senderChunkPos.x - range; x <= senderChunkPos.x + range; x++) {
				for (int z = senderChunkPos.z - range; z <= senderChunkPos.z + range; z++) {
					ForgeChunkManager.forceChunk(chunkTicket, new ChunkPos(x, z));
				}
			}
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}
	}
}
