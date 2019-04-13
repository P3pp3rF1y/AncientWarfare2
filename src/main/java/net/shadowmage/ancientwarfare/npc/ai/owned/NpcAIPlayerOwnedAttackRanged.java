package net.shadowmage.ancientwarfare.npc.ai.owned;

import com.google.common.primitives.Floats;
import net.minecraft.entity.IRangedAttackMob;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttack;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedAttackRanged extends NpcAIAttack<NpcBase> {

	private final IRangedAttackMob rangedAttacker;
	private double attackDistance = AWNPCStatics.archerRange * AWNPCStatics.archerRange;

	public NpcAIPlayerOwnedAttackRanged(NpcBase npc) {
		super(npc);
		this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
		this.moveSpeed = 1.d;
		setMutexBits(ATTACK + MOVE);
	}

	@Override
	protected boolean shouldCloseOnTarget(double dist) {
		return (dist > attackDistance || !this.npc.getEntitySenses().canSee(getTarget()));
	}

	@Override
	protected void doAttack(double dist) {
		npc.removeAITask(TASK_MOVE);
		this.npc.getNavigator().clearPath();
		if (this.getAttackDelay() <= 0) {
			float pwr = (float) (attackDistance / dist);
			pwr = Floats.constrainToRange(pwr, 0.1f, 1f);
			this.rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
			this.setAttackDelay(35);
		}
	}
}
