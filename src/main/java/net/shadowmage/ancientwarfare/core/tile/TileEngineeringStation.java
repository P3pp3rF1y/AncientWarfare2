package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileEngineeringStation extends TileUpdatable implements IRotatableTile {

	public CraftingRecipeMemory craftingRecipeMemory = new CraftingRecipeMemory(this);
	EnumFacing facing = EnumFacing.NORTH;
	public final ItemStackHandler extraSlots;

	public TileEngineeringStation() {
		extraSlots = new ItemStackHandler(18) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
			}
		};
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger("facing", facing.ordinal());
		craftingRecipeMemory.writeToNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		facing = EnumFacing.VALUES[tag.getInteger("facing")];
		BlockTools.notifyBlockUpdate(this);
		craftingRecipeMemory.readFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		craftingRecipeMemory.readFromNBT(tag);
		extraSlots.deserializeNBT(tag.getCompoundTag("extraInventory"));
		facing = EnumFacing.values()[tag.getInteger("facing")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		craftingRecipeMemory.writeToNBT(tag);
		tag.setTag("extraInventory", extraSlots.serializeNBT());
		tag.setInteger("facing", facing.ordinal());
		return tag;
	}

	@Override
	public EnumFacing getPrimaryFacing() {
		return facing;
	}

	@Override
	public void setPrimaryFacing(EnumFacing face) {
		this.facing = face;
		BlockTools.notifyBlockUpdate(this);
	}

	public void onBlockBreak() {
		craftingRecipeMemory.dropInventory();
		InventoryTools.dropItemsInWorld(world, extraSlots, pos);
	}
}
