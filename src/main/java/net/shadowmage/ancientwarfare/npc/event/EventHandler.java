package net.shadowmage.ancientwarfare.npc.event;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EventHandler
{
private EventHandler(){}
public static final EventHandler INSTANCE = new EventHandler();

@SubscribeEvent
public void entitySpawnEvent(EntityJoinWorldEvent evt)
  {
  String s = EntityList.getEntityString(evt.entity);
  if(AncientWarfareNPC.statics.shouldEntityTargetNpcs(s))
    {    
    if(evt.entity instanceof EntityCreature)
      {
      EntityCreature e = (EntityCreature)evt.entity;
      if(!(e instanceof IRangedAttackMob))
        {
        e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));        
        }
      else
        {
        boolean foundRangedAttack=false;
        for(EntityAITaskEntry entry : (List<EntityAITaskEntry>)e.tasks.taskEntries)
          {
          if(entry.action instanceof EntityAIArrowAttack)
            {
            foundRangedAttack=true;
            break;
            }
          }
        if(!foundRangedAttack)
          {
          e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));          
          }
        }
      e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));      
      }
    }
  }

}
