package net.shadowmage.ancientwarfare.npc.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

/**
 * client->server npc-command packet<br>
 * should simply contain the command type-id (enum.ordinal)<br>
 * as well as the target, either entity-id or block coordinates.
 *
 * @author Shadowmage
 */
public class PacketNpcCommand extends PacketBase {

    CommandType type;
    boolean blockTarget;
    int x, y, z;

    public PacketNpcCommand(CommandType type, Entity ent) {
        this.type = type;
        this.blockTarget = false;
        this.x = ent.getEntityId();
    }

    public PacketNpcCommand(CommandType type, int x, int y, int z) {
        this.type = type;
        this.blockTarget = true;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketNpcCommand() {
    }

    @Override
    protected void writeToStream(ByteBuf data) {
        data.writeInt(type.ordinal());
        data.writeBoolean(blockTarget);
        if (blockTarget) {
            data.writeInt(x);
            data.writeInt(y);
            data.writeInt(z);
        } else {
            data.writeInt(x);
        }
    }

    @Override
    protected void readFromStream(ByteBuf data) {
        this.type = CommandType.values()[data.readInt()];
        blockTarget = data.readBoolean();
        x = data.readInt();
        if (blockTarget) {
            y = data.readInt();
            z = data.readInt();
        }
    }

    @Override
    protected void execute() {
        NpcCommand.handleServerCommand(player, type, blockTarget, x, y, z);
    }

}
