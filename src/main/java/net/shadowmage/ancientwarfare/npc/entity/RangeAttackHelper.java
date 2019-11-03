package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public final class RangeAttackHelper {
	private RangeAttackHelper() {}

	/*
	 * @author Funwayguy for the speedFactor (range) calculations
	 */
	public static void doRangedAttack(EntityLivingBase attacker, EntityLivingBase target, float force, float inaccuracy) {
		double targetDist = attacker.getDistance(target.posX + (target.posX - target.lastTickPosX), target.getEntityBoundingBox().minY, target.posZ + (target.posZ - target.lastTickPosZ));
		float speedFactor = (float) ((0.00013 * (targetDist) * (targetDist)) + (0.02 * targetDist) + 1.25);

		EntityArrow entityarrow = new EntityTippedArrow(attacker.world, attacker);
		double d0 = target.posX - attacker.posX;
		double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entityarrow.posY;
		double d2 = target.posZ - attacker.posZ;
		double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
		entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, speedFactor, inaccuracy);

		entityarrow.setDamage(force * 2.0D + attacker.getRNG().nextGaussian() * 0.25D);

		int bonus = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, attacker.getHeldItemMainhand());
		if (bonus > 0) {
			entityarrow.setDamage(entityarrow.getDamage() + bonus * 0.5D + 0.5D);
		}

		bonus = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, attacker.getHeldItemMainhand());
		if (bonus > 0) {
			entityarrow.setKnockbackStrength(bonus);
		}

		if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, attacker.getHeldItemMainhand()) > 0) {
			entityarrow.setFire(100);
		}

		attacker.playSound(SoundEvents.ENTITY_ARROW_SHOOT, 1.0F, 1.0F / (attacker.getRNG().nextFloat() * 0.4F + 0.8F));
		attacker.world.spawnEntity(entityarrow);
	}
}
