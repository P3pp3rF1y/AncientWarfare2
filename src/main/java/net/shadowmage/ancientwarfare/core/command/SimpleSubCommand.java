package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class SimpleSubCommand implements ISubCommand {
	private String name;
	private ISubCommandExecutor executor;

	public SimpleSubCommand(String name, ISubCommandExecutor executor) {
		this.name = name;
		this.executor = executor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		executor.execute(server, sender, args);
	}

	public interface ISubCommandExecutor {
		void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
	}

	@Override
	public int getMaxArgs() {
		return 0;
	}

	@Override
	public int getMinArgs() {
		return getMaxArgs();
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return getName();
	}
}
