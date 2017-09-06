package net.shadowmage.ancientwarfare.npc.ai;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget.Sorter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Collections;
import java.util.List;

public class NpcAIMedicBase extends NpcAI<NpcBase> {

    private int injuredRecheckDelay = 0;
    private int injuredRecheckDelayMax = 20;
    private int healDelay = 0;
    private int healDelayMax = 20;

    private EntityLivingBase targetToHeal = null;

    private final EntityAINearestAttackableTarget.Sorter sorter;
    private IEntitySelector selector;

    public NpcAIMedicBase(NpcBase npc) {
        super(npc);
        sorter = new Sorter(npc);
        selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity selectedEntity) {
                if (selectedEntity instanceof EntityLivingBase) {
                    EntityLivingBase e = (EntityLivingBase) selectedEntity;
                    if (e.isEntityAlive() && e.getHealth() < e.getMaxHealth() && !NpcAIMedicBase.this.npc.isHostileTowards(e))
                        return true;
                }
                return false;
            }
        };
    }


    @Override
    public boolean shouldExecute() {
        if (!npc.getIsAIEnabled()) {
            return false;
        }
        if (!isProperSubtype()) {
            return false;
        }
        if (injuredRecheckDelay-- > 0) {
            return false;
        }
        injuredRecheckDelay = injuredRecheckDelayMax;
        double dist = npc.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
        AxisAlignedBB bb = npc.getEntityBoundingBox().expand(dist, dist / 2, dist);
        List<EntityLivingBase> potentialTargets = npc.world.selectEntitiesWithinAABB(EntityLivingBase.class, bb, selector);
        if (potentialTargets.isEmpty()) {
            return false;
        }
        Collections.sort(potentialTargets, sorter);
        Iterable<EntityLivingBase> sub = Iterables.filter(potentialTargets, new Predicate<EntityLivingBase>() {
            @Override
            public boolean apply(EntityLivingBase input) {
                return input instanceof NpcBase || input instanceof EntityPlayer;
            }
        });
        for(EntityLivingBase base : sub) {
            this.targetToHeal = base;
            if (validateTarget())
                return true;
        }
        this.targetToHeal = potentialTargets.get(0);
        if (!validateTarget()) {
            targetToHeal = null;
            return false;
        }
        return true;
    }

    private boolean validateTarget(){
        return targetToHeal!=null && targetToHeal.isEntityAlive() && targetToHeal.getHealth() < targetToHeal.getMaxHealth();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return npc.getIsAIEnabled() && isProperSubtype() && validateTarget();
    }

    protected boolean isProperSubtype() {
        return "medic".equals(npc.getNpcSubType());
    }

    @Override
    public void startExecuting() {
        npc.addAITask(TASK_GUARD);
    }

    @Override
    public void updateTask() {
        double dist = npc.getDistanceSqToEntity(targetToHeal);
        double attackDistance = (double) ((this.npc.width * this.npc.width * 2.0F * 2.0F) + (targetToHeal.width * targetToHeal.width * 2.0F * 2.0F));
        if (dist > attackDistance) {
            npc.addAITask(TASK_MOVE);
            moveToEntity(targetToHeal, dist);
            healDelay = healDelayMax;
        } else {
            npc.removeAITask(TASK_MOVE);
            healDelay--;
            if (healDelay < 0) {
                healDelay = healDelayMax;
                float amountToHeal = ((float) npc.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue()) / 2.f;
                npc.swingItem();
                targetToHeal.heal(amountToHeal);
            }
        }
    }

    @Override
    public void resetTask() {
        npc.removeAITask(TASK_MOVE + TASK_GUARD);
        targetToHeal = null;
    }


}
