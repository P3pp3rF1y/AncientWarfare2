package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.automation.init.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ShapedResearchRecipe;
import net.shadowmage.ancientwarfare.core.crafting.ShapelessResearchRecipe;
import net.shadowmage.ancientwarfare.core.init.AWCoreBlocks;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.init.AWStructureItems;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleItems;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class AWJEIPlugin implements IModPlugin {
	@Override
	public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
		subtypeRegistry.useNbtForSubtypes(AWNPCItems.NPC_SPAWNER);
		subtypeRegistry.useNbtForSubtypes(Item.getItemFromBlock(AWStructureBlocks.FIRE_PIT));
		subtypeRegistry.useNbtForSubtypes(AWNPCItems.COIN);
		subtypeRegistry.useNbtForSubtypes(AWStructureItems.TOTEM_PART);
		//noinspection ConstantConditions
		subtypeRegistry.registerSubtypeInterpreter(AWVehicleItems.SPAWNER, itemStack -> Integer.toString(itemStack.getMetadata()) + ":" + (itemStack.hasTagCompound() ? itemStack.getTagCompound().toString() : ""));
	}

	@Override
	public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
		IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

		registry.addRecipeCategories(new ShapedResearchRecipeCategory(guiHelper));
		registry.addRecipeCategories(new ShapelessResearchRecipeCategory(guiHelper));
	}

	@Override
	public void register(IModRegistry registry) {
		// Blacklist exclusions from the JEI inventory
		IIngredientBlacklist blackList = registry.getJeiHelpers().getIngredientBlacklist();
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoBallShot)));
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoBallIronShot)));
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoArrow)));
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoArrowFlame)));
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoArrowIron)));
		blackList.addIngredientToBlacklist(new ItemStack(AmmoRegistry.getItemForAmmo(AmmoRegistry.ammoArrowIronFlame)));


		List<ResearchRecipeWrapper> shapedResearchRecipes = AWCraftingManager.getRecipes().stream().filter(r -> r instanceof ShapedResearchRecipe).map(r -> new ShapedResearchRecipeWrapper(registry.getJeiHelpers(), (ShapedResearchRecipe) r)).collect(Collectors.toList());
		registry.addRecipes(shapedResearchRecipes, ShapedResearchRecipeCategory.UID);

		List<ResearchRecipeWrapper> shapelessResearchRecipes = AWCraftingManager.getRecipes().stream().filter(r -> r instanceof ShapelessResearchRecipe).map(r -> new ResearchRecipeWrapper<>(registry.getJeiHelpers().getStackHelper(), r)).collect(Collectors.toList());
		registry.addRecipes(shapelessResearchRecipes, ShapelessResearchRecipeCategory.UID);

		IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();

		if (!shapedResearchRecipes.isEmpty()) {
			registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, ShapedResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, ShapedResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, ShapedResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());

			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.AUTO_CRAFTING), ShapedResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.WAREHOUSE_CRAFTING), ShapedResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWCoreBlocks.ENGINEERING_STATION), ShapedResearchRecipeCategory.UID);
		}

		if (!shapelessResearchRecipes.isEmpty()) {
			registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, ShapelessResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, ShapelessResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());
			registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, ShapelessResearchRecipeCategory.UID, transferRegistry, registry.getJeiHelpers());

			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.AUTO_CRAFTING), ShapelessResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.WAREHOUSE_CRAFTING), ShapelessResearchRecipeCategory.UID);
			registry.addRecipeCatalyst(new ItemStack(AWCoreBlocks.ENGINEERING_STATION), ShapelessResearchRecipeCategory.UID);
		}

		registerMultiRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry, registry.getJeiHelpers());
		registerMultiRecipeTransferHandler(ContainerWarehouseCraftingStation.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry, registry.getJeiHelpers());
		registerMultiRecipeTransferHandler(ContainerEngineeringStation.class, VanillaRecipeCategoryUid.CRAFTING, transferRegistry, registry.getJeiHelpers());

		registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.AUTO_CRAFTING), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.WAREHOUSE_CRAFTING), VanillaRecipeCategoryUid.CRAFTING);
		registry.addRecipeCatalyst(new ItemStack(AWCoreBlocks.ENGINEERING_STATION), VanillaRecipeCategoryUid.CRAFTING);
	}

	private <C extends Container & ICraftingContainer> void registerMultiRecipeTransferHandler(Class<C> containerClass, String recipeCategoryUid, IRecipeTransferRegistry transferRegistry, IJeiHelpers jeiHelpers) {
		MultiRecipeTransferHandler<C> handler = new MultiRecipeTransferHandler<>(containerClass, jeiHelpers.recipeTransferHandlerHelper());
		transferRegistry.addRecipeTransferHandler(handler, recipeCategoryUid);
	}
}
