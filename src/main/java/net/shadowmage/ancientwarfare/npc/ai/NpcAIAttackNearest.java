package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

/*
 * Created by Olivier on 22/05/2015.
 */
public class NpcAIAttackNearest extends EntityAINearestAttackableTarget{
    public NpcAIAttackNearest(NpcBase npc, IEntitySelector entitySelector) {
        super(npc, EntityLivingBase.class, 0, true, false, entitySelector);
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean unused){
        return AIHelper.isTarget((NpcBase)this.taskOwner, target, shouldCheckSight);
    }
}
