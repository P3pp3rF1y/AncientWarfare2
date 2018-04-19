package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIAttackMeleeLongRange extends NpcAIAttack<NpcBase> {

	private static int MAX_DELAY = 20;

	public NpcAIAttackMeleeLongRange(NpcBase npc) {
		super(npc);
		this.setMutexBits(ATTACK + MOVE);
	}

	@Override
	protected boolean shouldCloseOnTarget(double distanceToEntity) {
		double attackDistance = (double) ((this.npc.width * this.npc.width * 4.0F) + (getTarget().width * getTarget().width * 4.0F));
		return (distanceToEntity > attackDistance);
	}

	@Override
	protected void doAttack(double distanceToEntity) {
		npc.removeAITask(TASK_MOVE);
		if (getAttackDelay() <= 0) {
			npc.swingArm(EnumHand.MAIN_HAND);
			npc.attackEntityAsMob(getTarget());
			this.setAttackDelay(MAX_DELAY);//TODO set attack delay from npc-attributes?
			npc.addExperience(AWNPCStatics.npcXpFromAttack);
		}
	}
}
