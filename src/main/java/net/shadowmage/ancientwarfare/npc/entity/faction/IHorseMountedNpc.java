package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.passive.AbstractHorse;

public interface IHorseMountedNpc {
	boolean isHorseAlive();

	void setHorseKilled();

	AbstractHorse instantiateHorseEntity();
}
