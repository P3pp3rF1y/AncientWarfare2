package net.shadowmage.ancientwarfare.structure.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
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
import java.util.Locale;

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
        String cmd = var2[0];
        if (cmd.toLowerCase(Locale.ENGLISH).equals("delete")) {
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
                    boolean shouldDelete = var2[2].toLowerCase(Locale.ENGLISH).equals("true");
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
        } else if (cmd.toLowerCase(Locale.ENGLISH).equals("build")) {
            if (var2.length < 5) {
                throw new WrongUsageException(getUsage(sender));
            }

            int x = CommandBase.parseInt(var2[2]);
            int y = CommandBase.parseInt(var2[3]);
            int z = CommandBase.parseInt(var2[4]);
            int face = 0;
            if(var2.length>5) {
                String dl = var2[5].toLowerCase(Locale.ENGLISH);
                if (dl.equals("north")) {
                    face = 2;
                } else if (dl.equals("east")) {
                    face = 3;
                } else if (dl.equals("south")) {
                    face = 0;
                } else if (dl.equals("west")) {
                    face = 1;
                } else {
                    face = CommandBase.parseInt(var2[5]);
                }
            }
            StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(var2[1]);
            TextComponentTranslation txt;
            if (template == null) {
                txt = new TextComponentTranslation("command.aw.structure.not_found", var2[1]);
            } else {
                StructureBuilder builder = new StructureBuilder(sender.getEntityWorld(), template, face, x, y, z);
                builder.instantConstruction();
                txt = new TextComponentTranslation("command.aw.structure.built", var2[1], x, y, z);
            }
            sender.sendMessage(txt);
        }else if(cmd.toLowerCase(Locale.ENGLISH).equals("save")){
            if(sender instanceof EntityLivingBase){
                @Nonnull ItemStack stack = ((EntityLivingBase) sender).getHeldItemMainhand();
                if(!stack.isEmpty()){
                    ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
                    if(settings.hasPos1() && settings.hasPos2() && settings.hasBuildKey() && (settings.hasName() || var2.length>1)){
                        String name = settings.hasName() ? settings.name() : var2[1];
                        NBTTagCompound tagCompound = new NBTTagCompound();
                        if(ItemStructureScanner.scanStructure(sender.getEntityWorld(), settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, true, tagCompound)) {
                            sender.sendMessage(new TextComponentTranslation("command.aw.structure.exported", var2[1]));
                        }
                    }else{
                        sender.sendMessage(new TextComponentTranslation("command.aw.structure.incomplete_data"));
                    }
                }
            }
        }
    }

    private boolean deleteTemplateFile(String name) {
        String path = TemplateLoader.includeDirectory + name + "." + AWStructureStatics.templateExtension;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(args, "build", "delete", "save");
        }else if(args.length > 5 && args[0].toLowerCase(Locale.ENGLISH).equals("build")){
            return CommandBase.getListOfStringsMatchingLastWord(args, "north", "east", "south", "west");
        }
        return Collections.emptyList();
    }
}
