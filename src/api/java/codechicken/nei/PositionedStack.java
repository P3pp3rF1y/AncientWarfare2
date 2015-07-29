package codechicken.nei;

import net.minecraft.item.ItemStack;

/**
 * From NotEnoughItems.
 * A dummy class, keeping only signatures and docs.
 */
public class PositionedStack
{
    public int relx;
    public int rely;
    public ItemStack items[];

    public PositionedStack(Object object, int x, int y, boolean genPerms)
    {
        relx = x;
        rely = y;
    }

    public PositionedStack(Object object, int x, int y)
    {
        this(object, x, y, true);
    }

    public void setMaxSize(int i)
    {
    }

    public PositionedStack copy()
    {
        return new PositionedStack(items, relx, rely);
    }
}
