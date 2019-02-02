package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

public class TileColored extends TileUpdatable {
	private static final String DYE_COLOR_TAG = "dyeColor";
	private static final String COLOR_TAG = "color";
	private boolean customColor = false;
	private int dyeColor = -1;
	private int color = -1;

	public void setDyeColor(int dyeColor) {
		this.dyeColor = dyeColor;
		customColor = false;
	}

	public void setColor(int color) {
		this.color = color;
		customColor = true;
	}

	@SideOnly(Side.CLIENT)
	public int getColor() {
		if (customColor) {
			return color;
		}

		return EnumDyeColor.byDyeDamage(dyeColor).getColorValue();
	}

	public ItemStack getPickBlock() {
		ItemStack item = new ItemStack(world.getBlockState(pos).getBlock());
		if (customColor) {
			item.setTagInfo(COLOR_TAG, new NBTTagInt(color));
		} else {
			item.setTagInfo(DYE_COLOR_TAG, new NBTTagInt(dyeColor));
		}
		return item;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	private void readNBT(NBTTagCompound compound) {
		customColor = compound.getBoolean("customColor");
		dyeColor = compound.getInteger(DYE_COLOR_TAG);
		color = compound.getInteger(COLOR_TAG);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeNBT(compound);
		return super.writeToNBT(compound);
	}

	private void writeNBT(NBTTagCompound compound) {
		compound.setBoolean("customColor", customColor);
		compound.setInteger(DYE_COLOR_TAG, dyeColor);
		compound.setInteger(COLOR_TAG, color);
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		writeNBT(tag);
		super.writeUpdateNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
	}
}
