package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

/*
 * client->server npc-command packet<br>
 * should simply contain the command type-id (enum.ordinal)<br>
 * as well as the target, either entity-id or block coordinates.
 *
 * @author Shadowmage
 */
public class PacketNpcCommand extends PacketBase {

	private CommandType type;
	private boolean blockTarget;
	private BlockPos pos;
	private int entityID;

	public PacketNpcCommand(CommandType type, Entity ent) {
		this.type = type;
		this.blockTarget = false;
		this.entityID = ent.getEntityId();
	}

	public PacketNpcCommand(CommandType type, BlockPos pos) {
		this.type = type;
		this.blockTarget = true;
		this.pos = pos;
	}

	public PacketNpcCommand() {
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		data.writeInt(type.ordinal());
		data.writeBoolean(blockTarget);
		if (blockTarget) {
			data.writeLong(pos.toLong());
		} else {
			data.writeInt(entityID);
		}
	}

	@Override
	protected void readFromStream(ByteBuf data) {
		this.type = CommandType.values()[data.readInt()];
		blockTarget = data.readBoolean();
		if (blockTarget) {
			pos = BlockPos.fromLong(data.readLong());
		} else {
			entityID = data.readInt();
		}
	}

	@Override
	protected void execute(EntityPlayer player) {
		NpcCommand.handleServerCommand(player, type, blockTarget, pos, entityID);
	}

}
