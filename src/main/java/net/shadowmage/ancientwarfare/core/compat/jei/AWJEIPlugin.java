package net.shadowmage.ancientwarfare.core.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteAutoCrafting;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@JEIPlugin
public class AWJEIPlugin implements IModPlugin {
    private static List<Item> nbtItems = Lists.newArrayList();
    public static void addNbtItems(Item... items) {
        nbtItems.addAll(Arrays.asList(items));
    }

    @Override
    public void registerItemSubtypes(@Nonnull ISubtypeRegistry subtypeRegistry) {
        subtypeRegistry.useNbtForSubtypes(nbtItems.toArray(new Item[nbtItems.size()]));
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();

        registry.addRecipeCategories(new ResearchRecipeCategory(guiHelper));
    }

    @Override
    public void register(IModRegistry registry) {
        registry.addRecipes(AWCraftingManager.getRecipes().stream().map(r -> new ResearchRecipeWrapper(registry.getJeiHelpers().getStackHelper(), r)).collect(Collectors.toList())
                , ResearchRecipeCategory.UID);

        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();

        transferRegistry.addRecipeTransferHandler(ContainerWorksiteAutoCrafting.class, ResearchRecipeCategory.UID, 2, 9, 11, 63);
        transferRegistry.addRecipeTransferHandler(ContainerWarehouseCraftingStation.class, ResearchRecipeCategory.UID, 2, 9, 11, 36);
        transferRegistry.addRecipeTransferHandler(ContainerEngineeringStation.class, ResearchRecipeCategory.UID, 2, 9, 11, 54);

        registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.worksiteAutoCrafting), ResearchRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(AWAutomationBlocks.warehouseCrafting), ResearchRecipeCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(AWBlocks.engineeringStation), ResearchRecipeCategory.UID);
    }
}
