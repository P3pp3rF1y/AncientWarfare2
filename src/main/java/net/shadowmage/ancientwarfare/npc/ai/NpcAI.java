package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

/**
 * AI template class with utility methods and member access for a non-specific NPC type
 * @author Shadowmage
 *
 */
public abstract class NpcAI extends EntityAIBase
{
/**
 * used during npc-ai task-rendering to determine how many bits to loop through
 * of the task bitfield -- needs to be increased if more task types / bits are added
 */
public static final int NUMBER_OF_TASKS = 10;
public static final int TASK_ATTACK = 1;
public static final int TASK_UPKEEP = 2;
public static final int TASK_IDLE_HUNGRY = 4;
public static final int TASK_GO_HOME = 8;
public static final int TASK_WORK = 16;
public static final int TASK_PATROL = 32;
public static final int TASK_GUARD = 64;
public static final int TASK_FOLLOW = 128;
public static final int TASK_WANDER = 256;
public static final int TASK_MOVE = 512;

/**
 * internal flag used to determine exclusion types
 */
public static final int MOVE = 1;
public static final int ATTACK = 2;
public static final int SWIM = 4;
public static final int HUNGRY = 8;

protected int moveRetryDelay;
protected double moveSpeed = 1.d;

protected NpcBase npc;

public NpcAI(NpcBase npc)
  {
  this.npc = npc;
  }

protected void moveToPosition(int x, int y, int z, double dist)
  {
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    npc.getNavigator().tryMoveToXYZ(x+0.5d, y, z+0.5d, moveSpeed);
    moveRetryDelay=10;//base .5 second retry delay
    if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32    
    }
  }

protected void moveToPosition(BlockPosition pos, double dist)
  {
  moveToPosition(pos.x, pos.y, pos.z, dist);
  }

protected void moveToEntity(Entity target, double dist)
  {
  moveRetryDelay--;
  if(moveRetryDelay<=0)
    {
    npc.getNavigator().tryMoveToEntityLiving(target, moveSpeed);
    moveRetryDelay=10;//base .5 second retry delay
    if(dist>256){moveRetryDelay+=10;}//add .5 seconds if distance>16
    if(dist>1024){moveRetryDelay+=20;}//add another 1 second if distance>32    
    }
  }

}
