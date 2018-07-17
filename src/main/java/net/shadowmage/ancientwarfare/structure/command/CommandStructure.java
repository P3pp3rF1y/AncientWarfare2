package net.shadowmage.ancientwarfare.structure.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class CommandStructure extends CommandBase {

	@Override
	public String getName() {
		return "awstructure";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "command.aw.structure.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] var2) throws CommandException {
		if (var2.length == 0) {
			throw new WrongUsageException(getUsage(sender));
		}
		String cmd = var2[0].toLowerCase();
		switch (cmd) {
			case "delete":
				delete(sender, var2);
				break;
			case "build":
				build(sender, var2);
				break;
			case "save":
				save(sender, var2);
				break;
			case "reload":
				TemplateLoader.INSTANCE.reloadAll();
				sender.sendMessage(new TextComponentTranslation("command.aw.structure.reloaded"));
		}
	}

	private void save(ICommandSender sender, String[] var2) {
		if (sender instanceof EntityLivingBase) {
			@Nonnull ItemStack stack = ((EntityLivingBase) sender).getHeldItemMainhand();
			if (!stack.isEmpty()) {
				ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
				if (settings.hasPos1() && settings.hasPos2() && settings.hasBuildKey() && (settings.hasName() || var2.length > 1)) {
					String name = settings.hasName() ? settings.name() : var2[1];
					ItemStructureScanner.setStructureName(stack, name);
					if (ItemStructureScanner.scanStructure(sender.getEntityWorld(), stack)) {
						sender.sendMessage(new TextComponentTranslation("command.aw.structure.exported", var2[1]));
					}
				} else {
					sender.sendMessage(new TextComponentTranslation("command.aw.structure.incomplete_data"));
				}
			}
		}
	}

	private void build(ICommandSender sender, String[] var2) throws WrongUsageException, NumberInvalidException {
		if (var2.length < 5) {
			throw new WrongUsageException(getUsage(sender));
		}

		int x = CommandBase.parseInt(var2[2]);
		int y = CommandBase.parseInt(var2[3]);
		int z = CommandBase.parseInt(var2[4]);
		EnumFacing face = EnumFacing.SOUTH;
		if (var2.length > 5) {
			face = EnumFacing.byName(var2[5]);
		}
		StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(var2[1]);
		TextComponentTranslation txt;
		if (template == null) {
			txt = new TextComponentTranslation("command.aw.structure.not_found", var2[1]);
		} else {
			StructureBuilder builder = new StructureBuilder(sender.getEntityWorld(), template, face, new BlockPos(x, y, z));
			builder.instantConstruction();
			txt = new TextComponentTranslation("command.aw.structure.built", var2[1], x, y, z);
		}
		sender.sendMessage(txt);
	}

	private void delete(ICommandSender sender, String[] var2) throws WrongUsageException {
		if (var2.length < 2) {
			throw new WrongUsageException(getUsage(sender));
		}
		String name = var2[1];
		boolean flag = StructureTemplateManager.INSTANCE.removeTemplate(name);
		if (flag)//check if var2.len>=3, pull string of end...if string==true, try delete template file for name
		{
			TextComponentTranslation txt = new TextComponentTranslation("command.aw.structure.template_removed", name);
			sender.sendMessage(txt);
			if (var2.length >= 3) {
				boolean shouldDelete = var2[2].equalsIgnoreCase("true");
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
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "build", "delete", "save");
		} else if (args.length > 5 && args[0].equalsIgnoreCase("build")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "north", "east", "south", "west");
		}
		return Collections.emptyList();
	}
}
