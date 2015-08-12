package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;

public final class RangeAttackHelper {
    public static final RangeAttackHelper DEFAULT = new RangeAttackHelper(1.6F, 14);
    private final float speedFactor, precisionFactor;
    private boolean difficultyBased = true;
    public RangeAttackHelper(float speed, float prec){
        speedFactor = speed;
        precisionFactor = prec;
    }

    public void removeDifficulty(){
        difficultyBased = false;
    }

    //TODO clean this up, increase max attack distance
    //TODO get attack damage to use from monster attributes
    public void doRangedAttack(EntityLivingBase attacker, EntityLivingBase target, float force){
        EntityArrow entityarrow = new EntityArrow(attacker.worldObj, attacker, target, speedFactor, getPrecisionFactor(attacker));

        entityarrow.setDamage(force * 2.0D + attacker.getRNG().nextGaussian() * 0.25D);
        if(difficultyBased){
            entityarrow.setDamage(entityarrow.getDamage() + attacker.worldObj.difficultySetting.getDifficultyId() * 0.11D);
        }
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

    /**
     * How much the arrow may deviate from its target
     * Between 0 and 1 to make more precise, above 1 for less precise
     */
    public float getPrecisionFactor(EntityLivingBase attacker){
        if(!difficultyBased)
            return precisionFactor;
        return precisionFactor - attacker.worldObj.difficultySetting.getDifficultyId() * 4;
    }
}
