package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
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
    if(npc.getAttackTarget()==null)
      {
      npc.setAttackTarget(target);
      }
    }
  }

}
