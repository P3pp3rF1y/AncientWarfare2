package net.shadowmage.ancientwarfare.structure.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

import javax.annotation.Nullable;

public abstract class TileFlag extends TileUpdatable {
	private static final String NAME_TAG = "name";
	private String name = "";

	public boolean isPlayerOwned() {
		return false;
	}

	@Nullable
	public GameProfile getPlayerProfile() {
		return null;
	}

	public String getName() {
		return name;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, pos.add(1, 3, 1));
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		readNBT(tag);
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
		return writeNBT(super.writeToNBT(compound));
	}

	protected void readNBT(NBTTagCompound tag) {
		name = tag.getString(NAME_TAG);
	}

	protected NBTTagCompound writeNBT(NBTTagCompound tag) {
		tag.setString(NAME_TAG, name);
		return tag;
	}

	@SuppressWarnings("ConstantConditions")
	public void setFromStack(ItemStack stack) {
		if (stack.hasTagCompound()) {
			NBTTagCompound tag = stack.getTagCompound();
			readNBT(tag);
		}
	}

	public abstract ItemStack getItemStack();
}
