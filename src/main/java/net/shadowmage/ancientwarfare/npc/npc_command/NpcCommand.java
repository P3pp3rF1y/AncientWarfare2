package net.shadowmage.ancientwarfare.npc.npc_command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
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
CLEAR_UPKEEP;
}

/**
 * client-side handle command. called from command baton key handler
 * @param cmd
 */
public static void handleCommandClient(CommandType type, MovingObjectPosition hit)
  {
  AWLog.logDebug("receiving client command...:"+type+" at: "+hit);
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
  AWLog.logDebug("receiving server-side command: "+type+" : "+block+" : "+x+","+y+","+z);
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
      ((NpcBase)e).setCurrentCommand(cmd);
      }
    }
  }

public static class Command
{
public final CommandType type;
public final int x, y, z;
public final boolean blockTarget;
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
}


}
