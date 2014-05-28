package net.shadowmage.ancientwarfare.npc.npc_command;

import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.network.PacketNpcCommand;

public class NpcCommand
{

public static enum CommandType
{
MOVE(true),
ATTACK(false),//attack click on entity
ATTACK_AREA(true),//attack click on block
GUARD(false),//attack click on friendly player or npc
SET_HOME(true),
SET_UPKEEP(true),
CLEAR_HOME(false),
CLEAR_UPKEEP(false);
private CommandType(boolean blockData){this.blockData=blockData;}
boolean blockData;
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
public static void handleServerCommand(World world, CommandType type, boolean block, int x, int y, int z)
  {
  AWLog.logDebug("receiving server-side command: "+type+" : "+block+" : "+x+","+y+","+z);
  }


public abstract static class Command
{
private CommandType commandID;
private Command(CommandType id){this.commandID=id;}
}

public static class CommandBlockBased extends Command
{
int targetX, targetY, targetZ;
private CommandBlockBased(CommandType id, int x, int y, int z)
  {
  super(id);
  targetX=x;
  targetY=y;
  targetZ=z;
  }
}

public static class CommandMove extends CommandBlockBased
{
public CommandMove(int x, int y, int z)
  {
  super(CommandType.MOVE, x,y,z);
  }
}

public static class CommandAttackArea extends CommandBlockBased
{
public CommandAttackArea(int x, int y, int z)
  {
  super(CommandType.ATTACK_AREA,x,y,z);
  }
}

public static class CommandSetHome extends CommandBlockBased
{
public CommandSetHome(int x, int y, int z)
  {
  super(CommandType.SET_HOME,x,y,z);
  }
}

public static class CommandSetUpkeep extends CommandBlockBased
{
public CommandSetUpkeep(int x, int y, int z)
  {
  super(CommandType.SET_UPKEEP, x,y,z);
  }
}

public static class CommandAttack extends Command
{
Entity target;
public CommandAttack(Entity target)
  {
  super(CommandType.ATTACK);
  this.target=target;
  }
}

public static class CommandGuard extends Command
{
Entity target;
public CommandGuard(Entity target)
  {
  super(CommandType.GUARD);
  this.target=target;
  }
}

}
