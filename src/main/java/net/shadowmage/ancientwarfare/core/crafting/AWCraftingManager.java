package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;

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

    /**
     * Create Research-dependent crafts from a config file.
     * Parse configuration data from in-jar resource file.
     *
     * @param path to file, incl. filename + extension, running-dir relative
     */
    public void parseRecipes(String path)
    {
        List<String> lines = StringTools.getResourceLines(path);
        for(String line : lines)
        {
            String[] split = StringTools.parseStringArray(line);
            if(split.length < 7){
                continue;
            }
            ItemStack stack = StringTools.safeParseStack(split[1], split[2], split[3]);
            if(stack == null){
                continue;
            }
            Object[] craft_par = new Object[split.length - 4];//All the inputs
            int i = 4;
            while(split[i].length() > 0 && split[i+1].length() < 4){//Any height of crafting grid, width limited at 1-3
                craft_par[i - 4] = split[i];
                i++;
            }
            for(; i < split.length; i += 2)
            {
                craft_par[i - 4] = split[i].charAt(0);//The character key
                Object crafting_item;
                if(split[i + 1].startsWith("(") && split[i+1].endsWith(")")){
                    String[] array = StringTools.parseStringArray(split[i + 1].substring(1, split[i + 1].length()-1));
                    crafting_item = StringTools.safeParseStack(array[0], array[1], array[2]);
                }else {
                    crafting_item = GameData.getItemRegistry().getObject(split[i + 1]);
                    if (crafting_item == GameData.getItemRegistry().getDefaultValue()) {//Not an item name
                        crafting_item = GameData.getBlockRegistry().getObject(split[i + 1]);
                        if (crafting_item == GameData.getBlockRegistry().getDefaultValue()) {//Not a block name
                            crafting_item = split[i + 1];//Maybe a generic "ore" name ?
                        }
                    }
                }
                craft_par[i - 3] = crafting_item;//The item value
            }
            try {
                createRecipe(stack, split[0], craft_par);
            }catch (Throwable throwable){
                throwable.printStackTrace();
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
