package codechicken.nei.api;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;

public class API {
    public API() {
    }

    public static void registerRecipeHandler(ICraftingHandler handler) {
        //GuiCraftingRecipe.registerRecipeHandler(handler);
    }

    public static void registerUsageHandler(IUsageHandler handler) {
        //GuiUsageRecipe.registerUsageHandler(handler);
    }
}
