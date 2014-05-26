package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.ChunkCoordinates;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMoveHome extends NpcAI
{

float dayRange, nightRange;
float dayLeash, nightLeash;
int moveTimer = 0;

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
  if(npc.getAttackTarget()!=null){return false;}
  ChunkCoordinates cc = npc.getHomePosition();
  float distSq = (float) npc.getDistanceSq(cc.posX, cc.posY, cc.posZ);
  boolean isNight = !npc.worldObj.isDaytime();
  float check = isNight ? nightRange : dayRange;
  if(distSq >= check*check){return true;}
  return false;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.hasHome()){return false;}
  ChunkCoordinates cc = npc.getHomePosition();
  float distSq = (float) npc.getDistanceSq(cc.posX, cc.posY, cc.posZ);
  boolean isNight = !npc.worldObj.isDaytime();
  float check = isNight ? nightLeash : dayLeash;
  if(distSq >= check*check){return true;}
  return false;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_GO_HOME);
  moveTimer = 0;
  ChunkCoordinates cc = npc.getHomePosition();
  npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);
  npc.setTarget(null);
  }

@Override
public void updateTask()
  {
  moveTimer--;
  if(moveTimer<=0)
    {
    ChunkCoordinates cc = npc.getHomePosition();
    npc.getNavigator().tryMoveToXYZ(cc.posX, cc.posY, cc.posZ, 1.0d);
    moveTimer = 10;
    }
  }

@Override
public void resetTask()
  {
  moveTimer = 0;
  npc.removeAITask(TASK_GO_HOME);
  }

}
