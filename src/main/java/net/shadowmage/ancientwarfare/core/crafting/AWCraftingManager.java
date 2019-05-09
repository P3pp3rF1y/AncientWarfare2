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
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.NoRecipeWrapper;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.RegularCraftingWrapper;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.ResearchCraftingWrapper;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.TriFunction;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.TriConsumer;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AWCraftingManager {
	private AWCraftingManager() {}

	private static final IForgeRegistry<ResearchRecipeBase> RESEARCH_RECIPES = (new RegistryBuilder<ResearchRecipeBase>()).setName(new ResourceLocation(AncientWarfareCore.MOD_ID, "research_recipes")).setType(ResearchRecipeBase.class).setMaxID(Integer.MAX_VALUE >> 5).disableSaving().allowModification().create();

	public static void init() {
		//noop - just call this so that the static final gets initialized at proper time
	}

	private static List<ICraftingRecipe> findMatchingResearchRecipes(InventoryCrafting inventory, World world, String playerName) {
		return findMatchingResearchRecipes(inventory, world, playerName, true);
	}

	private static List<ICraftingRecipe> findMatchingResearchRecipes(InventoryCrafting inventory,
			@Nullable World world, String playerName, boolean checkPlayerResearch) {
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

	private static List<ICraftingRecipe> findMatchingRegularRecipes(InventoryCrafting inventory, @Nullable World world) {
		List<ICraftingRecipe> ret = new ArrayList<>();
		if (world == null) {
			return ret;
		}
		for (IRecipe recipe : ForgeRegistries.RECIPES) {
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

	public static boolean canPlayerCraft(World world, String playerName, String research) {
		return ResearchTracker.INSTANCE.hasPlayerCompleted(world, playerName, research);
	}

	private static boolean canPlayerCraft(ResearchRecipeBase recipe, World world, String playerName) {
		return !AWCoreStatics.useResearchSystem || canPlayerCraft(world, playerName, recipe.getNeededResearch());
	}

	private static void addRecipe(ResearchRecipeBase recipe, boolean checkForExistence) {
		Item item = recipe.getRecipeOutput().getItem();
		if (AWCoreStatics.isItemCraftable(item)) {
			if ((AWCoreStatics.isItemResearcheable(item) && AWCoreStatics.useResearchSystem) || hasCountIngredient(recipe)) {
				NonNullList<ItemStack> subItems = NonNullList.create();
				//noinspection ConstantConditions
				item.getSubItems(item.getCreativeTab(), subItems);
				if (subItems.stream().anyMatch(s -> recipe.getRecipeOutput().isItemEqual(s))
						&& (!checkForExistence || !RESEARCH_RECIPES.containsKey(recipe.getRegistryName()))) {
					RESEARCH_RECIPES.register(recipe);
				}
			} else {
				if (!ForgeRegistries.RECIPES.containsKey(recipe.getRegistryName())) {
					ForgeRegistries.RECIPES.register(recipe.getCraftingRecipe());
				}
			}
		}
	}

	private static boolean hasCountIngredient(ResearchRecipeBase recipe) {
		return recipe.getIngredients().stream().anyMatch(i -> i instanceof IIngredientCount);
	}

	public static Collection<ResearchRecipeBase> getRecipes() {
		return RESEARCH_RECIPES.getValuesCollection();
	}

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final ResearchRecipeFactory factory = new ResearchRecipeFactory();
	private static final Method LOAD_CONSTANTS = ReflectionHelper.findMethod(JsonContext.class, "loadConstants", null, JsonObject[].class);

	public static void loadRecipes() {
		ModContainer awModContainer = Loader.instance().activeModContainer();

		//noinspection ConstantConditions
		loadRecipes(awModContainer, new File(AWCoreStatics.configPathForFiles + "research_recipes"), "");
		Loader.instance().getActiveModList().forEach(m -> AWCraftingManager.loadRecipes(m, m.getSource(), "assets/" + m.getModId() + "/research_recipes"));

		Loader.instance().setActiveModContainer(awModContainer);
	}

	public static void registerIngredients() {
		CraftingHelper.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "item_count"), (IIngredientFactory) (c, j) -> new IngredientCount(CraftingHelper.getItemStack(j, c)));
		CraftingHelper.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "ore_dict_count"), (IIngredientFactory) (c, j) -> new IngredientOreCount(JsonUtils.getString(j, "ore"), JsonUtils.getInt(j, "count", 1)));
		CraftingHelper.register(new ResourceLocation(AncientWarfareCore.MOD_ID, "item_nbt_relaxed"), (IIngredientFactory) (c, j) -> new IngredientNBTRelaxed(CraftingHelper.getItemStack(j, c)));
	}

	@SuppressWarnings({"squid:S3725", "squid:S3878"})
	private static void loadRecipes(ModContainer mod, File source, String base) {
		JsonContext ctx = new JsonContext(mod.getModId());

		FileUtils.findFiles(source, base, root -> {
			Path fPath = root.resolve("_constants.json");
			if (fPath != null && Files.exists(fPath)) {
				BufferedReader reader = null;
				try {
					reader = Files.newBufferedReader(fPath);
					JsonObject[] json = JsonUtils.fromJson(GSON, reader, JsonObject[].class);
					LOAD_CONSTANTS.invoke(ctx, new Object[] {json});
				}
				catch (IOException | IllegalAccessException | InvocationTargetException e) {
					AncientWarfareCore.LOG.error("Error loading _constants.json: ", e);
					return false;
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
				//noinspection ConstantConditions
				String type = ctx.appendModId(JsonUtils.getString(json, "type"));
				if (type.equals(AncientWarfareCore.MOD_ID + ":research_recipe") || type.equals(AncientWarfareCore.MOD_ID + ":shapeless_research_recipe")) {
					ResearchRecipeBase recipe = factory.parse(ctx, json);
					recipe.setRegistryName(key);
					addRecipe(recipe, mod.getModId().equals(AncientWarfareCore.MOD_ID));
				} else {
					AncientWarfareCore.LOG.info("Skipping recipe {} of type {} because it's not AW research recipe", key, type);
				}
			}
			catch (JsonParseException e) {
				AncientWarfareCore.LOG.error("Parsing error loading recipe {}", key, e);
			}
			catch (IOException e) {
				AncientWarfareCore.LOG.error("Couldn't read recipe {} from {}", key, file, e);
			}
			finally {
				IOUtils.closeQuietly(reader);
			}
		});
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
				//NOOP - dummy matrix so no change tracking
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

		if (recipes.isEmpty()) {
			return NoRecipeWrapper.INSTANCE;
		}

		//if there's only one match just return
		if (recipes.size() == 1) {
			return recipes.get(0);
		}

		//if there's multiple, filter by result and return the first that matches by result, just in case none match by result return the first recipe in the list
		return recipes.stream().filter(r -> recipeResultsEqual(result, r, inv)).findFirst().orElse(recipes.get(0));
	}

	private static boolean recipeResultsEqual(ItemStack result, ICraftingRecipe r, InventoryCrafting inv) {
		ItemStack otherResult = r.getCraftingResult(inv);
		if (otherResult.isEmpty()) {
			return false;
		}

		int[] oreIDs = OreDictionary.getOreIDs(result);
		if (oreIDs.length > 0) {
			int[] foundRecipeOreIDs = OreDictionary.getOreIDs(otherResult);
			return oreIDs.length == foundRecipeOreIDs.length && IntStream.of(oreIDs).allMatch(o -> IntStream.of(foundRecipeOreIDs).anyMatch(of -> o == of));
		}
		return otherResult.isItemEqual(result);
	}

	public static NonNullList<ItemStack> getRecipeInventoryMatch(ICraftingRecipe recipe, IItemHandler inventory) {
		return getRecipeInventoryMatch(recipe, NonNullList.create(), inventory);
	}

	public static NonNullList<ItemStack> getRecipeInventoryMatch(ICraftingRecipe recipe, List<ItemStack> exactStacks, IItemHandler inventory) {
		return getRecipeInventoryMatch(recipe, exactStacks, s -> InventoryTools.hasCountOrMore(inventory, s), inventory);
	}

	public static NonNullList<ItemStack> getRecipeInventoryMatch(ICraftingRecipe recipe, List<ItemStack> exactStacks, Function<ItemStack, Boolean> hasCountOrMore, IItemHandler inventory) {
		return getRecipeInventoryMatch(recipe, exactStacks, hasCountOrMore, inventory, () -> NonNullList.withSize(9, ItemStack.EMPTY), (list, slot, stack) -> {
			list.set(slot, stack);
			return list;
		}, (a, in) -> NonNullList.create());
	}

	public static <T> T getRecipeInventoryMatch(ICraftingRecipe recipe, IItemHandler inventory, Supplier<T> initialize, TriConsumer<T, Integer, ItemStack> onMatch, BiConsumer<T, Ingredient> onFail) {
		return getRecipeInventoryMatch(recipe, NonNullList.create(), s -> InventoryTools.hasCountOrMore(inventory, s), inventory, initialize, onMatch, onFail);
	}

	public static <T> T getRecipeInventoryMatch(ICraftingRecipe recipe, List<ItemStack> exactStacks, Function<ItemStack, Boolean> hasCountOrMore, IItemHandler inventory, Supplier<T> initialize, TriConsumer<T, Integer, ItemStack> onMatch, BiConsumer<T, Ingredient> onFail) {
		return getRecipeInventoryMatch(recipe, exactStacks, hasCountOrMore, inventory, initialize, (t, i, s) -> {
			onMatch.accept(t, i, s);
			return t;
		}, (t, in) -> {
			onFail.accept(t, in);
			return t;
		});
	}

	@SuppressWarnings("squid:UnusedPrivateMethod")
	private static <T> T getRecipeInventoryMatch(ICraftingRecipe recipe, List<ItemStack> exactStacks, Function<ItemStack, Boolean> hasCountOrMore, IItemHandler inventory, Supplier<T> initialize, TriFunction<T, Integer, ItemStack, T> onMatch, BiFunction<T, Ingredient, T> onFail) {
		T ret = initialize.get();

		if (!recipe.isValid()) {
			ret = onFail.apply(ret, Ingredient.EMPTY);
			return ret;
		}

		Map<Integer, IngredientMatchData> ingredientsData = getIngredientData(recipe.getIngredients());
		Map<Integer, Ingredient> remainingIngredients = getSlotIngredients(ingredientsData);

		ret = matchExactStacks(exactStacks, hasCountOrMore, onMatch, ret, ingredientsData, remainingIngredients);

		if (remainingIngredients.isEmpty()) {
			return ret;
		}

		ret = matchUsingRecipeIngredients(inventory, onMatch, ret, ingredientsData, remainingIngredients);

		ret = processFailedIngredients(onFail, ret, remainingIngredients);

		return ret;
	}

	private static <T> T matchUsingRecipeIngredients(IItemHandler inventory, TriFunction<T, Integer, ItemStack, T> onMatch, T ret, Map<Integer, IngredientMatchData> ingredientsData, Map<Integer, Ingredient> remainingIngredients) {
		for (int slot = 0; slot < inventory.getSlots() && !remainingIngredients.isEmpty(); slot++) {
			ItemStack resourceStack = inventory.getStackInSlot(slot);
			if (resourceStack.isEmpty()) {
				continue;
			}
			ret = matchIngredientsToStack(onMatch, ret, ingredientsData, remainingIngredients, resourceStack);
		}
		return ret;
	}

	private static <T> T matchIngredientsToStack(TriFunction<T, Integer, ItemStack, T> onMatch, T ret, Map<Integer, IngredientMatchData> ingredientsData, Map<Integer, Ingredient> remainingIngredients, ItemStack resourceStack) {
		int currentStackCount = resourceStack.getCount();

		Iterator<Map.Entry<Integer, Ingredient>> it = remainingIngredients.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Ingredient> entry = it.next();
			Ingredient ingredient = entry.getValue();

			if (ingredient.apply(resourceStack)) {
				IngredientMatchData ingredientData = ingredientsData.get(entry.getKey());
				int countToRemove = Math.min(ingredientData.remainingCount, currentStackCount);
				currentStackCount -= countToRemove;
				ingredientData.remainingCount -= countToRemove;
				if (ingredientData.remainingCount < 1) {
					ret = addSuccessfulMatch(ret, onMatch, resourceStack, ingredientData.count, ingredientData.index);
					it.remove();
				}
				if (currentStackCount <= 0) {
					break;
				}
			}
		}
		return ret;
	}

	private static <T> T matchExactStacks(List<ItemStack> exactStacks, Function<ItemStack, Boolean> hasCountOrMore, TriFunction<T, Integer, ItemStack, T> onMatch, T ret, Map<Integer, IngredientMatchData> ingredientsData, Map<Integer, Ingredient> remainingIngredients) {
		Map<ItemStack, Set<Integer>> consolidatedStacks = consolidateStacks(exactStacks, ingredientsData);
		for (Map.Entry<ItemStack, Set<Integer>> consolidatedStack : consolidatedStacks.entrySet()) {
			ItemStack stack = consolidatedStack.getKey();
			if (hasCountOrMore.apply(stack)) {
				for (Integer ingredientIndex : consolidatedStack.getValue()) {
					remainingIngredients.remove(ingredientIndex);
					if (ingredientsData.containsKey(ingredientIndex)) {
						IngredientMatchData ingredientData = ingredientsData.get(ingredientIndex);
						ret = addSuccessfulMatch(ret, onMatch, stack, ingredientData.count, ingredientData.index);
					} else {
						ret = addSuccessfulMatch(ret, onMatch, stack, 1, ingredientIndex);
					}
				}
			} else {
				for (int ingredientIndex : consolidatedStack.getValue()) {
					if (!remainingIngredients.containsKey(ingredientIndex)) {
						remainingIngredients.put(ingredientIndex, new IngredientCount(consolidatedStack.getKey()) {
							@Override
							public int getCount() {
								return 1;
							}
						});
					}
				}
			}
		}
		return ret;
	}

	private static <T> T addSuccessfulMatch(T ret, TriFunction<T, Integer, ItemStack, T> onMatch, ItemStack resourceStack, int count, int index) {
		ItemStack stackFound = resourceStack.copy();
		stackFound.setCount(count);
		ret = onMatch.apply(ret, index, stackFound);
		return ret;
	}

	private static Map<ItemStack, Set<Integer>> consolidateStacks(List<ItemStack> exactStacks, Map<Integer, IngredientMatchData> ingredientsData) {
		Map<ItemStack, Set<Integer>> ret = new HashMap<>();
		Set<Integer> matchedIngredientIndexes = new HashSet<>();
		int slot = 0;
		for (ItemStack stack : exactStacks) {
			consolidateStack(ingredientsData, ret, matchedIngredientIndexes, stack, slot);
			slot++;
		}

		return ret;
	}

	private static void consolidateStack(Map<Integer, IngredientMatchData> ingredientsData, Map<ItemStack, Set<Integer>> stackIngredientIndexes,
			Set<Integer> matchedIngredientIndexes, ItemStack stack, int slot) {
		if (stack.isEmpty()) {
			return;
		}
		int index = getIngredientIndexForStack(ingredientsData, matchedIngredientIndexes, stack);
		int count = 1;
		if (index == -1) {
			index = slot;
		} else {
			count = ingredientsData.get(index).count;
		}
		matchedIngredientIndexes.add(index);
		Optional<Map.Entry<ItemStack, Set<Integer>>> matching = getMatchingStackEntry(stackIngredientIndexes, stack);
		if (matching.isPresent()) {
			Map.Entry<ItemStack, Set<Integer>> entry = matching.get();
			entry.getKey().grow(count);
			entry.getValue().add(index);
		} else {
			Set<Integer> indexes = new HashSet<>();
			indexes.add(index);
			ItemStack stackCopy = stack.copy();
			stackCopy.setCount(count);
			stackIngredientIndexes.put(stackCopy, indexes);
		}
	}

	private static int getIngredientIndexForStack(Map<Integer, IngredientMatchData> ingredientsData, Set<Integer> matchedIngredientIndexes, ItemStack stack) {
		for (Map.Entry<Integer, IngredientMatchData> ingredientData : ingredientsData.entrySet()) {
			IngredientMatchData data = ingredientData.getValue();
			if (!matchedIngredientIndexes.contains(data.index) && data.ingredient.apply(stack)) {
				return data.index;
			}
		}
		return -1;
	}

	private static Optional<Map.Entry<ItemStack, Set<Integer>>> getMatchingStackEntry(Map<ItemStack, Set<Integer>> stacks, ItemStack stackToFind) {
		for (Map.Entry<ItemStack, Set<Integer>> entry : stacks.entrySet()) {
			if (InventoryTools.doItemStacksMatch(entry.getKey(), stackToFind)) {
				return Optional.of(entry);
			}
		}
		return Optional.empty();
	}

	private static Map<Integer, Ingredient> getSlotIngredients(Map<Integer, IngredientMatchData> ingredientsData) {
		Map<Integer, Ingredient> ret = new HashMap<>();
		ingredientsData.forEach((slot, ingredientData) -> ret.put(slot, ingredientData.ingredient));
		return ret;
	}

	private static <T> T processFailedIngredients(BiFunction<T, Ingredient, T> onFail, T ret, Map<Integer, Ingredient> remainingIngredients) {
		for (Ingredient ingredient : remainingIngredients.values()) {
			ret = onFail.apply(ret, ingredient);
		}
		return ret;
	}

	private static class IngredientMatchData {
		public Ingredient ingredient;
		public int index;
		public int count;
		public int remainingCount;

		private IngredientMatchData(Ingredient ingredient, int index, int count) {
			this.ingredient = ingredient;
			this.index = index;
			this.count = count;
			remainingCount = count;
		}
	}

	private static Map<Integer, IngredientMatchData> getIngredientData(NonNullList<Ingredient> ingredients) {
		Map<Integer, IngredientMatchData> ret = new LinkedHashMap<>();
		for (int slot = 0; slot < ingredients.size(); slot++) {
			Ingredient ingredient = ingredients.get(slot);

			if (ingredient.apply(ItemStack.EMPTY)) { //skip empty ingredients
				continue;
			}

			ret.put(slot, new IngredientMatchData(ingredient, slot, ingredient instanceof IIngredientCount ? ((IIngredientCount) ingredient).getCount() : 1));
		}

		return ret;
	}

	public static ItemStack getIngredientInventoryMatch(IItemHandler clonedResourceInventory, Ingredient ingredient) {
		ItemStack stackFound = ItemStack.EMPTY;
		int count = ingredient instanceof IIngredientCount ? ((IIngredientCount) ingredient).getCount() : 1;
		for (int slot = 0; slot < clonedResourceInventory.getSlots(); slot++) {
			ItemStack resourceStack = clonedResourceInventory.getStackInSlot(slot);

			if (!resourceStack.isEmpty()) {
				//required for ingredient to actually see proper count and say it's a good item
				//e.g. ingredient requires stack of 3 but inventory only has 3 stacks of 1 of the item - that's still a match for the recipe
				ItemStack properCountStack = resourceStack.copy();
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

	public static NonNullList<ItemStack> getReusableStacks(ICraftingRecipe recipe, InventoryCrafting craftMatrix) {
		return recipe.getRemainingItems(craftMatrix).stream().filter(s -> !s.isEmpty() && recipe.getIngredients().stream().anyMatch(i -> i.apply(s)))
				.collect(Collectors.toCollection(NonNullList::create));
	}
}
