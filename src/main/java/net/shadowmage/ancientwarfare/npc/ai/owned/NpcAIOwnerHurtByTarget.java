package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.shadowmage.ancientwarfare.npc.ai.AIHelper;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

import javax.annotation.Nullable;

public class NpcAIOwnerHurtByTarget extends EntityAITarget {
	private final NpcPlayerOwned npc;
	private EntityLivingBase attacker;
	private int timestamp;

	public NpcAIOwnerHurtByTarget(NpcPlayerOwned npc) {
		super(npc, false);
		this.npc = npc;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		EntityLivingBase entitylivingbase = npc.world.getPlayerEntityByUUID(npc.getOwner().getUUID());

		if (entitylivingbase == null) {
			return false;
		} else {
			attacker = entitylivingbase.getRevengeTarget();
			int i = entitylivingbase.getRevengeTimer();
			return i != timestamp && isSuitableTarget(attacker, false) && AIHelper.isWithinFollowRange(npc, attacker);
		}
	}

	@Override
	public void startExecuting() {
		taskOwner.setAttackTarget(attacker);
		EntityLivingBase entitylivingbase = npc.world.getPlayerEntityByUUID(npc.getOwner().getUUID());

		if (entitylivingbase != null) {
			timestamp = entitylivingbase.getRevengeTimer();
		}

		super.startExecuting();
	}

	@Override
	protected boolean isSuitableTarget(@Nullable EntityLivingBase target, boolean includeInvincibles) {
		return AIHelper.isTarget(npc, target, shouldCheckSight);
	}
}
