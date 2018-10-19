package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	@SideOnly(Side.CLIENT)
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
					inputs.add(getNonNullIngredientStack(ingredient.getAllIngredients()));
				}
			}
		}
		ICraftingRecipe recipe = AWCraftingManager.findMatchingRecipe(Minecraft.getMinecraft().world, inputs, result);

		if (AWCoreStatics.useResearchSystem && recipe.getNeededResearch().isPresent()) {
			if (container.getCraftingMemoryContainer().getCrafterName() == null) {
				String tooltipMessage = I18n.format("jei.tooltip.error.recipe.transfer.no.research_book");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}

			if (!AWCraftingManager.canPlayerCraft(Minecraft.getMinecraft().world, container.getCraftingMemoryContainer().getCrafterName(), recipe.getNeededResearch().get())) {
				String tooltipMessage = I18n.format("jei.tooltip.error.recipe.transfer.missing.research");
				return handlerHelper.createUserErrorWithTooltip(tooltipMessage);
			}
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
			String message = I18n.format("jei.tooltip.error.recipe.transfer.inventory.full");
			return handlerHelper.createUserErrorWithTooltip(message);
		}
		@SuppressWarnings("squid:S00108") List<Integer> missingItems = getMissingItems(inputs, recipe, allInventories);

		if (!missingItems.isEmpty()) {
			String message = I18n.format("jei.tooltip.error.recipe.transfer.missing");
			return handlerHelper.createUserErrorForSlots(message, missingItems);
		}

		if (doTransfer) {
			NetworkHandler.sendToServer(new PacketTransferRecipe(recipe));
		}

		return null;
	}

	private List<Integer> getMissingItems(NonNullList<ItemStack> inputs, ICraftingRecipe recipe, IItemHandlerModifiable allInventories) {
		return AWCraftingManager.getRecipeInventoryMatch(recipe, inputs, s -> InventoryTools.hasCountOrMore(allInventories, s), allInventories,
				ArrayList::new, (a, i, s) -> {}, (a, in) -> addMissingItem(a, in, inputs));
	}

	private ItemStack getNonNullIngredientStack(List<ItemStack> allIngredients) {
		for (ItemStack stack : allIngredients) {
			//because some mods apparently link null stacks to the ingredients in JEI
			if (stack != null) {
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	private void addMissingItem(List<Integer> missingItems, Ingredient missingIngredient, NonNullList<ItemStack> inputs) {
		List<ItemStack> matchingStacks = inputs.stream().filter(s -> missingIngredient.apply(s) && !missingItems.contains(getInputIndex(inputs.indexOf(s)))).collect(Collectors.toList());
		if (!matchingStacks.isEmpty()) {
			ItemStack matched = matchingStacks.get(matchingStacks.size() - 1);
			for (int i = inputs.size() - 1; i >= 0; i--) {
				if (!missingItems.contains(getInputIndex(i)) && inputs.get(i) == matched) {
					missingItems.add(getInputIndex(i));
				}
			}
		}
	}

	private int getInputIndex(int craftMatrixIndex) {
		int firstInputIndex = 1;
		return craftMatrixIndex + firstInputIndex;
	}
}
