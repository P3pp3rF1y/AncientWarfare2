package net.shadowmage.ancientwarfare.core.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import mezz.jei.plugins.vanilla.crafting.ShapelessRecipeWrapper;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.crafting.IShapedRecipe;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ShapedResearchRecipe;
import net.shadowmage.ancientwarfare.core.crafting.ShapelessResearchRecipe;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItems;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItems;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class AWJEIPlugin implements IModPlugin {
	private static List<IRecipe> wrappedRecipes = Lists.newArrayList();

	public static void addWrappedRecipe(IRecipe recipe) {
		wrappedRecipes.add(recipe);
	}

	@Override
	public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(AWNPCItems.npcSpawner);
		subtypeRegistry.registerSubtypeInterpreter(AWVehicleItems.spawner,
				itemStack -> Integer.toString(itemStack.getMetadata()) + ":" + (itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : ""));
	}

	@Override
	public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeCategories(new ShapedResearchRecipeCategory(guiHelper));
		registry.addRecipeCategories(new ShapelessResearchRecipeCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		List<ResearchRecipeWrapper> shapedResearchRecipes = AWCraftingManager.getRecipes().stream().filter(r -> r instanceof ShapedResearchRecipe).map(r -> new ResearchRecipeWrapper(registry.getJeiHelpers().getStackHelper(), r)).collect(Collectors.toList());
		registry.addRecipes(shapedResearchRecipes, ShapedResearchRecipeCategory.UID);

		List<ResearchRecipeWrapper> shapelessResearchRecipes = AWCraftingManager.getRecipes().stream().filter(r -> r instanceof ShapelessResearchRecipe).map(r -> new ResearchRecipeWrapper(registry.getJeiHelpers().getStackHelper(), r)).collect(Collectors.toList());
		registry.addRecipes(shapelessResearchRecipes, ShapelessResearchRecipeCategory.UID);

		registry.addRecipes(wrappedRecipes.stream().map(r -> wrapRecipe(registry.getJeiHelpers(), r)).collect(Collectors.toList()), VanillaRecipeCategoryUid.CRAFTING);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();

		if (!shapedResearchRecipes.isEmpty()) {
			registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, ShapedResearchRecipeCategory.UID, transferRegistry,
					registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, ShapedResearchRecipeCategory.UID, transferRegistry,
					registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, ShapedResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());

			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.worksiteAutoCrafting), ShapedResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.warehouseCrafting), ShapedResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWBlocks.engineeringStation), ShapedResearchRecipeCategory.UID);
		}

		if (!shapelessResearchRecipes.isEmpty()) {
			registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, ShapelessResearchRecipeCategory.UID, transferRegistry,
					registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, ShapelessResearchRecipeCategory.UID, transferRegistry,
					registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, ShapelessResearchRecipeCategory.UID, transferRegistry,
					registry.getJeiHelpers());

			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.worksiteAutoCrafting), ShapelessResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.warehouseCrafting), ShapelessResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWBlocks.engineeringStation), ShapelessResearchRecipeCategory.UID);
		}

		registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry, registry.getJeiHelpers());
		registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry,
				registry.getJeiHelpers());
		registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry, registry.getJeiHelpers());

		registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.worksiteAutoCrafting), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.warehouseCrafting), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(AWBlocks.engineeringStation), VanillaRecipeCategoryUid.CRAFTING);
	}

	private <C extends Container & ICraftingContainer> void registerMultiRecipeTransferHandler(Class<C> containerClass, String recipeCategoryUid,
			IRecipeTransferRegistry transferRegistry, IJeiHelpers jeiHelpers) {
		MultiRecipeTransferHandler<C> handler = new MultiRecipeTransferHandler<>(containerClass, jeiHelpers.recipeTransferHandlerHelper());
		transferRegistry.addRecipeTransferHandler(handler, recipeCategoryUid);
	}

	private ShapelessRecipeWrapper wrapRecipe(IJeiHelpers jeiHelpers, IRecipe recipe) {
		return recipe instanceof IShapedRecipe ? new ShapedRecipeWrapper(jeiHelpers, (IShapedRecipe) recipe) : new ShapelessRecipeWrapper<>(jeiHelpers, recipe);
	}
}
