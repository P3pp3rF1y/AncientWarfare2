package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIRideHorseFaction extends NpcAI
{

AttributeModifier followRangeModifier;
AttributeModifier moveSpeedModifier;
boolean wasHorseKilled = false;
EntityHorse horse;

public NpcAIRideHorseFaction(NpcBase npc)
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
  return !wasHorseKilled && (npc.ridingEntity==null || horse!=npc.ridingEntity);
  }

@Override
public void startExecuting()
  {  
  if(horse==null && !wasHorseKilled)
    {
    if(npc.ridingEntity instanceof EntityHorse)
      {
      horse = (EntityHorse)npc.ridingEntity;
      }
    else
      {
      spawnHorse();
      }
    }
  else if(horse!=null && horse.isDead)
    {
    wasHorseKilled=true;
    horse=null;
    }
  }

@Override
public void updateTask()
  {
  super.updateTask();
  }

private void spawnHorse()
  {
  EntityHorse horse = new EntityHorse(npc.worldObj);
  horse.setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
  horse.setHorseType(0);
  //TODO set horse variant randomly...need to find how/where to set this at/from
  //TODO set saddle in horse chest inventory slot 0
  horse.setHorseSaddled(true);//TODO how the F to access horse inventory to set saddle?
  horse.setHorseTamed(true);
  horse.setChested(true);
  this.horse = horse;
  applyModifiers();
  npc.worldObj.spawnEntityInWorld(horse);
  npc.mountEntity(horse);
  }

public void onKilled()
  {
  if(horse!=null)
    {
    removeModifiers();
    }
  horse = null;
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

}
