package net.shadowmage.ancientwarfare.npc.ai;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFindCommander extends NpcAI
{

int lastExecuted = -1;
NpcBase commander;
AttributeModifier modifier;

private final EntityAINearestAttackableTarget.Sorter sorter;
IEntitySelector selector;

public NpcAIFindCommander(NpcBase npc)
  {
  super(npc);
  modifier = new AttributeModifier("npc.commander.bonus", 2.d, 0);//additive modifier attribute, add 2 damage
  modifier.setSaved(false);
  sorter = new Sorter(npc);
  selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      if(var1 instanceof NpcBase)
        {
        NpcBase e = (NpcBase)var1;
        if(NpcAIFindCommander.this.npc.canBeCommandedBy(e.getOwnerName()) && isCommander(e))
          {
          return true;
          }
        }
      return false;
      }
    };
  }

@Override
public boolean shouldExecute()
  {
  if(!npc.getIsAIEnabled()){return false;}
  return !npc.getNpcSubType().equals("commander") && (commander==null || commander.isDead || !npc.canBeCommandedBy(commander.getOwnerName())) && (lastExecuted==-1 || npc.ticksExisted-lastExecuted>200);
  }

@Override
public boolean continueExecuting()
  {
  return false;
  }

/**
 * TODO override for faction npc to test for faction-type
 * @param npc
 * @return
 */
protected boolean isCommander(NpcBase npc)
  {
  return npc.getNpcFullType().equals("combat.commander");
  }

@SuppressWarnings("unchecked")
@Override
public void startExecuting()
  {
  lastExecuted = npc.ticksExisted;
  npc.getEntityAttribute(SharedMonsterAttributes.attackDamage).removeModifier(modifier);
  commander = null;  
  double dist = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
  AxisAlignedBB bb = npc.boundingBox.expand(dist, dist/2, dist);
  List<NpcBase> potentialTargets = npc.worldObj.selectEntitiesWithinAABB(NpcBase.class, bb, selector);
  if(!potentialTargets.isEmpty())
    {
    Collections.sort(potentialTargets, sorter);
    this.commander = potentialTargets.get(0);    
    npc.getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(modifier);
    }
  }

@Override
public void updateTask()
  {
  }

@Override
public void resetTask()
  {
  }

}
