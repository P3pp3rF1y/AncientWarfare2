package net.shadowmage.ancientwarfare.npc.ai;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIMountHorse extends NpcAI
{

int lastExecutedTick = -1;
int moveRetryDelay = 0;
double moveSpeed = 1.d;
private final EntityAINearestAttackableTarget.Sorter sorter;
AttributeModifier followRangeModifier;
AttributeModifier moveSpeedModifier;

EntityHorse target;

public NpcAIMountHorse(NpcBase npc)
  {
  super(npc);
  sorter = new Sorter(npc);
  this.setMutexBits(ATTACK+MOVE);
  this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", 1.5d, 2);
  moveSpeedModifier.setSaved(false);
  this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
  this.followRangeModifier.setSaved(false);
  }

@Override
public boolean shouldExecute()
  {
  return npc.ridingEntity==null && (lastExecutedTick==-1 || npc.ticksExisted-lastExecutedTick>200);
  }

@Override
public boolean continueExecuting()
  {
  return target!=null && npc.ridingEntity==null && target.riddenByEntity==null;
  }

@Override
public void startExecuting()
  {
  AWLog.logDebug("executing search for horses...");
  lastExecutedTick = npc.ticksExisted;
  target = null;
  AxisAlignedBB bb = npc.boundingBox.expand(40.d, 20.d, 40.d);//TODO set from npc follow-distance
  List<EntityHorse> horses = npc.worldObj.getEntitiesWithinAABB(EntityHorse.class, bb);
  if(horses.isEmpty()){return;}
  Collections.sort(horses, sorter);
  for(EntityHorse horse : horses)
    {
    if(horse.riddenByEntity==null && horse.isTame() && horse.isAdultHorse())
      {
      target = horse;
      break;
      }
    }
  }

@Override
public void updateTask()
  {  
  if(target!=null)
    {
    if(target.riddenByEntity!=null)
      {
      target=null;
      return;
      }
    double dist = npc.getDistanceSqToEntity(target);
    if(dist>5.d*5.d)
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
    else
      {
      npc.getNavigator().clearPathEntity(); 
      target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
      target.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
      target.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(moveSpeedModifier);
      target.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(followRangeModifier);
      npc.mountEntity(target);
      target = null;
      }
    }
  }

/**
 * should be called when the npc dismounts to remove the movement-speed modifier<br>
 * called from NpcPlayerOwned-interact when player interacts with riding npc
 * @param horse
 */
public void onDismount(EntityHorse horse)
  {
  horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
  horse.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
  }

@Override
public void resetTask()
  {  
  target = null;
  }

}
