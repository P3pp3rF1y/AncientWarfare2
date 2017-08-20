package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

import java.util.List;

public class CommandDebugAI extends CommandBase {

    private int permissionLevel = 2;

    @Override
    public int compareTo(Object par1Obj) {
        return super.compareTo((ICommand) par1Obj);
    }

    @Override
    public String getCommandName() {
        return "awnpcdebug";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return "command.aw.npcdebug.usage";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender var1, String[] var2) {
        AWNPCStatics.npcAIDebugMode = !AWNPCStatics.npcAIDebugMode;
        WorldData d = AWGameData.INSTANCE.getPerWorldData(var1.getEntityWorld(), WorldData.class);
        if (d == null) {
            throw new WrongUsageException("Couldn't find or build relevant data");
        }
        d.set("NpcAIDebugMode", AWNPCStatics.npcAIDebugMode);
        var1.sendMessage(new TextComponentTranslation("command.aw.npcdebug.used"));
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {
        return false;
    }

}
