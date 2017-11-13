package net.shadowmage.ancientwarfare.core.compat.jei;

import com.google.common.collect.Lists;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
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

        registry.addRecipeCatalyst(AWAutomationBlocks.worksiteAutoCrafting, ResearchRecipeCategory.UID);
        registry.addRecipeCatalyst(AWAutomationBlocks.warehouseCrafting, ResearchRecipeCategory.UID);
        registry.addRecipeCatalyst(AWBlocks.engineeringStation, ResearchRecipeCategory.UID);
    }
}
