package net.shadowmage.ancientwarfare.npc.ai;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedMedic extends NpcAI
{

int injuredRecheckDelay = 0;
int injuredRecheckDelayMax = 20;
int healDelay = 0;
int healDelayMax = 20;

EntityLivingBase targetToHeal = null;

private final EntityAINearestAttackableTarget.Sorter sorter;
IEntitySelector selector;

public NpcAIPlayerOwnedMedic(NpcBase npc)
  {
  super(npc);
  sorter = new Sorter(npc);
  selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      if(var1 instanceof EntityLivingBase)
        {
        EntityLivingBase e = (EntityLivingBase)var1;
        if(e.getHealth()<e.getMaxHealth() && !NpcAIPlayerOwnedMedic.this.npc.isHostileTowards(e))
          {
          return true;
          }
        }
      return false;
      }
    };
  }

@SuppressWarnings("unchecked")
@Override
public boolean shouldExecute()
  {
  if(!npc.getIsAIEnabled()){return false;}
  if(!"medic".equals(npc.getNpcSubType())){return false;}
  injuredRecheckDelay--;
  if(injuredRecheckDelay>0){return false;}
  injuredRecheckDelay=injuredRecheckDelayMax;
  double dist = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
  AxisAlignedBB bb = npc.boundingBox.expand(dist, dist/2, dist);
  List<EntityLivingBase> potentialTargets = npc.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, bb, selector);
  if(potentialTargets.isEmpty()){return false;}
  Collections.sort(potentialTargets, sorter);
  this.targetToHeal = potentialTargets.get(0);
  return true;
  }

@Override
public boolean continueExecuting()
  {
  if(!npc.getIsAIEnabled()){return false;}
  if(!"medic".equals(npc.getNpcSubType())){return false;}
  if(targetToHeal==null || targetToHeal.isDead || targetToHeal.getHealth()>=targetToHeal.getMaxHealth()){return false;}
  
  return super.continueExecuting();
  }

@Override
public void startExecuting()
  {
  npc.addAITask(TASK_GUARD);
  }

@Override
public void updateTask()
  {
  double dist = npc.getDistanceSqToEntity(targetToHeal);
  double attackDistance = (double)((this.npc.width * this.npc.width * 2.0F * 2.0F) + (targetToHeal.width * targetToHeal.width * 2.0F * 2.0F));
  if(dist>attackDistance)
    {
    npc.addAITask(TASK_MOVE);
    moveToEntity(targetToHeal, dist);
    healDelay = healDelayMax;//TODO get from config
    }
  else
    {
    npc.removeAITask(TASK_MOVE);
    healDelay--;
    if(healDelay<0)
      {
      healDelay = healDelayMax;//TODO get from config
      float amountToHeal = ((float) npc.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue())/2.f;
      targetToHeal.setHealth(targetToHeal.getHealth()+amountToHeal);
      }
    }
  }

@Override
public void resetTask()
  {
  npc.removeAITask(TASK_MOVE+TASK_GUARD);
  }


}
