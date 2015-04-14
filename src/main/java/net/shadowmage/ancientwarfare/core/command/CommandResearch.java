package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.List;

public class CommandResearch extends CommandBase {

    private int permissionLevel = 2;

    @Override
    public int compareTo(Object par1Obj) {
        return super.compareTo((ICommand) par1Obj);
    }

    @Override
    public String getCommandName() {
        return "awresearch";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "command.aw.research.usage";
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] commandParts) {
        if (commandParts == null || commandParts.length < 2) {
            throw new WrongUsageException("command.aw.research.usage");
        }
        if ("add".equals(commandParts[0])) {
            if (commandParts.length < 3) {
                throw new WrongUsageException("command.aw.research.usage");
            }
            String goal = commandParts[2];
            if (!goal.startsWith("research.")) {
                goal = "research." + goal;
            }
            ResearchGoal rGoal = ResearchGoal.getGoal(goal);
            if (rGoal == null) {
                throw new WrongUsageException("command.aw.research.usage");
            }
            ResearchTracker.INSTANCE.addResearchFromNotes(sender.getEntityWorld(), commandParts[1], rGoal.getId());
        } else if ("remove".equals(commandParts[0])) {
            if (commandParts.length < 3) {
                throw new WrongUsageException("command.aw.research.usage");
            }
            String goal = commandParts[2];
            if (!goal.startsWith("research.")) {
                goal = "research." + goal;
            }
            ResearchGoal rGoal = ResearchGoal.getGoal(goal);
            if (rGoal == null) {
                throw new WrongUsageException("command.aw.research.usage");
            }
            ResearchTracker.INSTANCE.removeResearch(sender.getEntityWorld(), commandParts[1], rGoal.getId());
        } else if ("fill".equals(commandParts[0])) {
            ResearchTracker.INSTANCE.fillResearch(sender.getEntityWorld(), commandParts[1]);
        } else if ("clear".equals(commandParts[0])) {
            ResearchTracker.INSTANCE.clearResearch(sender.getEntityWorld(), commandParts[1]);
        } else {
            throw new WrongUsageException("command.aw.research.usage");
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] text) {
        return text.length == 1 ? getListOfStringsMatchingLastWord(text, "add", "remove", "fill", "clear") : null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {
        return var2 == 1;//e.g. /awresearch add shadowmage45
    }

}
