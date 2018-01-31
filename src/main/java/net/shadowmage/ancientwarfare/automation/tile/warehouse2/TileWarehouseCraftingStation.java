package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.IBlockBreakHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public class TileWarehouseCraftingStation extends TileEntity implements IInteractableTile, IBlockBreakHandler {

	public InventoryCrafting layoutMatrix;
	public InventoryCraftResult result;
	public ItemStackHandler bookInventory = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			onInventoryChanged();
		}
	};

	NonNullList<ItemStack> matrixShadow;

	public TileWarehouseCraftingStation() {
		Container c = new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer var1) {
				return true;
			}

			@Override
			public void onCraftMatrixChanged(IInventory par1iInventory) {
				onInventoryChanged();
			}
		};

		layoutMatrix = new InventoryCrafting(c, 3, 3);
		matrixShadow = NonNullList.withSize(layoutMatrix.getSizeInventory(), ItemStack.EMPTY);
		result = new InventoryCraftResult();
	}

	/*
	 * called to shadow a copy of the input matrix, to know what to refill
	 */
	public void preItemCrafted() {
		@Nonnull ItemStack stack;
		for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
			stack = layoutMatrix.getStackInSlot(i);
			matrixShadow.set(i, stack.isEmpty() ? ItemStack.EMPTY : stack.copy());
		}
	}

	public void onItemCrafted() {
		TileWarehouseBase warehouse = getWarehouse();
		if (warehouse == null) {
			return;
		}
		AWLog.logDebug("crafting item...");
		int q;
		@Nonnull ItemStack layoutStack;
		for (int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
			layoutStack = matrixShadow.get(i);
			if (layoutStack.isEmpty()) {
				continue;
			}
			if (!layoutMatrix.getStackInSlot(i).isEmpty()) {
				continue;
			}
			q = warehouse.getCountOf(layoutStack);
			AWLog.logDebug("warehouse count of: " + layoutStack + " :: " + q);
			if (q > 0) {
				warehouse.decreaseCountOf(layoutStack, 1);
				layoutStack = layoutStack.copy();
				layoutStack.setCount(1);
				layoutMatrix.setInventorySlotContents(i, layoutStack);
			}
		}
		if (!world.isRemote) {
			warehouse.updateViewers();
		}
	}

	public final TileWarehouseBase getWarehouse() {
		if (pos.getY() <= 1)//could not possibly be a warehouse below...
		{
			return null;
		}
		TileEntity te = world.getTileEntity(pos.down());
		if (te instanceof TileWarehouseBase) {
			return (TileWarehouseBase) te;
		}
		return null;
	}

	private void onLayoutMatrixChanged() {
		result.setInventorySlotContents(0, AWCraftingManager.findMatchingRecipe(layoutMatrix, world, getCrafterName()));
		result.setRecipeUsed(CraftingManager.findMatchingRecipe(layoutMatrix, world));
	}

	public String getCrafterName() {
		return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		onLayoutMatrixChanged();
	}

	private void onInventoryChanged() {
		onLayoutMatrixChanged();
		markDirty();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bookInventory.deserializeNBT(tag.getCompoundTag("bookInventory"));
		InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
		InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("bookInventory", bookInventory.serializeNBT());

		NBTTagCompound inventoryTag = InventoryTools.writeInventoryToNBT(result, new NBTTagCompound());
		tag.setTag("resultInventory", inventoryTag);

		inventoryTag = InventoryTools.writeInventoryToNBT(layoutMatrix, new NBTTagCompound());
		tag.setTag("layoutMatrix", inventoryTag);

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
		InventoryTools.dropItemsInWorld(world, layoutMatrix, pos);
		InventoryTools.dropItemsInWorld(world, bookInventory, pos);
	}
}
