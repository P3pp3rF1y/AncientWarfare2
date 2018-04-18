package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.compat.jei.AWJEIPlugin;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.NoRecipeWrapper;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.RegularCraftingWrapper;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.ResearchCraftingWrapper;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.TriConsumer;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class AWCraftingManager {
	public static final IForgeRegistry<ResearchRecipeBase> RESEARCH_RECIPES = (new RegistryBuilder<ResearchRecipeBase>())
			.setName(new ResourceLocation(AncientWarfareCore.modID, "research_recipes")).setType(ResearchRecipeBase.class).setMaxID(Integer.MAX_VALUE >> 5)
			.disableSaving().allowModification().create();

	public static void init() {
		//noop - just call this so that the static final gets initialized at proper time
	}

	private static List<ICraftingRecipe> findMatchingResearchRecipes(InventoryCrafting inventory, World world, String playerName) {
		return findMatchingResearchRecipes(inventory, world, playerName, true);
	}

	private static List<ICraftingRecipe> findMatchingResearchRecipes(InventoryCrafting inventory, World world, String playerName, boolean checkPlayerResearch) {
		List<ICraftingRecipe> ret = new ArrayList<>();
		if (world == null)
			return ret;
		for (ResearchRecipeBase recipe : RESEARCH_RECIPES) {
			if (recipe.matches(inventory, world) && (!checkPlayerResearch || canPlayerCraft(recipe, world, playerName))) {
				ret.add(new ResearchCraftingWrapper(recipe));
			}
		}
		return ret;
	}

	private static List<ICraftingRecipe> findMatchingRegularRecipes(InventoryCrafting inventory, World world) {
		List<ICraftingRecipe> ret = new ArrayList<>();
		if (world == null) {
			return ret;
		}
		for (IRecipe recipe : CraftingManager.REGISTRY) {
			if (recipe.matches(inventory, world)) {
				ret.add(new RegularCraftingWrapper(recipe));
			}
		}
		return ret;
	}

	private static List<ICraftingRecipe> findMatchingRecipesNoResearchCheck(InventoryCrafting inventory, World world) {
		List<ICraftingRecipe> recipes = findMatchingResearchRecipes(inventory, world, "", false);
		recipes.addAll(findMatchingRegularRecipes(inventory, world));

		return recipes;
	}

	public static List<ICraftingRecipe> findMatchingRecipes(InventoryCrafting inventory, World world, String playerName) {
		List<ICraftingRecipe> recipes = findMatchingResearchRecipes(inventory, world, playerName);
		recipes.addAll(findMatchingRegularRecipes(inventory, world));

		return recipes;
	}

	public static boolean canPlayerCraft(World world, String playerName, int research) {
		return research == -1 || ResearchTracker.INSTANCE.hasPlayerCompleted(world, playerName, research);
	}

	private static boolean canPlayerCraft(ResearchRecipeBase recipe, World world, String playerName) {
		return !AWCoreStatics.useResearchSystem || canPlayerCraft(world, playerName, recipe.getNeededResearch());
	}

	public static void addRecipe(ResearchRecipeBase recipe, boolean checkForExistence) {
		Item item = recipe.getRecipeOutput().getItem();
		if (AWCoreStatics.isItemCraftable(item)) {
			if ((recipe.getNeededResearch() != -1 && AWCoreStatics.isItemResearcheable(item) && AWCoreStatics.useResearchSystem) || hasCountIngredient(
					recipe)) {
				if (!checkForExistence || !RESEARCH_RECIPES.containsKey(recipe.getRegistryName())) {
					RESEARCH_RECIPES.register(recipe);
				}
			} else {
				IRecipe r = recipe.getCraftingRecipe();
				ForgeRegistries.RECIPES.register(r);
				if (Loader.isModLoaded("jei")) {
					AWJEIPlugin.addWrappedRecipe(r);
				}
			}
		}
	}

	private static boolean hasCountIngredient(ResearchRecipeBase recipe) {
		return recipe.getIngredients().stream().anyMatch(i -> i instanceof IIngredientCount);
	}

	public static List<ResearchRecipeBase> getRecipes() {
		return RESEARCH_RECIPES.getValues();
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final ResearchRecipeFactory factory = new ResearchRecipeFactory();
	private static final Method LOAD_CONSTANTS = ReflectionHelper.findMethod(JsonContext.class, "loadConstants", null, JsonObject[].class);

	public static void loadRecipes() {
		ModContainer awModContainer = Loader.instance().activeModContainer();
		CraftingHelper.register(new ResourceLocation(AncientWarfareCore.modID, "item_count"),
				(IIngredientFactory) (c, j) -> new IngredientCount(CraftingHelper.getItemStack(j, c)));
		CraftingHelper.register(new ResourceLocation(AncientWarfareCore.modID, "ore_dict_count"),
				(IIngredientFactory) (c, j) -> new IngredientOreCount(JsonUtils.getString(j, "ore"), JsonUtils.getInt(j, "count", 1)));

		loadRecipes(awModContainer, new File(AWCoreStatics.configPathForFiles + "research_recipes"), "");
		Loader.instance().getActiveModList().forEach(m -> AWCraftingManager.loadRecipes(m, m.getSource(), "assets/" + m.getModId() + "/research_recipes"));
	}

	private static void loadRecipes(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		findFiles(source, base, root -> {
			Path fPath = root.resolve("_constants.json");
			if (fPath != null && Files.exists(fPath)) {
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(fPath);
					JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
					LOAD_CONSTANTS.invoke(ctx, new Object[] {json});
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error loading _constants.json: ", e);
					return false;
				}
				catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				finally {
					IOUtils.closeQuietly(reader);
				}
			}
			return true;
		}, (root, file) -> {
			Loader.instance().setActiveModContainer(mod);

			String relative = root.relativize(file).toString();
			if (!"json".equals(FilenameUtils.getExtension(file.toString())) || relative.startsWith("_"))
				return;

			String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
			ResourceLocation key = new ResourceLocation(ctx.getModId(), name);

			BufferedReader reader = null;
			try {
				reader = Files.newBufferedReader(file);
				JsonObject json = JsonUtils.fromJson(GSON, reader, JsonObject.class);
				String type = ctx.appendModId(JsonUtils.getString(json, "type"));
				if (type.equals(AncientWarfareCore.modID + ":research_recipe") || type.equals(AncientWarfareCore.modID + ":shapeless_research_recipe")) {
					ResearchRecipeBase recipe = factory.parse(ctx, json);
					recipe.setRegistryName(key);
					addRecipe(recipe, mod.getModId().equals(AncientWarfareCore.modID));
				} else {
					AncientWarfareCore.log.info("Skipping recipe {} of type {} because it's not AW research recipe", key, type);
				}
			}
			catch (JsonParseException e) {
				AncientWarfareCore.log.error("Parsing error loading recipe {}", key, e);
			}
			catch (IOException e) {
				AncientWarfareCore.log.error("Couldn't read recipe {} from {}", key, file, e);
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		});
	}

	private static void findFiles(File source, String base, Function<Path, Boolean> preprocessor, @Nullable BiConsumer<Path, Path> processor) {
		FileSystem fs = null;
		try {
			@Nullable Path root = null;
			if (source.isFile()) {
				try {
					fs = FileSystems.newFileSystem(source.toPath(), null);
					root = fs.getPath("/" + base);
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error loading FileSystem from jar: ", e);
					return;
				}
			} else if (source.isDirectory()) {
				root = source.toPath().resolve(base);
			}

			if (root == null || !Files.exists(root))
				return;

			if (preprocessor != null) {
				Boolean cont = preprocessor.apply(root);
				if (cont == null || !cont)
					return;
			}

			if (processor != null) {
				Iterator<Path> itr;
				try {
					itr = Files.walk(root).iterator();
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error iterating filesystem for: {}", root, e);
					return;
				}

				while (itr != null && itr.hasNext()) {
					processor.accept(root, itr.next());
				}
			}
		}
		finally {
			IOUtils.closeQuietly(fs);
		}
	}

	public static int getMatchingIngredientCount(@Nullable ResearchRecipeBase researchRecipe, ItemStack stack) {
		if (researchRecipe == null) {
			return 1;
		}

		for (Ingredient i : researchRecipe.getIngredients()) {
			if (i.apply(stack)) {
				return i instanceof IIngredientCount ? ((IIngredientCount) i).getCount() : 1;
			}
		}
		return 1;
	}

	public static ICraftingRecipe getRecipe(RecipeResourceLocation recipe) {
		switch (recipe.getRecipeType()) {
			case REGULAR:
				IRecipe regRecipe = CraftingManager.REGISTRY.getObject(recipe.getResourceLocation());
				return regRecipe != null ? new RegularCraftingWrapper(regRecipe) : NoRecipeWrapper.INSTANCE;
			case RESEARCH:
				ResearchRecipeBase resRecipe = RESEARCH_RECIPES.getValue(recipe.getResourceLocation());
				return resRecipe != null ? new ResearchCraftingWrapper(resRecipe) : NoRecipeWrapper.INSTANCE;
			default:
				return NoRecipeWrapper.INSTANCE;
		}
	}

	public static InventoryCrafting fillCraftingMatrixFromInventory(List<ItemStack> resources) {
		InventoryCrafting invCrafting = new InventoryCrafting(new Container() {
			@Override
			public boolean canInteractWith(EntityPlayer playerIn) {
				return true;
			}

			@Override
			public void onCraftMatrixChanged(IInventory inventoryIn) {
			}
		}, 3, 3);

		for (int i = 0; i < resources.size(); i++) {
			invCrafting.setInventorySlotContents(i, resources.get(i));
		}

		return invCrafting;
	}

	public static ICraftingRecipe findMatchingRecipe(World world, NonNullList<ItemStack> inputs, ItemStack result) {
		InventoryCrafting inv = fillCraftingMatrixFromInventory(inputs);
		List<ICraftingRecipe> recipes = findMatchingRecipesNoResearchCheck(inv, world);
		return recipes.stream().filter(r -> r.getRecipeOutput().isItemEqual(result)).findFirst().orElse(NoRecipeWrapper.INSTANCE);
	}

	public static boolean canCraftFromInventory(ICraftingRecipe recipe, IItemHandler inventory) {
		return getRecipeInventoryMatch(recipe, inventory, () -> true, (b, i, s) -> b, (b, in) -> false, true);
	}

	public static NonNullList<ItemStack> getRecipeInventoryMatch(ICraftingRecipe recipe, IItemHandler inventory) {
		return getRecipeInventoryMatch(recipe, inventory, () -> NonNullList.withSize(9, ItemStack.EMPTY),
				(TriConsumer<NonNullList<ItemStack>, Integer, ItemStack>) NonNullList::set, (a, in) -> a.clear(), true);
	}

	public static <T> T getRecipeInventoryMatch(ICraftingRecipe recipe, IItemHandler inventory, Supplier<T> initialize,
			TriConsumer<T, Integer, ItemStack> onMatch, BiConsumer<T, Ingredient> onFail, boolean stopOnFail) {
		return getRecipeInventoryMatch(recipe, inventory, initialize, (t, i, s) -> {
			onMatch.accept(t, i, s);
			return t;
		}, (t, in) -> {
			onFail.accept(t, in);
			return t;
		}, stopOnFail);
	}

	public static <T> T getRecipeInventoryMatch(ICraftingRecipe recipe, IItemHandler inventory, Supplier<T> initialize,
			TriFunction<T, Integer, ItemStack, T> onMatch, BiFunction<T, Ingredient, T> onFail, boolean stopOnFail) {
		T ret = initialize.get();

		if (!recipe.isValid()) {
			return ret;
		}

		List<Ingredient> ingredients = recipe.getIngredients();

		IItemHandler clonedResourceInventory = InventoryTools.cloneItemHandler(inventory);
		for (int i = 0; i < ingredients.size(); i++) {
			Ingredient ingredient = ingredients.get(i);
			if (ingredient.apply(ItemStack.EMPTY)) { //skip empty ingredients
				continue;
			}

			ItemStack stackFound = getIngredientInventoryMatch(clonedResourceInventory, ingredient);

			if (stackFound.isEmpty()) {
				ret = onFail.apply(ret, ingredient);
				if (stopOnFail) {
					return ret;
				}
			} else {
				ret = onMatch.apply(ret, i, stackFound);
			}
		}

		return ret;
	}

	private static ItemStack getIngredientInventoryMatch(IItemHandler clonedResourceInventory, Ingredient ingredient) {
		ItemStack stackFound = ItemStack.EMPTY;
		int count = ingredient instanceof IIngredientCount ? ((IIngredientCount) ingredient).getCount() : 1;
		for (int slot = 0; slot < clonedResourceInventory.getSlots(); slot++) {
			ItemStack resourceStack = clonedResourceInventory.getStackInSlot(slot);

			if (!resourceStack.isEmpty()) {
				//required for ingredient to actually see proper count and say it's a good item
				//e.g. ingredient requires stack of 3 but inventory only has 3 stacks of 1 of the item - that's still a match for the recipe
				ItemStack properCountStack = new ItemStack(resourceStack.writeToNBT(new NBTTagCompound()));
				properCountStack.setCount(count);

				if (ingredient.apply(properCountStack)) {
					ItemStack removedStack = InventoryTools.removeItems(clonedResourceInventory, resourceStack, count, true);

					if (removedStack.getCount() == count) {
						InventoryTools.removeItems(clonedResourceInventory, resourceStack, count, false);
						stackFound = removedStack;
						break;
					}
				}
			}
		}
		return stackFound;
	}

	private interface TriFunction<K, V, S, R> {
		R apply(K k, V v, S s);
	}
}
