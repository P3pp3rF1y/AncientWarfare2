package net.shadowmage.ancientwarfare.core.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.ICraftingGridHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.config.Constants;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.List;

public class ResearchRecipeCategory implements IRecipeCategory<ResearchRecipeWrapper> {
    public static final String UID = "engineering_station_recipe";
    private static final int craftOutputSlot = 0;
    private static final int craftInputSlot = 1;
    private static final int width = 116;
    private static final int height = 54;
    private final String localizedName;
    private final IDrawable background;
    private final ICraftingGridHelper craftingGridHelper;
    private final IDrawable icon;

    public ResearchRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation location = Constants.RECIPE_GUI_VANILLA;
        background = guiHelper.createDrawable(location, 0, 60, width, height);
        localizedName = I18n.format("jei.recipe.research_recipe");
        craftingGridHelper = guiHelper.createCraftingGridHelper(craftInputSlot, craftOutputSlot);
        icon = guiHelper.createDrawable(new ResourceLocation(AncientWarfareCore.modID + ":textures/items/core/research_book.png"), 0, 0, 16, 16, 16, 16);
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }


    @Nullable
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public String getModName() {
        return AncientWarfareCore.modID;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, ResearchRecipeWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();

        guiItemStacks.init(craftOutputSlot, false, 94, 18);

        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                int index = craftInputSlot + x + (y * 3);
                guiItemStacks.init(index, true, x * 18, y * 18);
            }
        }

        List<List<ItemStack>> inputs = ingredients.getInputs(ItemStack.class);
        List<List<ItemStack>> outputs = ingredients.getOutputs(ItemStack.class);

        craftingGridHelper.setInputs(guiItemStacks, inputs, recipeWrapper.getWidth(), recipeWrapper.getHeight());
        guiItemStacks.set(craftOutputSlot, outputs.get(0));
    }
}
