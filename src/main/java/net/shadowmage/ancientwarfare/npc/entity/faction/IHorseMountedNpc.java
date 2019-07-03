package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLiving;

public interface IHorseMountedNpc {
	boolean isHorseAlive();

	void setHorseKilled();

	EntityLiving instantiateMountedEntity();
}
