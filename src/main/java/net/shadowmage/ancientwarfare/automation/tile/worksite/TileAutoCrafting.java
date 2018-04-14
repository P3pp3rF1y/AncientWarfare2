package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.tile.CraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileAutoCrafting extends TileWorksiteBase {
	public CraftingRecipeMemory craftingRecipeMemory = new CraftingRecipeMemory(this);
	public ItemStackHandler outputInventory = new ItemStackHandler(9) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};
	public ItemStackHandler resourceInventory = new ItemStackHandler(18) {
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		}
	};

	private boolean canCraftLastCheck = false;
	private boolean canHoldLastCheck = false;

	public TileAutoCrafting() {
	}

	@Override
	public void onBlockBroken() {
		craftingRecipeMemory.dropInventory();
		InventoryTools.dropItemsInWorld(world, outputInventory, pos);
		InventoryTools.dropItemsInWorld(world, resourceInventory, pos);
		super.onBlockBroken();
	}

	private boolean canCraft() {
		return AWCraftingManager.canCraftFromInventory(craftingRecipeMemory.getRecipe(), resourceInventory);
	}

	public boolean tryCraftItem() {
		if (canCraft() && canHold()) {
			craftItem();
			return true;
		}
		return false;
	}

	private void craftItem() {
		List<ItemStack> resources = AWCraftingManager.getRecipeInventoryMatch(craftingRecipeMemory.getRecipe(), resourceInventory);
		InventoryCrafting invCrafting = AWCraftingManager.fillCraftingMatrixFromInventory(resources);
		@Nonnull ItemStack stack = craftingRecipeMemory.getRecipe().getCraftingResult(invCrafting);
		useResources(resources);
		NonNullList<ItemStack> remainingItems = craftingRecipeMemory.getRecipe().getRemainingItems(invCrafting);
		InventoryTools.insertOrDropItems(outputInventory, remainingItems, world, pos);

		stack = InventoryTools.mergeItemStack(outputInventory, stack);
		if (!stack.isEmpty()) {
			InventoryTools.dropItemInWorld(world, stack, pos);
		}
	}

	private void useResources(List<ItemStack> resources) {
		for (ItemStack stack : resources) {
			InventoryTools.removeItems(resourceInventory, stack, stack.getCount());
		}
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.CRAFTING;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		craftingRecipeMemory.readFromNBT(tag);
		resourceInventory.deserializeNBT(tag.getCompoundTag("resourceInventory"));
		outputInventory.deserializeNBT(tag.getCompoundTag("outputInventory"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		craftingRecipeMemory.writeToNBT(tag);
		tag.setTag("resourceInventory", resourceInventory.serializeNBT());
		tag.setTag("outputInventory", outputInventory.serializeNBT());
		return tag;
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

	//TODO make sure that this call really isn't needed
/*

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		onLayoutMatrixChanged();
	}
*/

	/* ***********************************INVENTORY METHODS*********************************************** */
	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, pos);
		}
		return true;
	}

	@Override
	protected boolean processWork() {
		return tryCraftItem();
	}

	@Override
	protected boolean hasWorksiteWork() {
		return canCraftLastCheck && canHoldLastCheck && !craftingRecipeMemory.getRecipeOutput().isEmpty();
	}

	@Override
	protected void updateWorksite() {
		canCraftLastCheck = canCraft();
		canHoldLastCheck = canHold();
	}

	private boolean canHold() {
		@Nonnull ItemStack test = craftingRecipeMemory.getRecipeOutput();
		return !test.isEmpty() && InventoryTools.canInventoryHold(outputInventory, test);
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) {
			if (facing == EnumFacing.DOWN) {
				return (T) outputInventory;
			} else {
				return (T) resourceInventory;
			}
		}

		return super.getCapability(capability, facing);
	}
}
