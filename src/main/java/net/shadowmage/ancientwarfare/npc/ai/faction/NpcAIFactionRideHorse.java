package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.IHorseMountedNpc;

public class NpcAIFactionRideHorse<T extends NpcBase & IHorseMountedNpc> extends NpcAIRideHorse<T> {
	public NpcAIFactionRideHorse(T npc) {
		super(npc, 1.5);
	}

	@Override
	public boolean shouldExecute() {
		return npc.isHorseAlive() && (npc.getRidingEntity() == null || horse != npc.getRidingEntity());
	}

	@Override
	public void startExecuting() {
		if (horse == null && npc.isHorseAlive()) {
			if (npc.getRidingEntity() instanceof EntityHorse) {
				horse = (EntityHorse) npc.getRidingEntity();
			} else {
				spawnHorse();
			}
		} else if (horse != null && horse.isDead) {
			npc.setHorseKilled();
			horse = null;
		}
	}

	private void spawnHorse() {
		AbstractHorse horse = npc.instantiateHorseEntity();
		horse.setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
		horse.onInitialSpawn(npc.world.getDifficultyForLocation(npc.getPosition()), null);
		horse.setGrowingAge(0);
		horse.setHorseTamed(true);
		this.horse = horse;
		npc.world.spawnEntity(horse);
		npc.startRiding(horse);
		onMountHorse();
	}
}
