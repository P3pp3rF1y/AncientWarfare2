package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class CommandFaction extends CommandBase {

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getName() {
		return "awfaction";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "command.aw.faction.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender var1, String[] var2) throws CommandException {
		if (var2.length < 2) {
			throw new WrongUsageException(getUsage(var1));
		}
		String cmd = var2[0];
		String playerName = var2[1];
		if (cmd.equalsIgnoreCase("get")) {
			showFactionStandings(var1, playerName);
		}
	}

	private void showFactionStandings(ICommandSender var1, String playerName) {
		World world = var1.getEntityWorld();
		var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.player", playerName));
		for (String faction : FactionRegistry.getFactionNames()) {
			int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.value", faction, standing));
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "get");
		} else if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;
	}

}
