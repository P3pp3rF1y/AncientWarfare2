package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIRideHorse extends NpcAI
{

AttributeModifier followRangeModifier;
AttributeModifier moveSpeedModifier;
EntityHorse horse;

public NpcAIRideHorse(NpcBase npc)
  {
  super(npc);
  this.moveSpeedModifier = new AttributeModifier("modifier.npc_ride_speed", 1.5d, 2);
  this.moveSpeedModifier.setSaved(false);
  this.followRangeModifier = new AttributeModifier("modifier.npc_horse_path_extension", 24.d, 0);
  this.followRangeModifier.setSaved(false);
  }

@Override
public boolean shouldExecute()
  {
  return horse != npc.ridingEntity && (horse!=null || npc.ridingEntity instanceof EntityHorse);
  }

@Override
public boolean continueExecuting()
  {
  return false;
  }

@Override
public void startExecuting()
  {
  if(horse!=null)
    {
    removeModifiers();
    }
  horse = null;
  if(npc.ridingEntity instanceof EntityHorse)
    {
    horse = (EntityHorse)npc.ridingEntity;
    applyModifiers();
    }
  }

private void applyModifiers()
  {
  horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
  horse.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
  horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).applyModifier(moveSpeedModifier);
  horse.getEntityAttribute(SharedMonsterAttributes.followRange).applyModifier(followRangeModifier);
  }

private void removeModifiers()
  {
  horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).removeModifier(moveSpeedModifier);
  horse.getEntityAttribute(SharedMonsterAttributes.followRange).removeModifier(followRangeModifier);
  }

@Override
public void resetTask()
  {
  }

}
