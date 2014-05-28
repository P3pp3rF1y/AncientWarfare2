package net.shadowmage.ancientwarfare.npc.npc_command;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class NpcCommand
{

public static final NpcCommand INSTANCE = new NpcCommand();

private List<Integer> commandedNpcs = new ArrayList<Integer>();
//private HashMap<String, List<Integer>> commandedNPCs = new HashMap<String, List<Integer>>();

public void onClientConnectToServer(){commandedNpcs.clear();}//TODO handle this

public void onNpcClicked(int entityID)
  {
  Integer val = Integer.valueOf(entityID);
  if(commandedNpcs.contains(val))
    {
    commandedNpcs.remove(val);
    }
  else
    {
    commandedNpcs.add(val);
    }
  }

public void addCommandedNpc(int entityID)
  {
  commandedNpcs.add(Integer.valueOf(entityID));  
  }

public void removeCommandedNpc(int entityID)
  {
  commandedNpcs.remove(Integer.valueOf(entityID));
  }

public void clearCommandedNpcs()
  {
  commandedNpcs.clear();
  }

public List<Integer> getCommandedNpcs()
  {
  return commandedNpcs;
  }

/**
 * client-side handle command. called from command baton key handler
 * @param cmd
 */
public void handleCommandClient(Command cmd)
  {
  
  }

private static int MOVE = 0;
private static int ATTACK_AREA=1;
private static int ATTACK = 2;
private static int SET_HOME = 3;
private static int CLEAR_HOME = 4;
private static int SET_UPKEEP = 5;
private static int CLEAR_UPKEEP = 6;
private static int GUARD = 7;

public abstract static class Command
{
private int commandID;
private Command(int id){this.commandID=id;}
}

public static class CommandBlockBased extends Command
{
int targetX, targetY, targetZ;
private CommandBlockBased(int id, int x, int y, int z)
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
  super(MOVE, x,y,z);
  }
}

public static class CommandAttackArea extends CommandBlockBased
{
public CommandAttackArea(int x, int y, int z)
  {
  super(ATTACK_AREA,x,y,z);
  }
}

public static class CommandSetHome extends CommandBlockBased
{
public CommandSetHome(int x, int y, int z)
  {
  super(SET_HOME,x,y,z);
  }
}

public static class CommandSetUpkeep extends CommandBlockBased
{
public CommandSetUpkeep(int x, int y, int z)
  {
  super(SET_UPKEEP, x,y,z);
  }
}

public static class CommandAttack extends Command
{
Entity target;
public CommandAttack(Entity target)
  {
  super(ATTACK);
  this.target=target;
  }
}

public static class CommandGuard extends Command
{
Entity target;
public CommandGuard(Entity target)
  {
  super(GUARD);
  this.target=target;
  }
}

}
