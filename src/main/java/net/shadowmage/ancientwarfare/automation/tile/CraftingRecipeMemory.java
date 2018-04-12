package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CraftingRecipeMemory {
	private final TileEntity tileEntity;
	private ICraftingRecipe recipe = NoRecipeWrapper.INSTANCE;

	public ItemStackHandler bookSlot = new ItemStackHandler(1) {
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			return ItemResearchBook.getResearcherName(stack) != null ? super.insertItem(slot, stack, simulate) : stack;
		}

		@Override
		protected void onContentsChanged(int slot) {
			tileEntity.markDirty();
		}
	};
	public InventoryCraftResult outputSlot = new InventoryCraftResult();
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
		}
	};//the 3x3 recipe template/matrix

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

	public boolean hasValidRecipe() {
		return recipe.isValid();
	}

	public List<Ingredient> getIngredients() {
		return recipe.getIngredients();
	}

	public ItemStack getCraftingResult(InventoryCrafting inv) {
		return recipe.getCraftingResult(inv);
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

	public ItemStack getRecipeOutput() {
		return recipe.getRecipeOutput();
	}

	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting invCrafting) {
		return recipe.getRemainingItems(invCrafting);
	}

	public void setRecipe(ICraftingRecipe recipe) {
		this.recipe = recipe;
		outputSlot.setInventorySlotContents(0, recipe.getRecipeOutput());
	}

	public ICraftingRecipe getRecipe() {
		return recipe;
	}
}
