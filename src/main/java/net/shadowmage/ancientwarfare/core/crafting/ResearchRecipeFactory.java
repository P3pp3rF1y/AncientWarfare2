package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

public class ResearchRecipeFactory implements IRecipeFactory {
	@Override
	public IRecipe parse(JsonContext context, JsonObject json) {
		NonNullList<Ingredient> ingredients = NonNullList.create();
		for(JsonElement ele : JsonUtils.getJsonArray(json, "ingredients"))
			ingredients.add(CraftingHelper.getIngredient(ele, context));

		if(ingredients.isEmpty()) {
			throw new JsonParseException("No ingredients for research recipe");
		}
		if(ingredients.size() > 1) {
			throw new JsonParseException("Too many ingredients for research recipe");
		}

		ItemStack result = CraftingHelper.getItemStack(JsonUtils.getJsonObject(json, "result"), context);

		String research = JsonUtils.getString(json, "research");

		if (!AWCoreStatics.useResearchSystem) {
			return new ShapedOreRecipe(new ResourceLocation(AncientWarfareCore.modID, "no_research_recipe"), result, ingredients);
		}

		return new ResearchRecipe(research, result, ingredients);
	}
}
