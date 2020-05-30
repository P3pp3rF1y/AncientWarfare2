package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;

public class TileDecorativeFlag extends TileFlag {

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}

	private void readNBT(NBTTagCompound tag) {
		name = tag.getString(NAME_TAG);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = writeNBT(super.writeToNBT(compound));
		return tag;
	}

	private NBTTagCompound writeNBT(NBTTagCompound tag) {
		tag.setString(NAME_TAG, name);
		return tag;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.DECORATIVE_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		stack.setTagCompound(tag);
		return stack;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			readNBT(tag);
		}
	}
}
