package net.shadowmage.ancientwarfare.npc.ai.owned;

import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAIPlayerOwnedRideHorse extends NpcAIRideHorse {
	private boolean saddled = false;

	public NpcAIPlayerOwnedRideHorse(NpcBase npc) {
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
		this.saddled = horse.isHorseSaddled();
		super.onMountHorse();
	}

	@Override
	protected void onDismountHorse() {
		super.onDismountHorse();
		horse.setHorseSaddled(saddled);
	}
}
