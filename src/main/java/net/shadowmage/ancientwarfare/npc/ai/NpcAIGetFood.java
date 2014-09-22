package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIGetFood extends NpcAI
{

public NpcAIGetFood(NpcBase npc)
  {
  super(npc);
  this.setMutexBits(MOVE+ATTACK+HUNGRY);
  }

@Override
public boolean shouldExecute()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining()==0 && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return npc.requiresUpkeep() && npc.getUpkeepPoint()!=null && npc.getFoodRemaining() < npc.getUpkeepAmount() && npc.getUpkeepDimensionId()==npc.worldObj.provider.dimensionId;
  }

/**
 * Execute a one shot task or start executing a continuous task
 */
@Override
public void startExecuting()
  {
  npc.addAITask(TASK_UPKEEP);
  }

/**
 * Updates the task
 */
@Override
public void updateTask()
  {
  BlockPosition pos = npc.getUpkeepPoint();
  if(pos==null){return;}
  double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
  if(dist>5.d*5.d)
    {
    npc.addAITask(TASK_MOVE);
    moveToPosition(pos, dist);
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    tryUpkeep(pos);
    }
  }

/**
 * Resets the task
 */
@Override
public void resetTask()
  {
  moveRetryDelay=0;
  npc.removeAITask(TASK_UPKEEP + TASK_MOVE);
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

protected void withdrawFood(IInventory inventory, int side)
  {
  int amount = npc.getUpkeepAmount() - npc.getFoodRemaining();
  if(amount<=0){return;}
  ItemStack stack;
  int val;
  int eaten = 0;
  if(side>=0 && inventory instanceof ISidedInventory)
    {
    int[] ind = ((ISidedInventory)inventory).getAccessibleSlotsFromSide(side);
    for(int i : ind)
      {
      stack = inventory.getStackInSlot(i);
      val = AncientWarfareNPC.statics.getFoodValue(stack);
      if(val<=0){continue;}
      while(eaten < amount && stack.stackSize>0)
        {
        eaten+=val;
        stack.stackSize--;
        inventory.markDirty();
        }
      if(stack.stackSize<=0)
        {
        inventory.setInventorySlotContents(i, null);
        }
      }
    }
  else
    {
    for(int i = 0 ; i<inventory.getSizeInventory();i++)
      {
      stack = inventory.getStackInSlot(i);
      val = AncientWarfareNPC.statics.getFoodValue(stack);
      if(val<=0){continue;}
      while(eaten < amount && stack.stackSize>0)
        {
        eaten+=val;
        stack.stackSize--;
        inventory.markDirty();
        }
      if(stack.stackSize<=0)
        {
        inventory.setInventorySlotContents(i, null);
        }
      }    
    }
  npc.setFoodRemaining(npc.getFoodRemaining()+eaten);
  }

}
