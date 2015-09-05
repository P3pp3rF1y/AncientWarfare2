package net.shadowmage.ancientwarfare.npc.event;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class EventHandler {
    private EventHandler() {
    }

    public static final EventHandler INSTANCE = new EventHandler();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void entitySpawnEvent(EntityJoinWorldEvent evt) {
        if (evt.entity instanceof NpcBase) {
            return;
        }
        if (evt.entity instanceof EntityCreature) {
            String s = EntityList.getEntityString(evt.entity);
            if (AncientWarfareNPC.statics.shouldEntityTargetNpcs(s)) {
                EntityCreature e = (EntityCreature) evt.entity;
                if (evt.entity instanceof EntitySkeleton) {
                    EntitySkeleton skel = (EntitySkeleton) evt.entity;
                    if (skel.getSkeletonType() == 0)//normal
                    {
                        e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                    } else//wither
                    {
                        e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));
                        e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                    }
                } else if (evt.entity instanceof IRangedAttackMob) {
                    e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                } else {
                    e.tasks.addTask(3, new EntityAIAttackOnCollide(e, NpcBase.class, 1.d, false));
                    e.targetTasks.addTask(2, new EntityAINearestAttackableTarget(e, NpcBase.class, 0, false));
                }
            }
        }
    }

}
