package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

public class TileDecorativeFlag extends TileFlag {
	@Override
	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.DECORATIVE_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		stack.setTagCompound(tag);
		return stack;
	}
}
