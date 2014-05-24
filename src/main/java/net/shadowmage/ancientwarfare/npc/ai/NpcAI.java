package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.ai.EntityAIBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public abstract class NpcAI extends EntityAIBase
{


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
