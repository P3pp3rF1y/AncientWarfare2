package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

public class TileColored extends TileUpdatable {
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

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		readNBT(compound);
	}

	private void readNBT(NBTTagCompound compound) {
		customColor = compound.getBoolean("customColor");
		dyeColor = compound.getInteger("dyeColor");
		color = compound.getInteger("color");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeNBT(compound);
		return super.writeToNBT(compound);
	}

	private void writeNBT(NBTTagCompound compound) {
		compound.setBoolean("customColor", customColor);
		compound.setInteger("dyeColor", dyeColor);
		compound.setInteger("color", color);
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
