package net.shadowmage.ancientwarfare.core.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.NoRecipeWrapper;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.List;

public class CraftingRecipeMemory {
	private final TileEntity tileEntity;
	private ICraftingRecipe recipe = NoRecipeWrapper.INSTANCE;

	public ItemStackHandler bookSlot = new ItemStackHandler(1) {
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
			return ItemResearchBook.getResearcherName(stack) != null ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			tileEntity.markDirty();
		}
	};
	public InventoryCraftResult outputSlot = new InventoryCraftResult() {
		@Override
		public void setInventorySlotContents(int index, ItemStack stack) {
			super.setInventorySlotContents(index, stack);
		}
	};
	//TODO change this to ItemStackHandler?
	public InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
		@Override
		public boolean canInteractWith(EntityPlayer playerIn) {
			return true;
		}
	}, 3, 3) {
		@Override
		public void markDirty() {
			super.markDirty();
			tileEntity.markDirty();
			updateOutput(this);
		}
	};//the 3x3 recipe template/matrix

	public List<ItemStack> getCraftingStacks() {
		List<ItemStack> ret = NonNullList.create();
		for (int slot = 0; slot < craftMatrix.getSizeInventory(); slot++) {
			ret.add(craftMatrix.getStackInSlot(slot));
		}
		return ret;
	}

	public CraftingRecipeMemory(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}

	public void dropInventory() {
		InventoryTools.dropItemsInWorld(tileEntity.getWorld(), bookSlot, tileEntity.getPos());
		//TODO change the crafting matrix to be just shadow copy of the items that wouldn't require actual item and thus dropping?
		InventoryTools.dropItemsInWorld(tileEntity.getWorld(), craftMatrix, tileEntity.getPos());
	}

	@Nullable
	public String getCrafterName() {
		return ItemResearchBook.getResearcherName(bookSlot.getStackInSlot(0));
	}

	public void readFromNBT(NBTTagCompound tag) {
		bookSlot.deserializeNBT(tag.getCompoundTag("bookSlot"));
		InventoryTools.readInventoryFromNBT(outputSlot, tag.getCompoundTag("outputSlot"));
		InventoryTools.readInventoryFromNBT(craftMatrix, tag.getCompoundTag("craftMatrix"));
		recipe = AWCraftingManager.getRecipe(RecipeResourceLocation.deserialize(tag.getString("recipe")));
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setTag("bookSlot", bookSlot.serializeNBT());
		tag.setTag("outputSlot", InventoryTools.writeInventoryToNBT(outputSlot, new NBTTagCompound()));
		tag.setTag("craftMatrix", InventoryTools.writeInventoryToNBT(craftMatrix, new NBTTagCompound()));
		tag.setString("recipe", recipe.getRegistryName().toString());
		return tag;
	}

	public void setRecipe(ICraftingRecipe recipe) {
		this.recipe = recipe;
		updateOutput(craftMatrix);
	}

	private void updateOutput(InventoryCrafting craftingMatrix) {
		outputSlot.setInventorySlotContents(0, recipe.getCraftingResult(craftingMatrix));
	}

	public ICraftingRecipe getRecipe() {
		return recipe;
	}

	public ItemStack getCraftingResult(InventoryCrafting invCrafting) {
		return recipe.getCraftingResult(invCrafting);
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting invCrafting) {
		return recipe.getRemainingItems(invCrafting);
	}
}
