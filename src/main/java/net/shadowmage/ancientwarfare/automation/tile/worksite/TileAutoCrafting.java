package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.CraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

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

	private boolean canHoldLastCheck = false;

	@Override
	public void onBlockBroken(IBlockState state) {
		craftingRecipeMemory.dropInventory();
		InventoryTools.dropItemsInWorld(world, outputInventory, pos);
		InventoryTools.dropItemsInWorld(world, resourceInventory, pos);
		super.onBlockBroken(state);
	}

	public boolean tryCraftItem() {
		if (canHold()) {
			NonNullList<ItemStack> reusableStacks = AWCraftingManager.getReusableStacks(craftingRecipeMemory.getRecipe(), craftingRecipeMemory.craftMatrix);
			NonNullList<ItemStack> resources = InventoryTools.removeItems(AWCraftingManager.getRecipeInventoryMatch(craftingRecipeMemory.getRecipe(), craftingRecipeMemory.getCraftingStacks(),
					new CombinedInvWrapper(resourceInventory, new ItemStackHandler(reusableStacks))), reusableStacks);
			if (!resources.isEmpty()) {
				craftItem(resources, reusableStacks);
				return true;
			}
		}
		return false;
	}

	private void craftItem(NonNullList<ItemStack> resources, NonNullList<ItemStack> reusableStacks) {
		InventoryCrafting invCrafting = AWCraftingManager.fillCraftingMatrixFromInventory(resources);
		ItemStack result = craftingRecipeMemory.getCraftingResult(invCrafting);
		InventoryTools.removeItems(resourceInventory, resources);
		NonNullList<ItemStack> remainingItems = InventoryTools.removeItems(craftingRecipeMemory.getRemainingItems(invCrafting), reusableStacks);

		for (ItemStack stack : remainingItems) {
			if (stack.isEmpty()) {
				continue;
			}

			if (!InventoryTools.removeItem(resources, is -> ItemStack.areItemStacksEqual(stack, is), stack.getCount(), true).isEmpty()) {
				InventoryTools.insertOrDropItem(resourceInventory, stack, world, pos);
			} else {
				InventoryTools.insertOrDropItem(outputInventory, stack, world, pos);
			}
		}

		InventoryTools.insertOrDropItem(outputInventory, result, world, pos);
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

	/* ***********************************INVENTORY METHODS*********************************************** */
	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, pos);
		}
		return true;
	}

	private static final CraftAction CRAFT_ACTION = new CraftAction();

	private static class CraftAction implements IWorksiteAction {
		@Override
		public double getEnergyConsumed(double efficiencyBonusFactor) {
			return IWorkSite.WorksiteImplementation.getEnergyPerActivation(efficiencyBonusFactor);
		}
	}

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		return canHoldLastCheck && !craftingRecipeMemory.getRecipe().getRecipeOutput().isEmpty() ? Optional.of(CRAFT_ACTION) : Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		return tryCraftItem();
	}

	@Override
	protected void updateWorksite() {
		canHoldLastCheck = canHold();
	}

	private boolean canHold() {
		ItemStack test = craftingRecipeMemory.getRecipe().getRecipeOutput();
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

	@SuppressWarnings("unchecked")
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
