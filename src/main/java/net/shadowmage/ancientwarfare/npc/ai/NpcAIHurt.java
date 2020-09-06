package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import javax.annotation.Nullable;

public class NpcAIHurt extends EntityAIHurtByTarget {
	public NpcAIHurt(NpcBase npc) {
		super(npc, true);
	}

	@Override
	protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean unused) {
		return !(target instanceof EntityPlayer || target instanceof NpcPlayerOwned)
				&& AIHelper.isTarget((NpcBase) taskOwner, target, shouldCheckSight);
	}
}
