package net.shadowmage.ancientwarfare.npc.ai.faction;

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
		return npc.isHorseAlive() && npc.isRiding() || horse != npc.getRidingEntity();
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
		EntityHorse horse = new EntityHorse(npc.world);
		horse.setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
		horse.setGrowingAge(0); //TODO there used to be loop here to make sure horse is grown - test that child horses don't get spawned
		horse.onInitialSpawn(npc.world.getDifficultyForLocation(npc.getPosition()), null);
		horse.setHorseTamed(true);
		this.horse = horse;
		npc.world.spawnEntity(horse);
		npc.startRiding(horse);
		onMountHorse();
	}
}
