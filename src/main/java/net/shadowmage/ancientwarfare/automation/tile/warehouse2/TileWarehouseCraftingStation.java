package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.CraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.tile.TileUpdatable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;

public class TileWarehouseCraftingStation extends TileUpdatable implements IInteractableTile, IBlockBreakHandler {

	public CraftingRecipeMemory craftingRecipeMemory = new CraftingRecipeMemory(this);

	@Nullable
	public final TileWarehouse getWarehouse() {
		if (pos.getY() <= 1)//could not possibly be a warehouse below...
		{
			return null;
		}
		return WorldTools.getTile(world, pos.down(), TileWarehouse.class).orElse(null);
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
	public void onBlockBroken(IBlockState state) {
		craftingRecipeMemory.dropInventory();
	}
}
