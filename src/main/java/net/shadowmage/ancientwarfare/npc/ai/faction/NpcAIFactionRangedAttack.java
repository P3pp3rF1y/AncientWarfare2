package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionRangedAttack extends NpcAIAttack<NpcBase> {

	private final IRangedAttackMob rangedAttacker;

	private int attackDistanceSq = -1;

	public NpcAIFactionRangedAttack(NpcBase npc) {
		super(npc);
		this.rangedAttacker = (IRangedAttackMob) npc;//will classcastexception if improperly used..
		this.moveSpeed = 1.d;
	}

	@Override
	protected boolean shouldCloseOnTarget(double dist) {
		return (dist > getAttackDistanceSq() || !this.npc.getEntitySenses().canSee(this.getTarget()));
	}

	private double getAttackDistanceSq() {
		if (attackDistanceSq == -1) {
			double distance = npc.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
			attackDistanceSq = (int) (distance * distance);
		}
		return attackDistanceSq;
	}

	@Override
	protected void doAttack(double dist) {
		double homeDist = npc.getDistanceSqFromHome();
		if (homeDist > MIN_RANGE && dist < 8 * 8) {
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
			this.setAttackDelay(35);
		}
	}
}
