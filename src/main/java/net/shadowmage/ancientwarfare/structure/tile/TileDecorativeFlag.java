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
		topColor = tag.getInteger("topColor");
		bottomColor = tag.getInteger("bottomColor");
		name = tag.getString("name");
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
		name = compound.getString(NAME_TAG);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		writeNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound tag = writeNBT(super.writeToNBT(compound));
		tag.setString(NAME_TAG, name);
		return tag;
	}

	private NBTTagCompound writeNBT(NBTTagCompound tag) {
		tag.setInteger("topColor", topColor);
		tag.setInteger("bottomColor", bottomColor);
		tag.setString("name", name);
		return tag;
	}

	public ItemStack getItemStack() {
		ItemStack stack = new ItemStack(AWStructureBlocks.DECORATIVE_FLAG);
		NBTTagCompound tag = new NBTTagCompound();
		writeNBT(tag);
		tag.setString(NAME_TAG, name);
		stack.setTagCompound(tag);
		return stack;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			readNBT(tag);
			name = tag.getString(NAME_TAG);
		}
	}
}
