package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.List;

public class CommandResearch implements ICommand {


    private int permissionLevel = 2;

    public int compareTo(ICommand par1ICommand) {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj) {
        return this.compareTo((ICommand) par1Obj);
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
            throw new WrongUsageException("command.aw.research.usage", new Object[]{});
        }
        String operation = commandParts[0];
        String target = commandParts[1];
        if (operation.equals("add")) {
            if (commandParts.length < 3) {
                throw new WrongUsageException("command.aw.research.usage", new Object[]{});
            }
            String goal = commandParts[2];
            if (!goal.startsWith("research.")) {
                goal = "research." + goal;
            }
            ResearchGoal rGoal = ResearchGoal.getGoal(goal);
            if (rGoal == null) {
                throw new WrongUsageException("command.aw.research.usage", new Object[]{});
            }
            ResearchTracker.instance().addResearchFromNotes(sender.getEntityWorld(), target, rGoal.getId());
        } else if (operation.equals("remove")) {
            if (commandParts.length < 3) {
                throw new WrongUsageException("command.aw.research.usage", new Object[]{});
            }
            String goal = commandParts[2];
            if (!goal.startsWith("research.")) {
                goal = "research." + goal;
            }
            ResearchGoal rGoal = ResearchGoal.getGoal(goal);
            if (rGoal == null) {
                throw new WrongUsageException("command.aw.research.usage", new Object[]{});
            }
            ResearchTracker.instance().removeResearch(sender.getEntityWorld(), target, rGoal.getId());
        } else if (operation.equals("fill")) {
            ResearchTracker.instance().fillResearch(sender.getEntityWorld(), target);
        } else if (operation.equals("clear")) {
            ResearchTracker.instance().clearResearch(sender.getEntityWorld(), target);
        } else {
            throw new WrongUsageException("command.aw.research.usage", new Object[]{});
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender.canCommandSenderUseCommand(this.permissionLevel, this.getCommandName());
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {
        return var2 == 1;//e.g. /awresearch add shadowmage45
    }

}
