package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMoveHome extends NpcAI
{

float dayRange, nightRange;
float dayLeash, nightLeash;

public NpcAIMoveHome(NpcBase npc, float dayRange, float nightRange, float dayLeash, float nightLeash)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK);
  this.dayRange = dayRange;
  this.nightRange = nightRange;
  this.dayLeash = dayLeash;
  this.nightLeash = nightLeash;
  }

@Override
public boolean shouldExecute()
  {
  if(!npc.hasHome()){return false;}
  ChunkCoordinates cc = npc.getHomePosition();
  float distSq = (float) npc.getDistanceSq(cc.posX+0.5d, cc.posY, cc.posZ+0.5d);
  if(npc.shouldBeAtHome() || exceedsRange(distSq))
    {
    return true;
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.hasHome()){return false;}
  ChunkCoordinates cc = npc.getHomePosition();
  float distSq = (float) npc.getDistanceSq(cc.posX+0.5d, cc.posY, cc.posZ+0.5d);
  if(npc.shouldBeAtHome() || exceedsLeash(distSq))
    {
    return true;
    }
  return false;
  }

protected boolean exceedsRange(float distSq)
  {
  float range = getRange() * getRange();
  return distSq > range;
  }

protected boolean exceedsLeash(float distSq)  
  {
  float leash = getLeashRange() * getLeashRange();
  return distSq > leash;
  }

protected float getLeashRange()
  {
  return npc.worldObj.isDaytime() && !npc.worldObj.isRaining() ? dayLeash : nightLeash;
  }

protected float getRange()
  {
  return npc.worldObj.isDaytime() && !npc.worldObj.isRaining() ? dayRange : nightRange;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_GO_HOME);
  }

@Override
public void updateTask()
  {
  ChunkCoordinates cc = npc.getHomePosition();
  double dist = npc.getDistanceSq(cc.posX+0.5d, cc.posY, cc.posZ+0.5d);
  double leash = getLeashRange() * getLeashRange();
  if(dist > leash)
    {
    npc.addAITask(TASK_MOVE);
    moveToPosition(cc.posX, cc.posY, cc.posZ, dist);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    npc.getNavigator().clearPathEntity();
    }
  }

@Override
public void resetTask()
  {
  npc.removeAITask(TASK_GO_HOME);
  }

}
