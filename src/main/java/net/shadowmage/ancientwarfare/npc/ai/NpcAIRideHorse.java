package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.pathfinding.PathEntity;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIRideHorse extends NpcAI
{

EntityHorse horse;

public NpcAIRideHorse(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  return npc.ridingEntity instanceof EntityHorse;
  }

@Override
public boolean continueExecuting()
  {
  return npc.ridingEntity instanceof EntityHorse;
  }

@Override
public void startExecuting()
  {
  horse = (EntityHorse) npc.ridingEntity;
  }

@Override
public void updateTask()
  {
  Entity e = npc.ridingEntity;
  if(e instanceof EntityHorse)
    {
    AWLog.logDebug("updating ride horse ai..");
    EntityHorse horse = (EntityHorse)e;
    PathEntity npcPath = npc.getNavigator().getPath();
    if(npcPath!=null)
      {
      AWLog.logDebug("npc has path..."+npcPath);
      PathEntity horsePath = horse.getNavigator().getPath();
      if(horsePath==null || !horsePath.isSamePath(npcPath))
        {
        AWLog.logDebug("horse has path...."+horsePath);
        horse.getNavigator().setPath(npcPath, 1.d);        
        }
      }
    else
      {
      AWLog.logDebug("npc has no path..clearing horse path..");
      horse.getNavigator().clearPathEntity();
      }
    }  
  }

@Override
public void resetTask()
  {  
  horse.getNavigator().clearPathEntity();
  horse = null;
  }

}
