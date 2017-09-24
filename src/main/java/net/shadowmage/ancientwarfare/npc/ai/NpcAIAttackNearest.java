package net.shadowmage.ancientwarfare.npc.ai;

import com.google.common.base.Predicate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nullable;

/*
 * Created by Olivier on 22/05/2015.
 */
public class NpcAIAttackNearest extends EntityAINearestAttackableTarget{
    public NpcAIAttackNearest(NpcBase npc, @Nullable final Predicate<NpcBase> targetSelector) {
        super(npc, EntityLivingBase.class, 0, true, false, targetSelector);
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean unused){
        return AIHelper.isTarget((NpcBase)this.taskOwner, target, shouldCheckSight);
    }
}
