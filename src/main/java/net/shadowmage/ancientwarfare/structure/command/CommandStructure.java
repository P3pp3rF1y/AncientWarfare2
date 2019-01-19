package net.shadowmage.ancientwarfare.structure.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.core.command.ISubCommand;
import net.shadowmage.ancientwarfare.core.command.ParentCommand;
import net.shadowmage.ancientwarfare.core.command.SimpleSubCommand;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.WorldGenStructureManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;
import net.shadowmage.ancientwarfare.structure.tile.ScannerTracker;
import net.shadowmage.ancientwarfare.structure.worldgen.StructureEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandStructure extends ParentCommand {

	public CommandStructure() {
		registerSubCommand(new DeleteCommand());
		registerSubCommand(new BuildCommand());
		registerSubCommand(new SaveCommand());
		registerSubCommand(new SimpleSubCommand("reload",
				(server, sender, args) -> {
					WorldGenStructureManager.INSTANCE.loadBiomeList(); //reset biome to template cache
					TemplateLoader.INSTANCE.reloadAll();
					sender.sendMessage(new TextComponentTranslation("command.aw.structure.reloaded"));
				}));
		registerSubCommand(new ReexportCommand());
		registerSubCommand(new SimpleSubCommand("scannerTp", (server, sender, args) -> {
			if (args.length == 1 && sender instanceof EntityPlayer) {
				Tuple<Integer, BlockPos> pos = ScannerTracker.getScannerPosByName(args[0]);
				ScannerTracker.teleportAboveScannerBlock((EntityPlayer) sender, pos);
			}
		}) {
			@Override
			public int getMaxArgs() {
				return 1;
			}
		});
		registerSubCommand(new SimpleSubCommand("name", (server, sender, args) -> {
			Optional<StructureEntry> structure = AWGameData.INSTANCE.getData(sender.getEntityWorld(), StructureMap.class)
					.getStructureAt(sender.getEntityWorld(), sender.getPosition());

			sender.sendMessage(structure.map(structureEntry -> new TextComponentTranslation("command.aw.structure.name", structureEntry.getName()))
					.orElseGet(() -> new TextComponentTranslation("command.aw.structure.no_structure")));
		}));
	}

	@Override
	public String getName() {
		return "awstructure";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "command.aw.structure.usage";
	}

	private class SaveCommand implements ISubCommand {
		@Override
		public String getName() {
			return "save";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] subArgs) throws CommandException {
			if (sender instanceof EntityLivingBase) {
				@Nonnull ItemStack stack = ((EntityLivingBase) sender).getHeldItemMainhand();
				if (!stack.isEmpty()) {
					ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
					if (settings.hasPos1() && settings.hasPos2() && settings.hasBuildKey() && (settings.hasName() || subArgs.length > 0)) {
						String name = settings.hasName() ? settings.name() : subArgs[0];
						ItemStructureScanner.setStructureName(stack, name);
						if (ItemStructureScanner.scanStructure(sender.getEntityWorld(), stack)) {
							sender.sendMessage(new TextComponentTranslation("command.aw.structure.exported", subArgs[0]));
						}
					} else {
						sender.sendMessage(new TextComponentTranslation("command.aw.structure.incomplete_data"));
					}
				}
			}
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}
	}

	private class BuildCommand implements ISubCommand {
		@Override
		public String getName() {
			return "build";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] subArgs) throws CommandException {
			if (subArgs.length < 4) {
				throw new WrongUsageException(getUsage(sender));
			}

			int x = CommandBase.parseInt(subArgs[1]);
			int y = CommandBase.parseInt(subArgs[2]);
			int z = CommandBase.parseInt(subArgs[3]);
			EnumFacing face = EnumFacing.SOUTH;
			if (subArgs.length > 4) {
				face = EnumFacing.byName(subArgs[4]);
			}
			Optional<StructureTemplate> template = StructureTemplateManager.getTemplate(subArgs[0]);
			if (template.isPresent()) {
				StructureBuilder builder = new StructureBuilder(sender.getEntityWorld(), template.get(), face, new BlockPos(x, y, z));
				builder.instantConstruction();
				sender.sendMessage(new TextComponentTranslation("command.aw.structure.built", subArgs[0], x, y, z));
			} else {
				sender.sendMessage(new TextComponentTranslation("command.aw.structure.not_found", subArgs[0]));
			}
		}

		@Override
		public int getMaxArgs() {
			return 5;
		}
	}

	private class DeleteCommand implements ISubCommand {
		@Override
		public String getName() {
			return "delete";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] subArgs) throws CommandException {
			if (subArgs.length < 1) {
				throw new WrongUsageException(getUsage(sender));
			}
			String name = subArgs[0];
			boolean flag = StructureTemplateManager.removeTemplate(name);
			if (flag)//check if var2.len>=3, pull string of end...if string==true, try delete template file for name
			{
				TextComponentTranslation txt = new TextComponentTranslation("command.aw.structure.template_removed", name);
				sender.sendMessage(txt);
				if (subArgs.length > 1) {
					boolean shouldDelete = subArgs[1].equalsIgnoreCase("true");
					if (shouldDelete) {
						if (deleteTemplateFile(name)) {
							txt = new TextComponentTranslation("command.aw.structure.file_deleted", name);
						} else {
							txt = new TextComponentTranslation("command.aw.structure.file_not_found", name);
						}
						sender.sendMessage(txt);
					}
				}
			} else//send template not found message
			{
				sender.sendMessage(new TextComponentTranslation("command.aw.structure.not_found", name));
			}
		}

		private boolean deleteTemplateFile(String name) {
			String path = TemplateLoader.INCLUDE_DIRECTORY + name + "." + AWStructureStatics.templateExtension;
			File file = new File(path);
			if (file.exists()) {
				file.delete();
				return true;
			}
			return false;
		}

		@Override
		public int getMaxArgs() {
			return 2;
		}
	}

	private class ReexportCommand implements ISubCommand {
		@Override
		public String getName() {
			return "scannersReexport";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			if (sender instanceof EntityPlayer) {
				boolean reloadMainSettings = false;
				if (args.length == 1) {
					reloadMainSettings = Boolean.valueOf(args[0]);
				}
				ScannerTracker.reexportAll((EntityPlayer) sender, reloadMainSettings);
			}
		}

		@Override
		public int getMaxArgs() {
			return 1;
		}
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return super.getTabCompletions(server, sender, args, targetPos);
		} else if (args.length > 5 && args[0].equalsIgnoreCase("build")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "north", "east", "south", "west");
		} else if (args.length == 2 && args[0].equalsIgnoreCase("scannertp")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, ScannerTracker.getTrackedScannerNames().toArray(new String[0]));
		}
		return Collections.emptyList();
	}
}
