package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

public class NpcAIFactionPanic extends EntityAIPanic {

	public NpcAIFactionPanic(EntityCreature creature, double speedIn) {
		super(creature, speedIn);
	}

	@Override
	protected boolean findRandomPosition() {

		Vec3d vec3d;
		if (creature.getRevengeTarget() != null) {
			vec3d = RandomPositionGenerator.findRandomTargetBlockAwayFrom(creature, 10, 4, creature.getRevengeTarget().getPositionVector());
		} else {
			vec3d = RandomPositionGenerator.findRandomTarget(creature, 5, 4);
		}

		if (vec3d == null) {
			return false;
		} else {
			randPosX = vec3d.x;
			randPosY = vec3d.y;
			randPosZ = vec3d.z;
			return true;
		}
	}

}
