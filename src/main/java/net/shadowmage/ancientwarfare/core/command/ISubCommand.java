package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface ISubCommand {
	String getName();

	void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;

	int getMaxArgs();
}
