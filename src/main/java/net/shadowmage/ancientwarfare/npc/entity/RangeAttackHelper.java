package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;

public final class RangeAttackHelper {
    private RangeAttackHelper(){}

    //TODO clean this up, increase max attack distance
    //TODO get attack damage to use from monster attributes
    public static void doRangedAttack(EntityLivingBase attacker, EntityLivingBase target, float force){
        EntityArrow entityarrow = new EntityArrow(attacker.worldObj, attacker, target, 1.6F, (float) (14 - attacker.worldObj.difficultySetting.getDifficultyId() * 4));

        int bonusDamage = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, attacker.getHeldItem());
        int knockBackStrenght = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, attacker.getHeldItem());

        entityarrow.setDamage((double) (force * 2.0F) + attacker.getRNG().nextGaussian() * 0.25D + (double) ((float) attacker.worldObj.difficultySetting.getDifficultyId() * 0.11F));

        if (bonusDamage > 0) {
            entityarrow.setDamage(entityarrow.getDamage() + (double) bonusDamage * 0.5D + 0.5D);
        }

        knockBackStrenght /= 2;
        if (knockBackStrenght > 0) {
            entityarrow.setKnockbackStrength(knockBackStrenght);
        }

        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, attacker.getHeldItem()) > 0) {
            entityarrow.setFire(100);
        }

        attacker.playSound("random.bow", 1.0F, 1.0F / (attacker.getRNG().nextFloat() * 0.4F + 0.8F));
        attacker.worldObj.spawnEntityInWorld(entityarrow);
    }
}
