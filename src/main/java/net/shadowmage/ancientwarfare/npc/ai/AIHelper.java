package net.shadowmage.ancientwarfare.npc.ai;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public final class AIHelper {
    private AIHelper(){}

    // Inserting QuiverBow ranged weapon usage here (for b78+)
    public static int doQuiverBowThing(EntityLiving entityLiving, Entity target){
        ItemStack held = entityLiving.getHeldItem();
        if (held!=null && Loader.isModLoaded("quiverchevsky")) {
            if (held.getItem() instanceof com.domochevsky.quiverbow.weapons._WeaponBase) {//A "quiver bow" is held
                com.domochevsky.quiverbow.weapons._WeaponBase weapon = (com.domochevsky.quiverbow.weapons._WeaponBase) held.getItem();
                if (weapon.isMobUsable()) {
                    entityLiving.faceEntity(target, 30.0F, 30.0F);
                    weapon.doSingleFire(held, entityLiving.world, entityLiving);//The "quiver bow" firing method
                    return weapon.getMaxCooldown() + 1; // The "quiver bow" attack delay

                }
            }
        }
        return 0;
    }

    public static boolean isTarget(NpcBase npc, EntityLivingBase target, boolean checkSight) {
        if (target == null || target == npc || !target.isEntityAlive() || !npc.canTarget(target)) {
            return false;
        } else if (target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.disableDamage) {
            return false;
        } else if (checkSight && !npc.getEntitySenses().canSee(target)) {
            return false;
        }
        return true;
    }
}
