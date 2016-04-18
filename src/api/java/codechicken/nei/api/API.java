package codechicken.nei.api;

import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;

/**
 * From NotEnoughItems.
 * A dummy class, keeping only signatures and docs.
 */
public class API {

    public static void registerRecipeHandler(ICraftingHandler handler) {
    }

    public static void registerUsageHandler(IUsageHandler handler) {
    }

    public static void registerGuiOverlay(Class<? extends GuiContainer> classz, String ident, int x, int y) {

    }

    public static void registerGuiOverlayHandler(Class<? extends GuiContainer> classz, IOverlayHandler handler, String ident) {

    }

    public static void hideItem(ItemStack item){

    }
}
