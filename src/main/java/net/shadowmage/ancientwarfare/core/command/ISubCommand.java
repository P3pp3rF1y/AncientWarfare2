package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface ISubCommand {
	String getName();

	void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

	int getMaxArgs();

	default int getMinArgs() {
		return 0;
	}

	default List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return Collections.emptyList();
	}

	String getUsage(ICommandSender sender);
}
