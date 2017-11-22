//TODO recipes

 package net.shadowmage.ancientwarfare.core.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AWCraftingManager {
    private static final List<ResearchRecipe> recipes = Lists.newArrayList();

    /*
     * First search within research recipes, then delegates to CraftingManager.findMatchingRecipe
     */
    public static ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, String playerName) {
        if (world == null)
            return null;
        if (playerName != null && !playerName.isEmpty()) {
            for (ResearchRecipe recipe : recipes) {
                if (recipe.matches(inventory, world) && canPlayerCraft(recipe, world, playerName)) {
                    return recipe.getCraftingResult(inventory);
                }
            }
        }
        IRecipe recipe = CraftingManager.findMatchingRecipe(inventory, world);
        return recipe != null ? recipe.getCraftingResult(inventory) : ItemStack.EMPTY;
    }

    private static boolean canPlayerCraft(ResearchRecipe recipe, World world, String playerName) {
        if (AWCoreStatics.useResearchSystem) {
            if (!ResearchTracker.INSTANCE.hasPlayerCompleted(world, playerName, recipe.getNeededResearch())) {
                return false;
            }
        }
        return true;
    }

    public static void addRecipe(ResearchRecipe recipe) {
        Item item = recipe.getRecipeOutput().getItem();
        if (AWCoreStatics.isItemCraftable(item)) {
            if (recipe.getNeededResearch() != - 1 && AWCoreStatics.isItemResearcheable(item)) {
                recipes.add(recipe);
            } else {
                ForgeRegistries.RECIPES.register(recipe);
            }
        }
    }

    public static List<ResearchRecipe> getRecipes() {
        return recipes;
    }

    // Replace calls to GameRegistry.addShapeless/ShapedRecipe with these methods, which will dump it to a json in your dir of choice
    // Also works with OD, replace GameRegistry.addRecipe(new ShapedOreRecipe/ShapelessOreRecipe with the same calls

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File RECIPE_DIR = null;
    private static final Set<String> USED_OD_NAMES = new TreeSet<>();

    private static void setupDir() {
        if (RECIPE_DIR == null) {
            RECIPE_DIR = new File(AWCoreStatics.configPathForFiles).toPath().resolve("recipes/").toFile();
        }

        if (!RECIPE_DIR.exists()) {
            RECIPE_DIR.mkdir();
        }
    }

    private static void addShapedRecipe(ItemStack result, Object... components) {
        setupDir();

        // GameRegistry.addShapedRecipe(result, components);

        Map<String, Object> json = new LinkedHashMap<>();

        List<String> pattern = new ArrayList<>();
        int i = 0;
        while (i < components.length && components[i] instanceof String) {
            pattern.add(((String) components[i]).toUpperCase());
            i++;
        }
        boolean isOreDict = false;
        Map<String, Map<String, Object>> key = new HashMap<>();
        Character curKey = null;
        for (; i < components.length; i++) {
            Object o = components[i];
            if (o instanceof Character) {
                if (curKey != null)
                    throw new IllegalArgumentException("Provided two char keys in a row");
                curKey = (Character) o;
            } else {
                if (curKey == null)
                    throw new IllegalArgumentException("Providing object without a char key");
                if (o instanceof String)
                    isOreDict = true;
                key.put(Character.toString(Character.toUpperCase(curKey)), serializeItem(o));
                curKey = null;
            }
        }
        json.put("type", isOreDict ? "forge:ore_shaped" : "minecraft:crafting_shaped");
        json.put("pattern", pattern);
        json.put("key", key);
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
        File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

        while (f.exists()) {
            suffix += "_alt";
            f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
        }

        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addShapelessRecipe(ItemStack result, Object... components)
    {
        setupDir();

        // addShapelessRecipe(result, components);

        Map<String, Object> json = new LinkedHashMap<>();

        boolean isOreDict = false;
        List<Map<String, Object>> ingredients = new ArrayList<>();
        for (Object o : components) {
            if (o instanceof String)
                isOreDict = true;
            ingredients.add(serializeItem(o));
        }
        json.put("type", isOreDict ? "forge:ore_shapeless" : "minecraft:crafting_shapeless");
        json.put("ingredients", ingredients);
        json.put("result", serializeItem(result));

        // names the json the same name as the output's registry name
        // repeatedly adds _alt if a file already exists
        // janky I know but it works
        String suffix = result.getItem().getHasSubtypes() ? "_" + result.getItemDamage() : "";
        File f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");

        while (f.exists()) {
            suffix += "_alt";
            f = new File(RECIPE_DIR, result.getItem().getRegistryName().getResourcePath() + suffix + ".json");
        }


        try (FileWriter w = new FileWriter(f)) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> serializeItem(Object thing) {
        if (thing instanceof Item) {
            return serializeItem(new ItemStack((Item) thing));
        }
        if (thing instanceof Block) {
            return serializeItem(new ItemStack((Block) thing));
        }
        if (thing instanceof ItemStack) {
            ItemStack stack = (ItemStack) thing;
            Map<String, Object> ret = new LinkedHashMap<>();
            if (stack.hasTagCompound())
                ret.put("type", "minecraft:item_nbt");
            ret.put("item", stack.getItem().getRegistryName().toString());
            if (stack.getItem().getHasSubtypes() || stack.getItemDamage() != 0) {
                ret.put("data", stack.getItemDamage());
            }
            if (stack.hasTagCompound()) {
                ret.put("nbt", stack.getTagCompound().toString());
            }
            if (stack.getCount() > 1) {
                ret.put("count", stack.getCount());
            }

            return ret;
        }
        if (thing instanceof String) {
            Map<String, Object> ret = new HashMap<>();
            USED_OD_NAMES.add((String) thing);
            ret.put("item", "#" + ((String) thing).toUpperCase(Locale.ROOT));
            return ret;
        }

        throw new IllegalArgumentException("Not a block, item, stack, or od name");
    }

    private static void generateConstants() {
        List<Map<String, Object>> json = new ArrayList<>();
        for (String s : USED_OD_NAMES) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", s.toUpperCase(Locale.ROOT));
            entry.put("ingredient", ImmutableMap.of("type", "forge:ore_dict", "ore", s));
            json.add(entry);
        }

        try (FileWriter w = new FileWriter(new File(RECIPE_DIR, "_constants.json"))) {
            GSON.toJson(json, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
