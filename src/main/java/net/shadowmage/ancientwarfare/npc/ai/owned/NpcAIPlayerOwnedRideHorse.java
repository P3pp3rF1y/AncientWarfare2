package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.minecraft.entity.passive.AbstractHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class NpcAIPlayerOwnedRideHorse extends NpcAIRideHorse<NpcPlayerOwned> {
	private boolean saddled = false;

	public NpcAIPlayerOwnedRideHorse(NpcPlayerOwned npc) {
		super(npc, 1.0);
	}

	@Override
	public boolean shouldContinueExecuting() {
		return horse != null;
	}

	@Override
	public void updateTask() {
		if (horse != npc.getRidingEntity() && horse != null) {
			onDismountHorse();
			horse = null;
		}
	}

	@Override
	public void resetTask() {
		if (horse != null) {
			onDismountHorse();
		}
		horse = null;
	}

	@Override
	protected void onMountHorse() {
		if (horse instanceof AbstractHorse) {
			this.saddled = ((AbstractHorse) horse).isHorseSaddled();
		}
		super.onMountHorse();
	}

	@Override
	protected void onDismountHorse() {
		super.onDismountHorse();
		if (horse instanceof AbstractHorse) {
			((AbstractHorse) horse).setHorseSaddled(saddled);
		}
	}
}
