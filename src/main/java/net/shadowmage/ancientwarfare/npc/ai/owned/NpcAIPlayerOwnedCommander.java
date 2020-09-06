package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.List;

public class NpcAIPlayerOwnedCommander extends NpcAI<NpcBase> {
	private int lastExecuted = -1;
	protected PotionEffect effect = new PotionEffect(MobEffects.STRENGTH, 20);//apply 1 second strength potion, times 1.3 damage

	public NpcAIPlayerOwnedCommander(NpcBase npc) {
		super(npc);
	}

	protected boolean canBeCommanded(NpcBase npc) {
		return npc.hasCommandPermissions(npc.getOwner());
	}

	@Override
	public boolean shouldExecute() {
		if (!super.shouldExecute() || isNotCommander(npc)) {
			return false;
		}
		return (lastExecuted == -1 || npc.ticksExisted - lastExecuted > 40);//Wait 2 seconds
	}

	protected boolean isNotCommander(NpcBase npc) {
		return !npc.getNpcSubType().equals("commander");
	}

	@Override
	public void startExecuting() {
		lastExecuted = npc.ticksExisted;
		double dist = npc.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
		AxisAlignedBB bb = npc.getEntityBoundingBox().expand(dist, dist / 2, dist);
		List<NpcBase> potentialTargets = npc.world.getEntitiesWithinAABB(NpcBase.class, bb,
				n -> n != null && canBeCommanded(n) && isNotCommander(n) && !n.isPotionActive(effect.getPotion()));
		for (NpcBase npcBase : potentialTargets) {
			npcBase.addPotionEffect(new PotionEffect(effect));
		}
	}

	@Override
	public void updateTask() {
		//noop
	}

	@Override
	public void resetTask() {
		//noop
	}

}
