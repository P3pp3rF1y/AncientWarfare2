package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandResearch extends CommandBase {

	private static final String COMMAND_AW_RESEARCH_USAGE = "command.aw.research.usage";

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "awresearch";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return COMMAND_AW_RESEARCH_USAGE;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] commandParts) throws CommandException {
		if (commandParts.length < 2) {
			throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
		}
		if ("add".equals(commandParts[0])) {
			if (commandParts.length < 3) {
				throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
			}
			String goal = commandParts[2];
			if (!ResearchRegistry.researchExists(goal)) {
				throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
			}
			ResearchTracker.INSTANCE.addResearchFromNotes(sender.getEntityWorld(), commandParts[1], goal);
		} else if ("remove".equals(commandParts[0])) {
			if (commandParts.length < 3) {
				throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
			}
			String goal = commandParts[2];
			if (!ResearchRegistry.researchExists(goal)) {
				throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
			}
			ResearchTracker.INSTANCE.removeResearch(sender.getEntityWorld(), commandParts[1], goal);
		} else if ("fill".equals(commandParts[0])) {
			ResearchTracker.INSTANCE.fillResearch(sender.getEntityWorld(), commandParts[1]);
		} else if ("clear".equals(commandParts[0])) {
			ResearchTracker.INSTANCE.clearResearch(sender.getEntityWorld(), commandParts[1]);
		} else {
			throw new WrongUsageException(COMMAND_AW_RESEARCH_USAGE);
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] text, @Nullable BlockPos targetPos) {
		if (text.length == 1) {
			return getListOfStringsMatchingLastWord(text, "add", "remove", "fill", "clear");
		} else if (text.length == 2) {
			return getListOfStringsMatchingLastWord(text, server.getOnlinePlayerNames());
		} else if (text.length == 3 && (text[0].equals("add") || text[0].equals("remove"))) {
			return getListOfStringsMatchingLastWord(text, ResearchRegistry.getAllResearchGoals().stream().map(ResearchGoal::getName).toArray(String[]::new));
		}

		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;//e.g. /awresearch add shadowmage45
	}

}
