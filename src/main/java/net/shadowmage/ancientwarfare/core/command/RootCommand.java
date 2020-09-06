package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.List;

public abstract class RootCommand extends CommandBase {
	private final ParentCommand delegate = new ParentCommand(true) {
		@Override
		public String getName() {
			return RootCommand.this.getName();
		}
	};

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		delegate.execute(server, sender, args);
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return delegate.getTabCompletions(server, sender, args, targetPos);
	}

	protected void registerSubCommand(ISubCommand subCommand) {
		delegate.registerSubCommand(subCommand);
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return delegate.getUsage(sender);
	}
}
