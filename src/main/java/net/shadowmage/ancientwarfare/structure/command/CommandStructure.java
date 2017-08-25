package net.shadowmage.ancientwarfare.structure.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class CommandStructure extends CommandBase {

    @Override
    public String getCommandName() {
        return "awstructure";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "command.aw.structure.usage";
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if (var2.length == 0) {
            throw new WrongUsageException(getCommandUsage(var1));
        }
        String cmd = var2[0];
        if (cmd.toLowerCase(Locale.ENGLISH).equals("delete")) {
            if (var2.length < 2) {
                throw new WrongUsageException(getCommandUsage(var1));
            }
            String name = var2[1];
            boolean flag = StructureTemplateManager.INSTANCE.removeTemplate(name);
            if (flag)//check if var2.len>=3, pull string of end...if string==true, try delete template file for name
            {
                TextComponentTranslation txt = new TextComponentTranslation("command.aw.structure.template_removed", name);
                var1.sendMessage(txt);
                if (var2.length >= 3) {
                    boolean shouldDelete = var2[2].toLowerCase(Locale.ENGLISH).equals("true");
                    if (shouldDelete) {
                        if (deleteTemplateFile(name)) {
                            txt = new TextComponentTranslation("command.aw.structure.file_deleted", name);
                        } else {
                            txt = new TextComponentTranslation("command.aw.structure.file_not_found", name);
                        }
                        var1.sendMessage(txt);
                    }
                }
            } else//send template not found message
            {
                var1.sendMessage(new TextComponentTranslation("command.aw.structure.not_found", name));
            }
        } else if (cmd.toLowerCase(Locale.ENGLISH).equals("build")) {
            if (var2.length < 5) {
                throw new WrongUsageException(getCommandUsage(var1));
            }

            int x = CommandBase.parseInt(var1, var2[2]);
            int y = CommandBase.parseInt(var1, var2[3]);
            int z = CommandBase.parseInt(var1, var2[4]);
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
                    face = CommandBase.parseInt(var1, var2[5]);
                }
            }
            StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(var2[1]);
            TextComponentTranslation txt;
            if (template == null) {
                txt = new TextComponentTranslation("command.aw.structure.not_found", var2[1]);
            } else {
                StructureBuilder builder = new StructureBuilder(var1.getEntityWorld(), template, face, x, y, z);
                builder.instantConstruction();
                txt = new TextComponentTranslation("command.aw.structure.built", var2[1], x, y, z);
            }
            var1.sendMessage(txt);
        }else if(cmd.toLowerCase(Locale.ENGLISH).equals("save")){
            if(var1 instanceof EntityLivingBase){
                @Nonnull ItemStack stack = ((EntityLivingBase) var1).getHeldItem();
                if(stack!=null){
                    ItemStructureSettings settings = ItemStructureSettings.getSettingsFor(stack);
                    if(settings.hasPos1() && settings.hasPos2() && settings.hasBuildKey() && (settings.hasName() || var2.length>1)){
                        String name = settings.hasName() ? settings.name() : var2[1];
                        NBTTagCompound tagCompound = new NBTTagCompound();
                        if(ItemStructureScanner.scanStructure(var1.getEntityWorld(), settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, true, tagCompound)) {
                            var1.sendMessage(new TextComponentTranslation("command.aw.structure.exported", var2[1]));
                        }
                    }else{
                        var1.sendMessage(new TextComponentTranslation("command.aw.structure.incomplete_data"));
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

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        if (var2.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(var2, "build", "delete", "save");
        }else if(var2.length > 5 && var2[0].toLowerCase(Locale.ENGLISH).equals("build")){
            return CommandBase.getListOfStringsMatchingLastWord(var2, "north", "east", "south", "west");
        }
        return null;
    }
}
