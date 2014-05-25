package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIClimbLadder extends NpcAI
{

public NpcAIClimbLadder(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  if(npc.isOnLadder() && !npc.getNavigator().noPath())
    {
    PathEntity path = npc.getNavigator().getPath();
    PathPoint pp = path.getPathPointFromIndex(path.getCurrentPathIndex());
    if(pp.yCoord!=MathHelper.floor_double(npc.posY))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean continueExecuting()
  {
  // TODO Auto-generated method stub
  return super.continueExecuting();
  }

@Override
public void startExecuting()
  {
  // TODO Auto-generated method stub
  super.startExecuting();
  }

@Override
public void updateTask()
  {
  // TODO Auto-generated method stub
  super.updateTask();
  }

@Override
public void resetTask()
  {
  // TODO Auto-generated method stub
  super.resetTask();
  }

}
