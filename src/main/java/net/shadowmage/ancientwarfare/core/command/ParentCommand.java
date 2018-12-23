package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ParentCommand extends CommandBase {
	private Map<String, ISubCommand> subCommands = new HashMap<>();

	protected void registerSubCommand(ISubCommand subCommand) {
		subCommands.put(subCommand.getName().toLowerCase(), subCommand);
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length == 0 || !subCommands.containsKey(args[0].toLowerCase())) {
			throw new WrongUsageException(getUsage(sender));
		}

		String[] subArgs = new String[args.length - 1];
		System.arraycopy(args, 1, subArgs, 0, args.length - 1);
		ISubCommand subCommand = subCommands.get(args[0].toLowerCase());
		if (subArgs.length > subCommand.getMaxArgs()) {
			throw new WrongUsageException(getUsage(sender));
		}
		subCommand.execute(server, sender, subArgs);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, subCommands.values().stream().map(ISubCommand::getName).toArray(String[]::new));
		}
		return super.getTabCompletions(server, sender, args, targetPos);
	}
}
