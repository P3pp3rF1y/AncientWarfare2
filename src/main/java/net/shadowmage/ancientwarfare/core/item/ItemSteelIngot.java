package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

public class ItemSteelIngot extends Item {

    public ItemSteelIngot(String regName) {
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setTextureName("ancientwarfare:core/steel_ingot");
    }

}
