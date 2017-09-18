package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public abstract class ItemAWBase extends Item {
    public ItemAWBase(String modID, String regName) {
        super();
        setUnlocalizedName(regName);
        setRegistryName(new ResourceLocation(modID, regName));
    }
}
