package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.config.SessionData;
import mezz.jei.startup.StackHelper;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.RegularCraftingWrapper;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiRecipeTransferHandler<C extends Container> extends BasicRecipeTransferHandler<C> {
	private final StackHelper stackHelper;
	private final IRecipeTransferHandlerHelper handlerHelper;
	private final IRecipeTransferInfo<C> transferHelper;

	public MultiRecipeTransferHandler(StackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<C> transferHelper) {
		super(stackHelper, handlerHelper, transferHelper);
		this.stackHelper = stackHelper;
		this.handlerHelper = handlerHelper;
		this.transferHelper = transferHelper;
	}

	@Override
	public Class<C> getContainerClass() {
		return transferHelper.getContainerClass();
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		ItemStack result = stacks.getGuiIngredients().get(0).getDisplayedIngredient();
		NonNullList<ItemStack> inputs = NonNullList.create();
		for (int i = 1; i < stacks.getGuiIngredients().size(); i++) {
			IGuiIngredient<ItemStack> ingredient = stacks.getGuiIngredients().get(i);
			if (ingredient.getDisplayedIngredient() != null) {
				inputs.add(ingredient.getDisplayedIngredient());
			}
		}

		ICraftingRecipe recipe = AWCraftingManager.findMatchingRecipe(Minecraft.getMinecraft().world, Minecraft.getMinecraft().player.getName(), inputs, result);

		if (recipe instanceof RegularCraftingWrapper) {
			return super.transferRecipe(container, recipeLayout, player, maxTransfer, doTransfer);
		}

		if (!SessionData.isJeiOnServer()) {
			String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
			return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
		}

		if (!transferHelper.canHandle(container)) {
			return handlerHelper.createInternalError();
		}

		Map<Integer, Slot> inventorySlots = new HashMap<>();
		for (Slot slot : transferHelper.getInventorySlots(container)) {
			inventorySlots.put(slot.slotNumber, slot);
		}

		Map<Integer, Slot> craftingSlots = new HashMap<>();
		for (Slot slot : transferHelper.getRecipeSlots(container)) {
			craftingSlots.put(slot.slotNumber, slot);
		}

		int inputCount = 0;
		IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
		for (IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
			if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
				inputCount++;
			}
		}

		NonNullList<ItemStack> availableItemStacks = NonNullList.create();
		int filledCraftSlotCount = 0;
		int emptySlotCount = 0;

		for (Slot slot : craftingSlots.values()) {
			final ItemStack stack = slot.getStack();
			if (!stack.isEmpty()) {
				filledCraftSlotCount++;
				availableItemStacks.add(stack.copy());
			}
		}

		for (Slot slot : inventorySlots.values()) {
			final ItemStack stack = slot.getStack();
			if (!stack.isEmpty()) {
				availableItemStacks.add(stack.copy());
			} else {
				emptySlotCount++;
			}
		}

		// check if we have enough inventory space to shuffle items around to their final locations
		if (filledCraftSlotCount - inputCount > emptySlotCount) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
			return handlerHelper.createUserErrorWithTooltip(message);
		}

		List<Integer> missingItems = AWCraftingManager.getRecipeInventoryMatch(recipe, new ItemStackHandler(availableItemStacks), ArrayList::new, (a, i, s) -> {
		}, ArrayList::add, false);

		if (!missingItems.isEmpty()) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
			return handlerHelper.createUserErrorForSlots(message, missingItems);
		}

		List<Integer> craftingSlotIndexes = new ArrayList<>(craftingSlots.keySet());
		Collections.sort(craftingSlotIndexes);

		List<Integer> inventorySlotIndexes = new ArrayList<>(inventorySlots.keySet());
		Collections.sort(inventorySlotIndexes);

		if (doTransfer) {
			NetworkHandler.sendToServer(new PacketTransferRecipe(recipe));
		}

		return null;
	}
}
