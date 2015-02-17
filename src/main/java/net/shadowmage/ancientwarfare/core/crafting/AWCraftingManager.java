package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.util.ArrayList;
import java.util.List;

public class AWCraftingManager {

    private static final String[] emptyStringArray = new String[]{};
    private static final String[] singleInputArray = new String[]{""};
    public static final AWCraftingManager INSTANCE = new AWCraftingManager();

    private final List<RecipeResearched> recipes;
    private AWCraftingManager() {
        recipes = new ArrayList<RecipeResearched>();
    }

    /**
     * shameless copy of CraftingManager.findMatchingRecipe, with added param for player
     */
    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, String playerName) {
        ItemStack item1 = CraftingManager.getInstance().findMatchingRecipe(inventory, world);
        if (item1 != null) {
            return item1;
        }
        if (playerName == null || playerName.isEmpty()) {
            return null;
        }
        for (RecipeResearched recipe : this.recipes) {
            if (recipe.matches(inventory, world) && canPlayerCraft(recipe, world, playerName)) {
                return recipe.getCraftingResult(inventory);
            }
        }
        return null;
    }

    private boolean canPlayerCraft(RecipeResearched recipe, World world, String playerName) {
        if (AWCoreStatics.useResearchSystem) {
            for (Integer i : recipe.getNeededResearch()) {
                if (!ResearchTracker.instance().hasPlayerCompleted(world, playerName, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addRecipe(RecipeResearched recipe) {
        Item item = recipe.getRecipeOutput().getItem();
        boolean craftable = AWCoreStatics.isItemCraftable(item);
        if (craftable) {
            if (!recipe.getNeededResearch().isEmpty() && AWCoreStatics.isItemResearched(item)) {
                this.recipes.add(recipe);
            } else {
                GameRegistry.addRecipe(recipe);
            }
        }
    }

    public RecipeResearched createRecipe(ItemStack result, String research, Object... inputArray) {
        if (research == null || research.isEmpty()) {
            return createRecipe(result, emptyStringArray, inputArray);
        } else {
            singleInputArray[0] = research;
            return createRecipe(result, singleInputArray, inputArray);
        }
    }

    public RecipeResearched createRecipe(ItemStack result, String[] research, Object... inputArray) {
        RecipeResearched recipe = new RecipeResearched(result, inputArray).addResearch(research);
        addRecipe(recipe);
        return recipe;
    }

    public List<RecipeResearched> getRecipes() {
        return recipes;
    }

}
