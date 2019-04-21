package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.structure.util.WoodVariantHelper;

public class WoodItemBlock extends ItemBlockBase {
	public WoodItemBlock(Block block) {
		super(block);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "_" + WoodVariantHelper.getVariant(stack).getName();
	}
}
