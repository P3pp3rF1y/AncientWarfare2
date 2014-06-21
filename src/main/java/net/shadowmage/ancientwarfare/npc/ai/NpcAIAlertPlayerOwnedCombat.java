package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAlertPlayerOwnedCombat extends NpcAIAlertPlayerOwned
{

public NpcAIAlertPlayerOwnedCombat(NpcBase npc)
  {
  super(npc);
  }

@Override
public void handleAlert(NpcBase broadcaster, EntityLivingBase target)
  {
  if(alertDelay<0)
    {
    alertDelay=200;
    double fr = npc.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
    if(npc.getAttackTarget()==null && npc.getDistanceSqToEntity(target) < fr*fr)
      {
      npc.setAttackTarget(target);
      } 
    }
  }

}
