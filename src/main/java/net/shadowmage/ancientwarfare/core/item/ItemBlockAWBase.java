package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public abstract class ItemBlockAWBase extends ItemBlock {
    public ItemBlockAWBase(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
    }
}
