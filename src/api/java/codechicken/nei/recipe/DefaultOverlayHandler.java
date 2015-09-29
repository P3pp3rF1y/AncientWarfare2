package codechicken.nei.recipe;

import codechicken.nei.api.IOverlayHandler;
import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * From NotEnoughItems.
 * A dummy class, keeping only signatures and docs.
 */
public class DefaultOverlayHandler implements IOverlayHandler
{
    public DefaultOverlayHandler(int x, int y)
    {
        offsetx = x;
        offsety = y;
    }

    public DefaultOverlayHandler()
    {
        this(5, 11);
    }

    public int offsetx;
    public int offsety;

    @Override
    public void overlayRecipe(GuiContainer gui, IRecipeHandler recipe, int recipeIndex, boolean shift)
    {

    }
}
