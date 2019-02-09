package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;

public class TileColored extends TileUpdatable {
	private static final String DYE_COLOR_TAG = "dyeColor";
	private static final String COLOR_TAG = "color";
	private static final String UNLOCALIZED_NAME_PART_TAG = "unlocalizedNamePart";
	private static final String CUSTOM_DATA_TAG = "customData";
	private boolean customColor = false;
	private int dyeColor = -1;
	private int color = -1;
	private String customData;
	private String unlocalizedNamePart;

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
			item.setTagCompound(
					new NBTBuilder().setInteger(COLOR_TAG, color).setString(CUSTOM_DATA_TAG, customData).setString(UNLOCALIZED_NAME_PART_TAG, unlocalizedNamePart).build());
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

	protected void readNBT(NBTTagCompound compound) {
		customColor = compound.getBoolean("customColor");
		dyeColor = compound.getInteger(DYE_COLOR_TAG);
		if (compound.hasKey(COLOR_TAG)) {
			color = compound.getInteger(COLOR_TAG);
		}
		customData = compound.getString(CUSTOM_DATA_TAG);
		unlocalizedNamePart = compound.getString(UNLOCALIZED_NAME_PART_TAG);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		writeNBT(compound);
		return super.writeToNBT(compound);
	}

	protected void writeNBT(NBTTagCompound compound) {
		compound.setBoolean("customColor", customColor);
		if (dyeColor != -1) {
			compound.setInteger(DYE_COLOR_TAG, dyeColor);
		}
		if (color != -1) {
			compound.setInteger(COLOR_TAG, color);
		}
		if (customData != null) {
			compound.setString(CUSTOM_DATA_TAG, customData);
		}
		if (unlocalizedNamePart != null) {
			compound.setString(UNLOCALIZED_NAME_PART_TAG, unlocalizedNamePart);
		}
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

	public void setCustomData(String customData) {
		this.customData = customData;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (!stack.hasTagCompound()) {
			return;
		}
		NBTTagCompound tag = stack.getTagCompound();
		if (tag.hasKey(DYE_COLOR_TAG)) {
			setDyeColor(tag.getInteger(DYE_COLOR_TAG));
		} else if (tag.hasKey(COLOR_TAG)) {
			setColor(tag.getInteger(COLOR_TAG));
			customData = tag.getString(CUSTOM_DATA_TAG);
			unlocalizedNamePart = tag.getString(UNLOCALIZED_NAME_PART_TAG);
		}
	}
}
