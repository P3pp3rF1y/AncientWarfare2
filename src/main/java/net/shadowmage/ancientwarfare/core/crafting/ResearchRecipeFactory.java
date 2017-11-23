package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.JsonContext;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ResearchRecipeFactory {
	public ResearchRecipe parse(JsonContext context, JsonObject json) {
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.height = recipe.getHeight();
		primer.width = recipe.getWidth();
		primer.input = recipe.getIngredients();
		primer.mirrored = false;

		String research = JsonUtils.getString(json, "research");

		return new ResearchRecipe(research, recipe.getRecipeOutput(), primer);
	}
}
