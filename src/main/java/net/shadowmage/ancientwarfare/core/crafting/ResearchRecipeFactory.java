package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ResearchRecipeFactory {
	public ResearchRecipeBase parse(JsonContext context, JsonObject json) {
		String type = JsonUtils.getString(json, "type");
		if (type.equals("ancientwarfare:research_recipe")) {
			return parseShaped(context, json);
		} else if (type.equals("ancientwarfare:shapeless_research_recipe")) {
			return parseShapeless(context, json);
		}
		return null;
	}

	private ResearchRecipeBase parseShapeless(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ings = NonNullList.create();
		String research = JsonUtils.getString(json, "research");
		for (JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
			ings.add(CraftingHelper.getIngredient(ele, context));

		if (ings.isEmpty())
			throw new JsonParseException("No ingredients for shapeless recipe");

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);
		return new ShapelessResearchRecipe(research, ings, result);
	}

	private ResearchRecipeBase parseShaped(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.height = recipe.getHeight();
		primer.width = recipe.getWidth();
		primer.input = recipe.getIngredients();
		primer.mirrored = false;

		String research = JsonUtils.getString(json, "research");

		return new ShapedResearchRecipe(research, recipe.getRecipeOutput(), primer);
	}
}
