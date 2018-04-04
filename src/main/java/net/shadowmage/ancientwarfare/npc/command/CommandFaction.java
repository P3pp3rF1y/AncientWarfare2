package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.faction.FactionTracker;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;

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
		if (cmd.toLowerCase(Locale.ENGLISH).equals("set")) {
			if (var2.length < 4) {
				throw new WrongUsageException("command.aw.faction.set.usage");
			}
			String faction = var2[2];
			if (!isFactionNameValid(faction)) {
				throw new WrongUsageException("command.aw.faction.set.usage");
			}
			String amount = var2[3];
			int amt = 0;
			try {
				amt = Integer.parseInt(amount);
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException("command.aw.faction.set.usage");
			}
			FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.set", playerName, faction, amt));
		} else if (cmd.toLowerCase(Locale.ENGLISH).equals("setall")) {
			if (var2.length < 3) {
				throw new WrongUsageException("command.aw.faction.setall.usage");
			}
			String amount = var2[2];
			int amt = 0;
			try {
				amt = Integer.parseInt(amount);
			}
			catch (NumberFormatException e) {
				throw new WrongUsageException("command.aw.faction.setall.usage");
			}
			for (String faction : AWNPCStatics.factionNames) {
				FactionTracker.INSTANCE.setStandingFor(var1.getEntityWorld(), playerName, faction, amt);
				var1.sendMessage(new TextComponentTranslation("command.aw.faction.set", playerName, faction, amt));
			}
		} else if (cmd.toLowerCase(Locale.ENGLISH).equals("get")) {
			World world = var1.getEntityWorld();
			var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.player", playerName));
			for (String faction : AWNPCStatics.factionNames) {
				int standing = FactionTracker.INSTANCE.getStandingFor(world, playerName, faction);
				var1.sendMessage(new TextComponentTranslation("command.aw.faction.status.value", faction, standing));
			}
		}
	}

	private boolean isFactionNameValid(String factionName) {
		for (String name : AWNPCStatics.factionNames) {
			if (name.equalsIgnoreCase(factionName)) {
				return true;
			}
		}
		return false;
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
			if (args[0].toLowerCase(Locale.ENGLISH).equals("set")) {
				return CommandBase.getListOfStringsMatchingLastWord(args, AWNPCStatics.factionNames);
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
