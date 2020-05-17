package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.shadowmage.ancientwarfare.core.item.ItemBlockBase;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

public class ItemBlockGravestone extends ItemBlockBase {
	public ItemBlockGravestone(Block block) {
		super(block);
	}

	public static int getVariant(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("variant") : 1;
	}

	public static ItemStack getVariantStack(int variant) {
		ItemStack stack = new ItemStack(AWStructureBlocks.GRAVESTONE);
		stack.setTagCompound(new NBTBuilder().setInteger("variant", variant).build());
		return stack;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack.hasTagCompound()) {
			String name = "tile.gravestone." + getVariant(stack) + ".name";
			return I18n.translateToLocal(name);
		}

		return super.getItemStackDisplayName(stack);
	}
}
