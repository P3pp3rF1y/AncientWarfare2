package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class AWCraftingManager {
    public static final IForgeRegistry<ResearchRecipe> RESEARCH_RECIPES = (new RegistryBuilder<ResearchRecipe>())
            .setName(new ResourceLocation(AncientWarfareCore.modID, "research_recipes"))
            .setType(ResearchRecipe.class)
                .setMaxID(Integer.MAX_VALUE >> 5)
                .disableSaving()
                .allowModification()
                .create();

    public static void init() {
        //noop - just call this so that the static final gets initialized at proper time
    }

    /*
     * First search within research recipes, then delegates to CraftingManager.findMatchingRecipe
     */
    public static ItemStack findMatchingRecipe(InventoryCrafting inventory, World world, String playerName) {
        if (world == null)
            return ItemStack.EMPTY;
        if (playerName != null && !playerName.isEmpty()) {
            for (ResearchRecipe recipe : RESEARCH_RECIPES) {
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
            if (recipe.getNeededResearch() != - 1 && AWCoreStatics.isItemResearcheable(item) && AWCoreStatics.useResearchSystem) {
                RESEARCH_RECIPES.register(recipe);
            } else {
                ForgeRegistries.RECIPES.register(new ResearchRecipe.IRecipeWrapper(recipe));
            }
        }
    }

    public static List<ResearchRecipe> getRecipes() {
        return RESEARCH_RECIPES.getValues();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ResearchRecipeFactory factory = new ResearchRecipeFactory();
    private static final Method LOAD_CONSTANTS = ReflectionHelper.findMethod(JsonContext.class, "loadConstants",null, JsonObject[].class);

    public static void loadRecipes() {
        Loader.instance().getActiveModList().forEach(AWCraftingManager::loadRecipes);
    }

    private static void loadRecipes(ModContainer mod) {
        JsonContext ctx = new JsonContext(mod.getModId());

        findFiles(mod, "assets/" + mod.getModId() + "/research_recipes",
            root ->
            {
                Path fPath = root.resolve("_constants.json");
                if (fPath != null && Files.exists(fPath))
                {
                    BufferedReader reader = null;
                    try
                    {
                        reader = Files.newBufferedReader(fPath);
                        JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
                        LOAD_CONSTANTS.invoke(ctx, new Object[]{json});
                    }
                    catch (IOException e)
                    {
                        FMLLog.log.error("Error loading _constants.json: ", e);
                        return false;
                    }
                    catch(IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    finally
                    {
                        IOUtils.closeQuietly(reader);
                    }
                }
                return true;
            },
            (root, file) -> {
            Loader.instance().setActiveModContainer(mod);

            String relative = root.relativize(file).toString();
            if(!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
                return;

            String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
            ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

            BufferedReader reader = null;
            try {
                reader = Files.newBufferedReader(file);
                JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
                String type = ctx.appendModId(JsonUtils.getString(json, "type"));
                if(type.equals(AncientWarfareCore.modID + ":research_recipe")) {
                    ResearchRecipe recipe = factory.parse(ctx, json);
                    recipe.setRegistryName(key);
                    addRecipe(recipe);
                } else {
                    AncientWarfareCore.log.info("Skipping recipe {} of type {} because it's not AW research recipe", key, type);
                }
            }
            catch(JsonParseException e) {
                AncientWarfareCore.log.error("Parsing error loading recipe {}", key, e);
            }
            catch(IOException e) {
                AncientWarfareCore.log.error("Couldn't read recipe {} from {}", key, file, e);
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
        });
    }

    private static void findFiles(ModContainer mod, String base, Function<Path, Boolean> preprocessor, @Nullable BiConsumer<Path, Path> processor) {
        FileSystem fs = null;
        try {
            File source = mod.getSource();

            @Nullable Path root = null;
            if(source.isFile()) {
                try {
                    fs = FileSystems.newFileSystem(source.toPath(), null);
                    root = fs.getPath("/" + base);
                }
                catch(IOException e) {
                    AncientWarfareCore.log.error("Error loading FileSystem from jar: ", e);
                    return;
                }
            } else if(source.isDirectory()) {
                root = source.toPath().resolve(base);
            }

            if(root == null || !Files.exists(root))
                return;

            if (preprocessor != null)
            {
                Boolean cont = preprocessor.apply(root);
                if (cont == null || !cont)
                    return;
            }

            if(processor != null) {
                Iterator<Path> itr;
                try {
                    itr = Files.walk(root).iterator();
                }
                catch(IOException e) {
                    AncientWarfareCore.log.error("Error iterating filesystem for: {}", mod.getModId(), e);
                    return;
                }

                while(itr != null && itr.hasNext()) {
                    processor.accept(root, itr.next());
                }
            }
        }
        finally {
            IOUtils.closeQuietly(fs);
        }
    }
}
