package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.structure.block.BlockFirePit;

public class ItemBlockFirePit extends ItemBlockBase {
	public ItemBlockFirePit(Block block) {
		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return super.getUnlocalizedName(stack);
		}

		//noinspection ConstantConditions
		return String.format("%s.%s.%s", super.getUnlocalizedName(stack),
				stack.getTagCompound().getString(BlockFirePit.VARIANT_TAG),
				stack.getTagCompound().getBoolean(BlockFirePit.LIT_TAG) ? "lit" : "unlit");
	}
}
