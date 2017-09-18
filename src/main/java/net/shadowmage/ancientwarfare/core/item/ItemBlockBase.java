package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public abstract class ItemBlockBase extends ItemBlock {
    public ItemBlockBase(Block block) {
        super(block);
        setRegistryName(block.getRegistryName());
    }
}
