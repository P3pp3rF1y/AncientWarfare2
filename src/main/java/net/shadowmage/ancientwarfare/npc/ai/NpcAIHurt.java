package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

/**
 * Created by Olivier on 22/05/2015.
 */
public class NpcAIHurt extends EntityAIHurtByTarget{
    public NpcAIHurt(NpcBase npc) {
        super(npc, true);
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase target, boolean unused){
        return AIHelper.isTarget((NpcBase)this.taskOwner, target, shouldCheckSight);
    }
}
