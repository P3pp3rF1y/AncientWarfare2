package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public final class AIHelper {
	private AIHelper() {
	}

	private static Set<Integer> additionalHostileEntitiesToTarget = new HashSet<>();

	public static boolean isTarget(NpcBase npc, @Nullable EntityLivingBase target, boolean checkSight) {
		return target != null && !(target == npc || !target.isEntityAlive() || !npc.canTarget(target)
				|| target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage
				|| checkSight && !npc.getEntitySenses().canSee(target));
	}

	public static boolean isWithinFollowRange(EntityLiving entity, EntityLivingBase target) {
		return entity.getDistance(target) <= entity.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue();
	}

	public static void addHostileEntityToTarget(Entity e) {
		additionalHostileEntitiesToTarget.add(e.getEntityId());
	}

	public static void removeHostileEntityToTarget(Entity e) {
		additionalHostileEntitiesToTarget.remove(e.getEntityId());
	}

	public static boolean isAdditionalEntityToTarget(Entity e) {
		return additionalHostileEntitiesToTarget.contains(e.getEntityId());
	}

	public static Optional<EntityPlayer> getOwnerPlayer(IEntityOwnable entity, World world) {
		if (entity.getOwner() != null) {
			EntityPlayer player = world.getPlayerEntityByUUID(entity.getOwner().getUniqueID());

			return player != null ? Optional.of(player) : Optional.empty();
		}
		return Optional.empty();
	}

}
