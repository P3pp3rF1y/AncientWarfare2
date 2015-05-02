package com.domochevsky.quiverbow.weapons;

import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * The basic signature needed from QuiverBow
 */
public class _WeaponBase extends Item {
    public boolean isMobUsable(){
        return true;
    }

    public void doSingleFire(ItemStack held, World worldObj, EntityLiving entityLiving){}

    public int getMaxCooldown(){
        return 0;
    }
}
