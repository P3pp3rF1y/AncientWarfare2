package net.shadowmage.ancientwarfare.npc.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentTranslation;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.gamedata.WorldData;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;

public class CommandDebugAI extends CommandBase {

	private int permissionLevel = 2;

	@Override
	public String getName() {
		return "awnpcdebug";
	}

	@Override
	public String getUsage(ICommandSender var1) {
		return "command.aw.npcdebug.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender var1, String[] var2) throws CommandException {
		AWNPCStatics.npcAIDebugMode = !AWNPCStatics.npcAIDebugMode;
		WorldData d = AWGameData.INSTANCE.getPerWorldData(var1.getEntityWorld(), WorldData.class);
		if (d == null) {
			throw new WrongUsageException("Couldn't find or build relevant data");
		}
		d.set("NpcAIDebugMode", AWNPCStatics.npcAIDebugMode);
		var1.sendMessage(new TextComponentTranslation("command.aw.npcdebug.used"));
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(this.permissionLevel, this.getName());
	}

	@Override
	public boolean isUsernameIndex(String[] var1, int var2) {
		return false;
	}

}
