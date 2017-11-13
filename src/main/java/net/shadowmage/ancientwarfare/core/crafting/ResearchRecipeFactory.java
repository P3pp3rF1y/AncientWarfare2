package net.shadowmage.ancientwarfare.core.crafting;

import com.google.gson.JsonObject;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.JsonUtils;
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
		ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

		CraftingHelper.ShapedPrimer primer = new CraftingHelper.ShapedPrimer();
		primer.height = recipe.getHeight();
		primer.width = recipe.getWidth();
		primer.input = recipe.getIngredients();
		primer.mirrored = false;

		String research = JsonUtils.getString(json, "research");

		if (!AWCoreStatics.useResearchSystem) {
			return new ShapedOreRecipe(new ResourceLocation(AncientWarfareCore.modID, "no_research_recipe"), recipe.getRecipeOutput(), primer);
		}

		return new ResearchRecipe(research, recipe.getRecipeOutput(), primer);
	}
}
