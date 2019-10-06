package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.IRangedAttackMob;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionPotionAttack extends NpcAIFactionRangedAttack {

	private final IRangedAttackMob rangedAttacker;

	private int attackDistanceSq = -1;

	public NpcAIFactionPotionAttack(NpcBase npc) {
		super(npc);
		this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
		this.moveSpeed = 0.8d;
	}

	@Override
	protected boolean shouldCloseOnTarget(double dist) {
		return (dist > getAttackDistanceSq() || !this.npc.getEntitySenses().canSee(this.getTarget()));
	}

	@Override
	protected double getAttackDistanceSq() {
		if (attackDistanceSq == -1) {
			attackDistanceSq = 20;
		}
		return attackDistanceSq;
	}

	@Override
	protected void doAttack(double dist) {
		double homeDist = npc.getDistanceSqFromHome();
		if (homeDist > MIN_RANGE && dist < 16) {
			npc.addAITask(TASK_MOVE);
			this.moveToPosition(npc.getHomePosition(), homeDist);
		} else {
			npc.removeAITask(TASK_MOVE);
			npc.getNavigator().clearPath();
		}
		if (this.getAttackDelay() <= 0) {
			float pwr = (float) (getAttackDistanceSq() / dist);
			pwr = Math.min(Math.max(pwr, 0.1F), 1F);
			this.rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
			this.setAttackDelay(80);
		}
	}
}
