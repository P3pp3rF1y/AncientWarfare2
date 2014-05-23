package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIGetFood extends NpcAI
{

int moveDelayTicks = 0;

public NpcAIGetFood(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK);
  }

@Override
public boolean shouldExecute()
  {
  AWLog.logDebug("npc get food shouldExecute");
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0 && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

@Override
public boolean continueExecuting()
  {
  AWLog.logDebug("npc get food continueExecuting");
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0 && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
  AWLog.logDebug("npc get food starting executing");  
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
  AWLog.logDebug("npc get food update task");
  BlockPosition pos = npc.getUpkeepPoint();
  if(pos==null){return;}
  if(withinDistanceToUpkeep(pos))
    {
    tryUpkeep(pos);
    }
  else
    {
    moveToUpkeep(pos);
    }
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  AWLog.logDebug("npc get food resetting task");
  moveDelayTicks=0;
  }

protected void moveToUpkeep(BlockPosition pos)
  {
  if(moveDelayTicks>0)
    {
    moveDelayTicks--;
    }
  else
    {
    moveDelayTicks=10;
    npc.getNavigator().tryMoveToXYZ(pos.x+0.5d, pos.y, pos.z+0.5d, 1.d);
    }
  }

protected void tryUpkeep(BlockPosition pos)
  {
  TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
  int side = npc.getUpkeepBlockSide();
  if(te instanceof IInventory)
    {
    withdrawFood((IInventory) te, side);    
    }
  }

protected boolean withinDistanceToUpkeep(BlockPosition pos)
  {
  return pos!=null && npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d) < 5.d*5.d;
  }

protected void withdrawFood(IInventory inventory, int side)
  {
  }

}
