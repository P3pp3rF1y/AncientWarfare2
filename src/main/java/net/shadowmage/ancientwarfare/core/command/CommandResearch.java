package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import javax.annotation.Nullable;
import java.util.List;

public class CommandResearch extends CommandBase {

    @Override
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    @Override
    public String getName() {
        return "awresearch";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "command.aw.research.usage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] commandParts) throws CommandException {
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
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] text, @Nullable BlockPos targetPos) {
        return text.length == 1 ? getListOfStringsMatchingLastWord(text, "add", "remove", "fill", "clear") : text.length == 2 ? getListOfStringsMatchingLastWord(text, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2) {
        return var2 == 1;//e.g. /awresearch add shadowmage45
    }

}
