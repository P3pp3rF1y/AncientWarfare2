package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;

public class TileAltarCandle extends TileColored {
	private static final String FLAME_COLOR_TAG = "flameColor";
	private static final String FLAME_SMOKE_TAG = "flameSmoke";

	private int flameColor = -1;
	private boolean flameSmoke = false;

	public int getFlameColor() {
		return flameColor;
	}

	public boolean isFlameSmoke() {
		return flameSmoke;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void setFromStack(ItemStack stack) {
		super.setFromStack(stack);
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag.hasKey(FLAME_COLOR_TAG)) {
				flameColor = tag.getInteger(FLAME_COLOR_TAG);
			}
			if (tag.hasKey(FLAME_SMOKE_TAG)) {
				flameSmoke = tag.getBoolean(FLAME_SMOKE_TAG);
			}
		}
	}

	@Override
	protected void readNBT(NBTTagCompound compound) {
		super.readNBT(compound);
		if (compound.hasKey(FLAME_COLOR_TAG)) {
			flameColor = compound.getInteger(FLAME_COLOR_TAG);
		}
		flameSmoke = compound.getBoolean(FLAME_SMOKE_TAG);
	}

	@Override
	protected void writeNBT(NBTTagCompound compound) {
		super.writeNBT(compound);
		if (flameColor != -1) {
			compound.setInteger(FLAME_COLOR_TAG, flameColor);
		}
		if (flameSmoke) {
			compound.setBoolean(FLAME_SMOKE_TAG, true);
		}
	}

	@Override
	public ItemStack getPickBlock() {
		ItemStack stack = super.getPickBlock();
		if (flameColor != -1) {
			stack.setTagInfo(FLAME_COLOR_TAG, new NBTTagInt(flameColor));
		}
		if (flameSmoke) {
			stack.setTagInfo(FLAME_SMOKE_TAG, new NBTTagByte((byte) 1));
		}
		return stack;
	}

}
