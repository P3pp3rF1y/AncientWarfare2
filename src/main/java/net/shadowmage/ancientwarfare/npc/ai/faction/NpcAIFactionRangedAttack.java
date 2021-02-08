package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIFactionRangedAttack extends NpcAIAttack<NpcBase> {
	private final IRangedAttackMob rangedAttacker;

	private int attackDistanceSq;
	private final int rangedAttackDelay;

	public <T extends NpcBase & IRangedAttackMob> NpcAIFactionRangedAttack(T npc, double moveSpeed, int attackDistanceSq, int attackDelay) {
		super(npc);
		rangedAttacker = npc;
		this.moveSpeed = moveSpeed;
		this.attackDistanceSq = attackDistanceSq;
		this.rangedAttackDelay = attackDelay;
	}

	public <T extends NpcBase & IRangedAttackMob> NpcAIFactionRangedAttack(T npc) {
		this(npc, 1, (int) Math.pow(npc.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(), 2), 45);
	}

	@Override
	protected boolean shouldCloseOnTarget(double dist) {
		return (dist > getAttackDistanceSq() || !npc.getEntitySenses().canSee(getTarget()));
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
		if (!npc.isHandActive()) {
			npc.setActiveHand(EnumHand.MAIN_HAND);
		} else if (getAttackDelay() <= 0) {
			npc.resetActiveHand();
			float pwr = (float) (getAttackDistanceSq() / dist);
			pwr = Math.min(Math.max(pwr, 0.1F), 1F);
			rangedAttacker.attackEntityWithRangedAttack(getTarget(), pwr);
			setAttackDelay(rangedAttackDelay);
		}
	}
}
