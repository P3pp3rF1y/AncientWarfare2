package net.shadowmage.ancientwarfare.npc.npc_command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;

import java.util.List;
import java.util.UUID;

public class NpcCommand {

    public static enum CommandType {
        MOVE,
        ATTACK,//attack click on entity
        ATTACK_AREA,//attack click on block
        GUARD,//attack click on friendly player or npc
        SET_HOME,
        SET_UPKEEP,
        CLEAR_HOME,
        CLEAR_UPKEEP,
        CLEAR_COMMAND;

        public boolean isPersistent() {
            return (this == ATTACK || this == GUARD || this == ATTACK_AREA);
        }
    }

    /*
     * client-side handle command. called from command baton key handler
     */
    public static void handleCommandClient(CommandType type, RayTraceResult hit) {
        if (hit != null && hit.typeOfHit != RayTraceResult.Type.MISS) {
            if (hit.typeOfHit == RayTraceResult.Type.ENTITY && hit.entityHit != null) {
                NetworkHandler.sendToServer(new PacketNpcCommand(type, hit.entityHit));
            } else if (hit.typeOfHit == RayTraceResult.Type.BLOCK) {
                NetworkHandler.sendToServer(new PacketNpcCommand(type, hit.getBlockPos()));
            }
        }
    }

    /*
     * server side handle command. called from packet triggered from client key input while baton is equipped
     */
    public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, BlockPos pos, int entityID) {
        Command cmd;
        if (block) {
            cmd = new Command(type, pos);
        } else {
            cmd = new Command(type, entityID);
        }
        List<Entity> targets = ItemCommandBaton.getCommandedEntities(player.world, EntityTools.getItemFromEitherHand(player, ItemCommandBaton.class));
        for (Entity e : targets) {
            if (e instanceof NpcPlayerOwned) {
                ((NpcPlayerOwned) e).handlePlayerCommand(cmd);
            }
        }
    }

    public static final class Command {
        public CommandType type;
		public BlockPos pos = BlockPos.ORIGIN;
		public boolean blockTarget;

        UUID entityUUID;
        int entityID;
        Entity entity;

        public Command() {
        }

        public Command(NBTTagCompound tag) {
            readFromNBT(tag);
        }

        public Command(CommandType type, BlockPos pos) {
            this.type = type;
            this.pos = pos;
            this.blockTarget = true;
        }

        public Command(CommandType type, int entityID) {
            this.type = type;
            this.entityID = entityID;
            this.blockTarget = false;
        }

        public Command copy() {
            Command cmd = new Command();
            cmd.type = this.type;
            cmd.pos = this.pos;
            cmd.entity = this.entity;
            cmd.entityID = this.entityID;
            cmd.entityUUID = this.entityUUID;
            cmd.blockTarget = this.blockTarget;
            return cmd;
        }

        public final void readFromNBT(NBTTagCompound tag) {
            type = CommandType.values()[tag.getInteger("type")];
            blockTarget = tag.getBoolean("block");
            pos = BlockPos.fromLong(tag.getLong("pos"));
            if (tag.hasKey("idmsb") && tag.hasKey("idlsb")) {
                entityUUID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
            }
            entityID = tag.getInteger("entityid");
        }

        public final NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setInteger("type", type.ordinal());
            tag.setBoolean("block", blockTarget);
            tag.setLong("pos", pos.toLong());
            if (entityUUID != null) {
                tag.setLong("idmsb", entityUUID.getMostSignificantBits());
                tag.setLong("idlsb", entityUUID.getLeastSignificantBits());
            }
            tag.setInteger("entityid", entityID);
            return tag;
        }

        /*
         * should be called by packet prior to passing command into npc processing
         */
        public void findEntity(World world) {
            if (blockTarget) {
                return;
            }
            if (entity != null) {
                return;
            }
            if (entityUUID == null) {
                entity = world.getEntityByID(entityID);
                if (entity != null) {
                    entityUUID = entity.getPersistentID();
                }
            } else {
                entity = world.getPlayerEntityByUUID(entityUUID);
            }
        }

        public Entity getEntityTarget(World world) {
            if (blockTarget) {
                return null;
            }
            if (entity != null) {
                return entity;
            } else {
                findEntity(world);
            }
            return entity;
        }

    }


}
