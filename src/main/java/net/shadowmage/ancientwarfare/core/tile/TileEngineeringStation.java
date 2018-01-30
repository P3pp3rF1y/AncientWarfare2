package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;

public class TileEngineeringStation extends TileUpdatable implements IRotatableTile {

	EnumFacing facing = EnumFacing.NORTH;
	NonNullList<ItemStack> matrixShadow;

	public InventoryCrafting layoutMatrix;
	public InventoryCraftResult result;
	public final ItemStackHandler bookInventory;
	public final ItemStackHandler extraSlots;

	public TileEngineeringStation() {
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
		bookInventory = new ItemStackHandler(1) {
			@Override
			protected void onContentsChanged(int slot) {
				onInventoryChanged();
			}
		};

		result = new InventoryCraftResult();
		extraSlots = new ItemStackHandler(18) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
			}
		};
	}

	public String getCrafterName() {
		return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
	}

	/*
	 * called to shadow a copy of the input matrix, to know what to refill
	 */
	public void preItemCrafted() {
		@Nonnull ItemStack stack;
		for(int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
			stack = layoutMatrix.getStackInSlot(i);
			matrixShadow.set(i, stack.copy());
		}
	}

	public void onItemCrafted() {
		@Nonnull ItemStack layoutStack;
		for(int i = 0; i < layoutMatrix.getSizeInventory(); i++) {
			layoutStack = matrixShadow.get(i);
			if(layoutStack.isEmpty()) {
				continue;
			}
			if(!layoutMatrix.getStackInSlot(i).isEmpty()) {
				continue;
			}
			layoutMatrix.setInventorySlotContents(i, InventoryTools.removeItems(extraSlots, layoutStack, 1));
		}
	}

	private void onLayoutMatrixChanged() {
		result.setInventorySlotContents(0, AWCraftingManager.findMatchingRecipe(layoutMatrix, world, getCrafterName()));
		result.setRecipeUsed(CraftingManager.findMatchingRecipe(layoutMatrix, world));
	}

	private void onInventoryChanged() {
		onLayoutMatrixChanged();
		markDirty();
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		onLayoutMatrixChanged();
	}

	@Override
	protected void writeUpdateNBT(NBTTagCompound tag) {
		super.writeUpdateNBT(tag);
		tag.setInteger("facing", facing.ordinal());
	}

	@Override
	protected void handleUpdateNBT(NBTTagCompound tag) {
		super.handleUpdateNBT(tag);
		facing = EnumFacing.VALUES[tag.getInteger("facing")];
		BlockTools.notifyBlockUpdate(this);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		bookInventory.deserializeNBT(tag.getCompoundTag("bookInventory"));
		extraSlots.deserializeNBT(tag.getCompoundTag("extraInventory"));
		InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
		InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
		facing = EnumFacing.values()[tag.getInteger("facing")];
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		tag.setTag("bookInventory", bookInventory.serializeNBT());
		tag.setTag("extraInventory", extraSlots.serializeNBT());

		NBTTagCompound inventoryTag = new NBTTagCompound();
		InventoryTools.writeInventoryToNBT(result, inventoryTag);
		tag.setTag("resultInventory", inventoryTag);

		inventoryTag = new NBTTagCompound();
		InventoryTools.writeInventoryToNBT(layoutMatrix, inventoryTag);
		tag.setTag("layoutMatrix", inventoryTag);

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
		InventoryTools.dropItemsInWorld(world, bookInventory, pos);
		InventoryTools.dropItemsInWorld(world, extraSlots, pos);
		InventoryTools.dropItemsInWorld(world, layoutMatrix, pos);
	}
}
