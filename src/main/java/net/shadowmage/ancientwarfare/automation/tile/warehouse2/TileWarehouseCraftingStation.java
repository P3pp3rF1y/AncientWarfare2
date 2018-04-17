package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.automation.tile.CraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;

public class TileWarehouseCraftingStation extends TileUpdatable implements IInteractableTile, IBlockBreakHandler {

	public CraftingRecipeMemory craftingRecipeMemory = new CraftingRecipeMemory(this);

	public final TileWarehouse getWarehouse() {
		if (pos.getY() <= 1)//could not possibly be a warehouse below...
		{
			return null;
		}
		TileEntity te = world.getTileEntity(pos.down());
		if (te instanceof TileWarehouseBase) {
			return (TileWarehouse) te;
		}
		return null;
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		craftingRecipeMemory.writeToNBT(tag);
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		craftingRecipeMemory.readFromNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		craftingRecipeMemory.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		craftingRecipeMemory.writeToNBT(tag);
		return tag;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_CRAFTING, pos);
		}
		return true;
	}

	@Override
	public void onBlockBroken() {
		craftingRecipeMemory.dropInventory();
	}
}
