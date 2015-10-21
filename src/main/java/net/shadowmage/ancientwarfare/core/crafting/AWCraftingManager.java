package net.shadowmage.ancientwarfare.core.crafting;

import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IResearchRecipe;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.util.ArrayList;
import java.util.List;

public class AWCraftingManager {

    private static final String[] singleInputArray = new String[]{""};
    public static final AWCraftingManager INSTANCE = new AWCraftingManager();

    private final List<IResearchRecipe> recipes;

    private AWCraftingManager() {
        recipes = new ArrayList<IResearchRecipe>();
    }

    /**
     * First search within research recipes, then delegates to CraftingManager.findMatchingRecipe
     */
    public ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, String playerName) {
        if (world == null)
            return null;
        if (playerName != null && !playerName.isEmpty()) {
            for (IResearchRecipe recipe : this.recipes) {
                if (recipe.matches(inventory, world) && canPlayerCraft(recipe, world, playerName)) {
                    return recipe.getCraftingResult(inventory);
                }
            }
        }
        return CraftingManager.getInstance().findMatchingRecipe(inventory, world);
    }

    private boolean canPlayerCraft(IResearchRecipe recipe, World world, String playerName) {
        if (AWCoreStatics.useResearchSystem) {
            for (Integer i : recipe.getNeededResearch()) {
                if (!ResearchTracker.INSTANCE.hasPlayerCompleted(world, playerName, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void addRecipe(IResearchRecipe recipe) {
        Item item = recipe.getRecipeOutput().getItem();
        if (AWCoreStatics.isItemCraftable(item)) {
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
    public void parseRecipes(String path) {
        List<String> lines = StringTools.getResourceLines(path);
        for (String line : lines) {
            String[] split = StringTools.parseStringArray(line);
            if (split.length < 5) {
                continue;
            }
            ItemStack stack = StringTools.safeParseStack(split[1], split[2], split[3]);
            if (stack == null) {
                continue;
            }
            boolean shaped = true;
            if (split.length < 7 || split[4].length() > 3) {
                shaped = false;
            }
            Object[] craft_par = new Object[split.length - 4];//All the inputs
            int i = 4;
            while (split[i].length() > 0 && split[i + 1].length() < 4) {//Any height of crafting grid, width limited at 1-3
                craft_par[i - 4] = split[i];
                i++;
            }
            for (; i < split.length; i += 2) {
                if(shaped)
                    craft_par[i - 4] = split[i].charAt(0);//The character key
                else
                    i--;
                if (split[i + 1].startsWith("(") && split[i + 3].endsWith(")")) {
                    craft_par[i - 3] = StringTools.safeParseStack(split[i + 1].substring(1), split[i + 2], split[i + 3].substring(0, split[i + 3].length() - 1));
                    i = i + 2;
                } else {
                    Object crafting_item = GameData.getItemRegistry().getObject(split[i + 1]);
                    if (crafting_item == GameData.getItemRegistry().getDefaultValue()) {//Not an item name
                        crafting_item = GameData.getBlockRegistry().getObject(split[i + 1]);
                        if (crafting_item == GameData.getBlockRegistry().getDefaultValue()) {//Not a block name
                            crafting_item = split[i + 1];//Maybe a generic "ore" name ?
                        }
                    }
                    craft_par[i - 3] = crafting_item;//The item value
                }
            }
            ArrayList list = new ArrayList();
            for (Object object : craft_par) {
                if (object != null) {
                    list.add(object);
                }
            }
            try {
                createRecipe(shaped, stack, split[0].trim(), list.toArray());
            } catch (Throwable throwable) {
                AWLog.logError("Error creating recipe for parsed line: " + line);
            }
        }
    }

    public void createRecipe(ItemStack result, String research, Object... inputArray) {
        createRecipe(true, result, research, inputArray);
    }

    public void createRecipe(boolean shaped, ItemStack result, String research, Object... inputArray) {
        if (research == null || research.isEmpty()) {
            createRecipe(shaped, result, inputArray);
        } else {
            singleInputArray[0] = research;
            createRecipe(shaped, result, singleInputArray, inputArray);
        }
    }

    private void createRecipe(boolean shaped, ItemStack result, Object[] inputArray) {
        if(AWCoreStatics.isItemCraftable(result.getItem())) {
            IRecipe recipe = shaped ? new ShapedOreRecipe(result, inputArray) : new ShapelessOreRecipe(result, inputArray);
            GameRegistry.addRecipe(recipe);
        }
    }

    public IResearchRecipe createRecipe(boolean shaped, ItemStack result, String[] research, Object... inputArray) {
        IResearchRecipe recipe = shaped ? new RecipeResearched(result, inputArray) : new UnformedRecipeResearched(result, inputArray);
        addRecipe(recipe.addResearch(research));
        return recipe;
    }

    public List<IResearchRecipe> getRecipes() {
        return recipes;
    }

}
