package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.config.SessionData;
import mezz.jei.util.Translator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MultiRecipeTransferHandler<C extends Container & ICraftingContainer> implements IRecipeTransferHandler<C> {
	private final IRecipeTransferHandlerHelper handlerHelper;
	private Class<C> containerClass;

	public MultiRecipeTransferHandler(Class<C> containerClass, IRecipeTransferHandlerHelper handlerHelper) {
		this.handlerHelper = handlerHelper;
		this.containerClass = containerClass;
	}

	@Override
	public Class<C> getContainerClass() {
		return containerClass;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		IGuiItemStackGroup stacks = recipeLayout.getItemStacks();
		ItemStack result = stacks.getGuiIngredients().get(0).getDisplayedIngredient();
		NonNullList<ItemStack> inputs = NonNullList.create();
		for (int i = 0; i < stacks.getGuiIngredients().size(); i++) {
			IGuiIngredient<ItemStack> ingredient = stacks.getGuiIngredients().get(i);
			if (ingredient.isInput()) {
				if (ingredient.getAllIngredients().isEmpty()) {
					inputs.add(ItemStack.EMPTY);
				} else {
					inputs.add(ingredient.getDisplayedIngredient());
				}
			}
		}
		ICraftingRecipe recipe = AWCraftingManager.findMatchingRecipe(Minecraft.getMinecraft().world, inputs, result);

		if (recipe.getNeededResearch() > -1) {
			if (container.getCraftingMemoryContainer().getCrafterName() == null) {
				String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.research_book");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}

			if (!AWCraftingManager
					.canPlayerCraft(Minecraft.getMinecraft().world, container.getCraftingMemoryContainer().getCrafterName(), recipe.getNeededResearch())) {
				String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing.research");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}
		}

		if (!SessionData.isJeiOnServer()) {
			String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
			return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
		}

		List<Slot> craftingSlots = container.getCraftingMemoryContainer().getCraftingMatrixSlots();

		NonNullList<ItemStack> craftingMatrixStacks = NonNullList.create();
		for (Slot slot : craftingSlots) {
			final ItemStack stack = slot.getStack();
			if (!stack.isEmpty()) {
				craftingMatrixStacks.add(stack.copy());
			}
		}

		IItemHandlerModifiable inventories = new CombinedInvWrapper(container.getInventories());
		IItemHandlerModifiable allInventories = new CombinedInvWrapper(new ItemStackHandler(craftingMatrixStacks), inventories);

		// check if we have enough inventory space to put crafting slots into inventories
		if (!InventoryTools.insertItems(inventories, craftingMatrixStacks, true).isEmpty()) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
			return handlerHelper.createUserErrorWithTooltip(message);
		}

		List<Integer> missingItems = AWCraftingManager.getRecipeInventoryMatch(recipe, allInventories, ArrayList::new, (a, i, s) -> {
		}, (a, in) -> a.add(inputs.indexOf(inputs.stream().filter(in::apply).findFirst().orElse(ItemStack.EMPTY)) + 1), false);

		if (!missingItems.isEmpty()) {
			String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
			return handlerHelper.createUserErrorForSlots(message, missingItems);
		}

		if (doTransfer) {
			NetworkHandler.sendToServer(new PacketTransferRecipe(recipe));
		}

		return null;
	}
}
