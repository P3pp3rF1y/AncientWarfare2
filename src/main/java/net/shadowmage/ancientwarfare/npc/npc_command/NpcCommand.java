package net.shadowmage.ancientwarfare.npc.npc_command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;

public class NpcCommand
{

public static enum CommandType
{
MOVE,
ATTACK,//attack click on entity
ATTACK_AREA,//attack click on block
GUARD,//attack click on friendly player or npc
SET_HOME,
SET_UPKEEP,
CLEAR_HOME,
CLEAR_UPKEEP,
CLEAR_COMMAND;
}

/**
 * client-side handle command. called from command baton key handler
 * @param cmd
 */
public static void handleCommandClient(CommandType type, MovingObjectPosition hit)
  {
  if(hit!=null && hit.typeOfHit!=MovingObjectType.MISS)
    {
    if(hit.typeOfHit==MovingObjectType.ENTITY && hit.entityHit!=null)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.entityHit);
      NetworkHandler.sendToServer(pkt);
      }
    else if(hit.typeOfHit==MovingObjectType.BLOCK)
      {
      PacketNpcCommand pkt = new PacketNpcCommand(type, hit.blockX, hit.blockY, hit.blockZ);
      NetworkHandler.sendToServer(pkt);
      }    
    }
  }

/**
 * server side handle command. called from packet triggered from client key input while baton is equipped
 */
public static void handleServerCommand(EntityPlayer player, CommandType type, boolean block, int x, int y, int z)
  {
  Command cmd = null;
  if(block)
    {
    cmd = new Command(type, x, y, z);
    }
  else
    {
    cmd = new Command(type, x);
    }
  List<Entity> targets = new ArrayList<Entity>();
  ItemCommandBaton.getCommandedEntities(player.worldObj, player.getCurrentEquippedItem(), targets);
  for(Entity e : targets)
    {
    if(e instanceof NpcBase)
      {
      ((NpcBase)e).handlePlayerCommand(cmd);
      }
    }
  }

public static final class Command
{
public CommandType type;
public int x, y, z;
public boolean blockTarget;

UUID entityID;
Entity entity;

public Command(){}

public Command(NBTTagCompound tag)
  {
  readFromNBT(tag);
  }

public Command(CommandType type, int x, int y, int z)
  {
  this.type = type;
  this.x = x;
  this.y = y;
  this.z = z;
  this.blockTarget = true;  
  }

public Command(CommandType type, int entityID)
  {
  this.type = type;
  this.x = entityID;
  this.y=this.z=0;
  this.blockTarget = false;
  }

public Command copy()
  {
  Command cmd = new Command();
  cmd.type = this.type;
  cmd.x=this.x;
  cmd.y=this.y;
  cmd.z=this.z;
  cmd.entity=this.entity;
  cmd.entityID=this.entityID;
  cmd.blockTarget=this.blockTarget;
  return cmd;
  }

public final void readFromNBT(NBTTagCompound tag)
  {
  type = CommandType.values()[tag.getInteger("type")];
  blockTarget = tag.getBoolean("block");
  x = tag.getInteger("x");
  y = tag.getInteger("y");
  z = tag.getInteger("z");
  if(tag.hasKey("idmsb") && tag.hasKey("idlsb"))
    {
    entityID = new UUID(tag.getLong("idmsb"), tag.getLong("idlsb"));
    }
  }

public final NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("type", type.ordinal());
  tag.setBoolean("block", blockTarget);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  if(entityID!=null)
    {
    tag.setLong("idmsb", entityID.getMostSignificantBits());
    tag.setLong("idlsb", entityID.getLeastSignificantBits());
    }
  return tag;
  }

/**
 * should be called by packet prior to passing command into npc processing
 */
public void findEntity(World world)
  {
  if(blockTarget){return;}
  if(entity!=null){return;}
  if(entityID==null)
    {
    entity = world.getEntityByID(x);
    if(entity!=null)
      {
      entityID = entity.getPersistentID();
      }
    }
  else
    {
    entity = WorldTools.getEntityByUUID(world, entityID.getMostSignificantBits(), entityID.getLeastSignificantBits());
    }
  }

public Entity getEntityTarget(World world)
  {
  if(blockTarget){return null;}
  if(entity!=null)
    {
    return entity;
    }
  else
    {
    findEntity(world);    
    } 
  return entity;
  }

}


}
