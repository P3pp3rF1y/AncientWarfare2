package net.shadowmage.ancientwarfare.structure.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilder;
import net.shadowmage.ancientwarfare.structure.template.load.TemplateLoader;

import java.io.File;
import java.util.List;

public class CommandStructure implements ICommand {

    private int permissionLevel = 2;

    public CommandStructure() {
    }

    public int compareTo(ICommand par1ICommand) {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand) par1Obj);
    }

    @Override
    public String getCommandName() {
        return "awstructure";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "command.aw.structure.usage";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        if (var2.length == 0) {
            throw new WrongUsageException(getCommandUsage(var1));
        }
        String cmd = var2[0];
        if (cmd.toLowerCase().equals("delete")) {
            if (var2.length < 2) {
                throw new WrongUsageException(getCommandUsage(var1));
            }
            String name = var2[1];
            boolean flag = StructureTemplateManager.INSTANCE.removeTemplate(name);
            if (flag)//check if var2.len>=3, pull string of end...if string==true, try delete template file for name
            {
                ChatComponentTranslation txt = new ChatComponentTranslation("command.aw.structure.template_removed", name);
                var1.addChatMessage(txt);
                if (var2.length >= 3) {
                    boolean shouldDelete = var2[2].toLowerCase().equals("true");
                    if (shouldDelete) {
                        if (deleteTemplateFile(name)) {
                            txt = new ChatComponentTranslation("command.aw.structure.file_deleted", name);
                        } else {
                            txt = new ChatComponentTranslation("command.aw.structure.file_not_found", name);
                        }
                        var1.addChatMessage(txt);
                    }
                }
            } else//send template not found message
            {
                var1.addChatMessage(new ChatComponentTranslation("command.aw.structure.not_found", name));
            }
        } else if (cmd.toLowerCase().equals("build")) {
            if (var2.length < 6) {
                throw new WrongUsageException(getCommandUsage(var1));
            }
            String name = var2[1];
            String xs = var2[2];
            String ys = var2[3];
            String zs = var2[4];
            String direction = var2[5];

            int x = CommandBase.parseInt(var1, xs);
            int y = CommandBase.parseInt(var1, ys);
            int z = CommandBase.parseInt(var1, zs);
            int face = 0;
            String dl = direction.toLowerCase();
            if (dl.equals("north")) {
                face = 2;
            } else if (dl.equals("east")) {
                face = 3;
            } else if (dl.equals("south")) {
                face = 0;
            } else if (dl.equals("west")) {
                face = 1;
            } else {
                face = CommandBase.parseInt(var1, direction);
            }
            StructureTemplate template = StructureTemplateManager.INSTANCE.getTemplate(name);
            if (template == null) {
                ChatComponentTranslation txt = new ChatComponentTranslation("command.aw.structure.not_found", name);
                var1.addChatMessage(txt);
            } else {
                StructureBuilder builder = new StructureBuilder(var1.getEntityWorld(), template, face, x, y, z);
                builder.instantConstruction();
                ChatComponentTranslation txt = new ChatComponentTranslation("command.aw.structure.built", name, x, y, z);
                var1.addChatMessage(txt);
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
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        if (var2.length == 1) {
            return CommandBase.getListOfStringsMatchingLastWord(var2, "build", "delete");
        }
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {
        return false;
    }

}
