package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;

public class ShapedResearchRecipeCategory extends ResearchRecipeCategory implements IRecipeCategory<ResearchRecipeWrapper> {
	public static final String UID = "shaped_research_recipe";
	private final String localizedName = I18n.format("jei.recipe.research_recipe");

	public ShapedResearchRecipeCategory(IGuiHelper guiHelper) {
		super(guiHelper);
	}

	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return localizedName;
	}

}
