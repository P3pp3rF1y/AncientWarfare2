package net.shadowmage.ancientwarfare.core.inventory;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ResearchRecipeBase;

/**
 * This needs to be used instead of vanilla SlotCrafting because vanilla one can only work with ingredients of IRecipes, but has no clue what to do
 * with other recipe ingredients and thus returns everything in CraftingMatrix in the getRemainingItems call and thus there's item dupe in that case.
 */
public class SlotResearchCrafting extends Slot {
	/**
	 * The craft matrix inventory linked to this result slot.
	 */
	private final InventoryCrafting craftMatrix;
	/**
	 * The player that is using the GUI where this slot resides.
	 */
	private final EntityPlayer player;
	/**
	 * The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset.
	 */
	private int amountCrafted;

	private String crafterName;

	public SlotResearchCrafting(EntityPlayer player, String crafterName, InventoryCrafting craftingInventory, IInventory inventory, int slotIndex, int xPosition, int yPosition) {
		super(inventory, slotIndex, xPosition, yPosition);
		this.player = player;
		this.craftMatrix = craftingInventory;
		this.crafterName = crafterName;
	}

	/**
	 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
	 */
	public boolean isItemValid(ItemStack stack) {
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	public ItemStack decrStackSize(int amount) {
		if (getHasStack()) {
			amountCrafted += Math.min(amount, getStack().getCount());
		}
		return super.decrStackSize(amount);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	protected void onCrafting(ItemStack stack, int amount) {
		amountCrafted += amount;
		onCrafting(stack);
	}

	protected void onSwapCraft(int p_190900_1_) {
		amountCrafted += p_190900_1_;
	}

	@Override
	protected void onCrafting(ItemStack stack) {
		InventoryCraftResult inventorycraftresult = (InventoryCraftResult) inventory;
		IRecipe irecipe = inventorycraftresult.getRecipeUsed();
		if (amountCrafted > 0) {
			stack.onCrafting(player.world, player, amountCrafted);
			if (irecipe != null) {
				FMLCommonHandler.instance().firePlayerCraftingEvent(player, stack, craftMatrix);
			}
		}
		amountCrafted = 0;
		if (irecipe != null && !irecipe.isHidden()) {
			player.unlockRecipes(Lists.newArrayList(irecipe));
			inventorycraftresult.setRecipeUsed(null);
		}
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
		onCrafting(stack);
		InventoryCraftResult inventorycraftresult = (InventoryCraftResult) inventory;
		NonNullList<ItemStack> nonnulllist;

		ResearchRecipeBase researchRecipe = AWCraftingManager.findMatchingResearchRecipe(craftMatrix, thePlayer.world, crafterName);

		if (researchRecipe == null && inventorycraftresult.getRecipeUsed() != null) {
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
			nonnulllist = CraftingManager.getRemainingItems(craftMatrix, thePlayer.world);
			net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
		} else {
			nonnulllist = ForgeHooks.defaultRecipeGetRemainingItems(craftMatrix);
		}
		for (int i = 0; i < nonnulllist.size(); ++i) {
			ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
			ItemStack itemstack1 = nonnulllist.get(i);
			if (!itemstack.isEmpty()) {
				this.craftMatrix.decrStackSize(i, AWCraftingManager.getMatchingIngredientCount(researchRecipe, itemstack));
				itemstack = this.craftMatrix.getStackInSlot(i);
			}
			if (!itemstack1.isEmpty()) {
				if (itemstack.isEmpty()) {
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				} else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
					itemstack1.grow(itemstack.getCount());
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				} else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
					this.player.dropItem(itemstack1, false);
				}
			}
		}
		return stack;
	}
}
