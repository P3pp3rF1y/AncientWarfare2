package net.shadowmage.ancientwarfare.npc.ai;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;

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
                    weapon.doSingleFire(held, entityLiving.worldObj, entityLiving);//The "quiver bow" firing method
                    return weapon.getMaxCooldown() + 1; // The "quiver bow" attack delay

                }
            }
        }
        return 0;
    }
}
