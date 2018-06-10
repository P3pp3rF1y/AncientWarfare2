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
			if (var2.length < 4) {
				throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
			}
			String faction = var2[2];
			if (FactionRegistry.getFactionNames().stream().noneMatch(f -> f.equalsIgnoreCase(faction))) {
				throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
			}
			String amount = var2[3];
			int amt = 0;
			try {
				amt = Integer.parseInt(amount);
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException(COMMAND_AW_FACTION_SET_USAGE_UNLOC);
			}
			FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.set", playerName, faction, amt));
		} else if (cmd.equalsIgnoreCase("setall")) {
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
		} else if (cmd.equalsIgnoreCase("get")) {
			World world = var1.getEntityWorld();
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.player", playerName));
			for (String faction : FactionRegistry.getFactionNames()) {
				int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
				var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.value", faction, standing));
			}
		}
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)//the command
		{
			return CommandBase.getListOfStringsMatchingLastWord(args, "set", "setall", "get");
		} else if (args.length == 2)//would be a player name
		{
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else if (args.length == 3)//would be a faction name for the set command
		{
			if (args[0].equalsIgnoreCase("set")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, FactionRegistry.getFactionNames());
			}
		} else if (args.length == 4)//would be a number for the set command value
		{

		}
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return var2 == 1;
	}

}
