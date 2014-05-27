package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public abstract class NpcAI extends EntityAIBase
{
/**
 * used during npc-ai task-rendering to determine how many bits to loop through
 * of the task bitfield
 */
public static final int NUMBER_OF_TASKS = 9;
public static final int TASK_ATTACK = 1;
public static final int TASK_UPKEEP = 2;
public static final int TASK_IDLE_HUNGRY = 4;
public static final int TASK_GO_HOME = 8;
public static final int TASK_WORK = 16;
public static final int TASK_PATROL = 32;
public static final int TASK_GUARD = 64;
public static final int TASK_FOLLOW = 128;
public static final int TASK_MOVE = 256;

public static final int MOVE = 1;
public static final int ATTACK = 2;
public static final int SWIM = 4;
public static final int HUNGRY = 8;

NpcBase npc;
public NpcAI(NpcBase npc)
  {
  this.npc = npc;
  }

}
