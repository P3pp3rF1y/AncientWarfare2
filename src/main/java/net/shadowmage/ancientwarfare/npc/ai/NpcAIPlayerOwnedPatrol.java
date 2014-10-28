package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.orders.CombatOrder;

public class NpcAIPlayerOwnedPatrol extends NpcAI
{

double moveSpeed = 1.d;
boolean init = false;
int patrolIndex;
boolean atPoint;
int ticksAtPoint;

int maxTicksAtPoint = 50;//default 2.5 second idle at each point

public CombatOrder orders;
ItemStack ordersStack;

public NpcAIPlayerOwnedPatrol(NpcBase npc)
  {
  super(npc);
  setMutexBits(ATTACK+MOVE);
  }

public void onOrdersInventoryChanged()
  {
  patrolIndex = 0;
  ordersStack = npc.ordersStack;
  orders = CombatOrder.getCombatOrder(ordersStack);
  }

@Override
public boolean shouldExecute()
  {
  if(!init)
    {
    init = true;
    ordersStack = npc.ordersStack;
    orders = CombatOrder.getCombatOrder(ordersStack);
    if(orders==null || patrolIndex >= orders.getPatrolSize())
      {
      patrolIndex=0;
      }
    }
  if(!npc.getIsAIEnabled()){return false;}
  if(npc.getAttackTarget()!=null){return false;}
  return orders!=null && ordersStack!=null && orders.getPatrolDimension()==npc.worldObj.provider.dimensionId && orders.getPatrolSize()>0;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.getIsAIEnabled()){return false;}
  if(npc.getAttackTarget()!=null){return false;}
  return orders!=null && ordersStack!=null && orders.getPatrolDimension()==npc.worldObj.provider.dimensionId && orders.getPatrolSize()>0;
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_PATROL);
  }

@Override
public void updateTask()
  {
  if(atPoint)
    {
    npc.removeAITask(TASK_MOVE);
    ticksAtPoint++;
    if(ticksAtPoint>maxTicksAtPoint)
      {
      setMoveToNextPoint();
      }
    }
  else
    { 
    BlockPosition pos = orders.getPatrolPoint(patrolIndex);
    double dist = npc.getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    if(dist>2.d*2.d)
      {
      moveToPosition(pos, dist);
      }
    else
      {
      atPoint = true;
      ticksAtPoint = 0;
      }
    }
  }

private void setMoveToNextPoint()
  {
  atPoint = false;
  ticksAtPoint = 0;
  patrolIndex++;
  moveRetryDelay=0;
  if(patrolIndex>=orders.getPatrolSize()){patrolIndex=0;}
  }

public void resetTask()
  {
  ticksAtPoint=0;
  moveRetryDelay=0;
  npc.removeAITask(TASK_PATROL+TASK_MOVE);
  }

public void readFromNBT(NBTTagCompound tag)
  {
  patrolIndex = tag.getInteger("patrolIndex");
  atPoint = tag.getBoolean("atPoint");
  ticksAtPoint = tag.getInteger("ticksAtPoint");
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("patrolIndex", patrolIndex);
  tag.setBoolean("atPoint", atPoint);
  tag.setInteger("ticksAtPoint", ticksAtPoint);
  return tag;
  }

}
