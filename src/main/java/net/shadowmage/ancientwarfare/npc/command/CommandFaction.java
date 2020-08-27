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
	private static final String COMMAND_AW_FACTION_SET_USAGE_UNLOC = "command.aw.faction.set.usage";

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
		if (cmd.equalsIgnoreCase("set")) {
			setFactionStanding(var1, var2, playerName);
		} else if (cmd.equalsIgnoreCase("setall")) {
			setAllFactionStandings(var1, var2, playerName);
		} else if (cmd.equalsIgnoreCase("get")) {
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

	private void setAllFactionStandings(ICommandSender var1, String[] var2, String playerName) throws WrongUsageException {
		if (var2.length < 3) {
			throw new WrongUsageException("command.aw.faction.setall.usage");
		}
		String amount = var2[2];
		int amt;
		try {
			amt = Integer.parseInt(amount);
		}
		catch (NumberFormatException e) {
			throw new WrongUsageException("command.aw.faction.setall.usage");
		}
		for (String faction : FactionRegistry.getFactionNames()) {
			FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.set", playerName, faction, amt));
		}
	}

	private void setFactionStanding(ICommandSender var1, String[] var2, String playerName) throws WrongUsageException {
		if (var2.length < 4) {
			throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
		}
		String faction = var2[2];
		if (FactionRegistry.getFactionNames().stream().noneMatch(f -> f.equalsIgnoreCase(faction))) {
			throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
		}
		String amount = var2[3];
		int amt;
		try {
			amt = Integer.parseInt(amount);
		}
		catch (NumberFormatException e) {
			throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
		}
		FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
		var1.sendMessage(new TextComponentTranslation("command.aw.faction.set", playerName, faction, amt));
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1) {
			return CommandBase.getListOfStringsMatchingLastWord(args, "set", "setall", "get");
		} else if (args.length == 2) {
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
			return CommandBase.getListOfStringsMatchingLastWord(args, FactionRegistry.getFactionNames());
		}
		return Collections.emptyList();
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;
	}

}
