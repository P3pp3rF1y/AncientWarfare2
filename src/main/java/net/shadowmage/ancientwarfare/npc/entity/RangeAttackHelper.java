package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;


public final class RangeAttackHelper {
    /**
     * @author Funwayguy for the speedFactor (range) calculations 
     */
    public static void doRangedAttack(EntityLivingBase attacker, EntityLivingBase target, float force, float precision) {
        double targetDist = attacker.getDistance(target.posX + (target.posX - target.lastTickPosX), target.boundingBox.minY, target.posZ + (target.posZ - target.lastTickPosZ));
        float speedFactor = (float)((0.00013*(targetDist)*(targetDist)) + (0.02*targetDist) + 1.25);

        EntityArrow entityarrow = new EntityArrow(attacker.worldObj, attacker, target, speedFactor, precision);
        

        entityarrow.setDamage(force * 2.0D + attacker.getRNG().nextGaussian() * 0.25D);
        
        int bonus = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, attacker.getHeldItem());
        if (bonus > 0) {
            entityarrow.setDamage(entityarrow.getDamage() + bonus * 0.5D + 0.5D);
        }

        bonus = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, attacker.getHeldItem());
        if (bonus > 0) {
            entityarrow.setKnockbackStrength(bonus);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, attacker.getHeldItem()) > 0) {
            entityarrow.setFire(100);
        }

        attacker.playSound("random.bow", 1.0F, 1.0F / (attacker.getRNG().nextFloat() * 0.4F + 0.8F));
        attacker.worldObj.spawnEntityInWorld(entityarrow);
    }
}
