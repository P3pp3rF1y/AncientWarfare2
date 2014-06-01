package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
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
    if(npc.getAttackTarget()==null)
      {
      alertDelay=200;
      AWLog.logDebug("combat responding to alert from: "+broadcaster+ " target: "+target);
      npc.setAttackTarget(target);
      }
    }
  }

}
