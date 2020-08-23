package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.AbstractHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.IHorseMountedNpc;

public class NpcAIFactionRideHorse<T extends NpcBase & IHorseMountedNpc> extends NpcAIRideHorse<T> {
	public NpcAIFactionRideHorse(T npc) {
		super(npc, 1.5);
	}

	@Override
	protected boolean shouldRideHorse() {
		return npc.isHorseAlive() && (npc.getRidingEntity() == null || horse != npc.getRidingEntity());
	}

	@Override
	public void startExecuting() {
		if (horse == null && npc.isHorseAlive()) {
			if (npc.getRidingEntity() instanceof EntityLiving) {
				horse = (EntityLiving) npc.getRidingEntity();
			} else {
				spawnHorse();
			}
		} else if (horse != null && horse.isDead) {
			npc.setHorseKilled();
			horse = null;
		}
	}

	private void spawnHorse() {
		EntityLiving horse = npc.instantiateMountedEntity();
		horse.setLocationAndAngles(npc.posX, npc.posY, npc.posZ, npc.rotationYaw, npc.rotationPitch);
		horse.onInitialSpawn(npc.world.getDifficultyForLocation(npc.getPosition()), null);
		if (horse instanceof AbstractHorse) {
			AbstractHorse h = (AbstractHorse) horse;
			h.setGrowingAge(0);
			h.setHorseTamed(true);
		}

		this.horse = horse;
		npc.world.spawnEntity(horse);
		npc.startRiding(horse);
		onMountHorse();
	}
}
