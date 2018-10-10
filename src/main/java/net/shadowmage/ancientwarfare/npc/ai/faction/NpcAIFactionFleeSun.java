package net.shadowmage.ancientwarfare.npc.ai.faction;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

import javax.annotation.Nullable;
import java.util.Random;

public class NpcAIFactionFleeSun extends EntityAIBase {
	private final World world;
	private double movementSpeed;
	private final NpcFaction npc;
	private double shelterX;
	private double shelterY;
	private double shelterZ;

	public NpcAIFactionFleeSun(NpcFaction npc, double movementSpeed) {
		this.npc = npc;
		this.world = npc.world;
		this.movementSpeed = movementSpeed;
	}

	@Override
	public boolean shouldExecute() {
		if (!world.isDaytime()) {
			return false;
		} else if (!npc.isBurning()) {
			return false;
		} else if (!world.canSeeSky(new BlockPos(npc.posX, npc.getEntityBoundingBox().minY, npc.posZ))) {
			return false;
		} else if (!npc.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
			return false;
		} else {
			Vec3d vec3d = findPossibleShelter();

			if (vec3d == null) {
				return false;
			} else {
				shelterX = vec3d.x;
				shelterY = vec3d.y;
				shelterZ = vec3d.z;
				return true;
			}
		}
	}

	public boolean shouldContinueExecuting() {
		return !npc.getNavigator().noPath();
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		npc.getNavigator().tryMoveToXYZ(shelterX, shelterY, shelterZ, movementSpeed);
	}

	@Nullable
	private Vec3d findPossibleShelter() {
		Random random = npc.getRNG();
		BlockPos entityPos = new BlockPos(npc.posX, getMinY(), npc.posZ);

		for (int i = 0; i < 10; ++i) {
			BlockPos randomPos = entityPos.add(random.nextInt(20) - 10, random.nextInt(6) - 3, random.nextInt(20) - 10);

			if (!world.canSeeSky(randomPos) && npc.getBlockPathWeight(randomPos) > 0.0F) {
				return new Vec3d((double) randomPos.getX(), (double) randomPos.getY(), (double) randomPos.getZ());
			}
		}

		return null;
	}

	private double getMinY() {
		//noinspection ConstantConditions
		return npc.isRiding() ? npc.getRidingEntity().getEntityBoundingBox().minY : npc.getEntityBoundingBox().minY;
	}
}
