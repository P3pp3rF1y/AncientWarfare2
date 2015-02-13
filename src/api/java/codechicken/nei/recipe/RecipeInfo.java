package codechicken.nei.recipe;

import net.minecraft.client.gui.inventory.GuiContainer;

public class RecipeInfo {

    public static boolean hasDefaultOverlay(GuiContainer gui, String ident) {
        return false;//return positionerMap.containsKey(new OverlayKey(gui.getClass(), ident));
    }

    public static boolean hasOverlayHandler(GuiContainer gui, String ident) {
        return false;//return overlayMap.containsKey(new OverlayKey(gui.getClass(), ident));
    }
}
