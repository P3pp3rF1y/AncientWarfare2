package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemBlockBase {

    public ItemBlockMeta(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + stack.getItemDamage();
    }

    @Override
    public int getMetadata(int itemDamage) {
        return itemDamage;
    }

}
