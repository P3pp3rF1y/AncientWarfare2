package codechicken.nei;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * From NotEnoughItems.
 * A dummy class, keeping only signatures and docs.
 */
public class PositionedStack
{
    public int relx;
    public int rely;
    public ItemStack items[];
    //compatibility dummy
    public ItemStack item;

    private boolean permutated = false;

    public PositionedStack(Object object, int x, int y, boolean genPerms)
    {
        relx = x;
        rely = y;
    }

    public PositionedStack(Object object, int x, int y)
    {
        this(object, x, y, true);
    }

    public void generatePermutations()
    {
    }

    public void setMaxSize(int i)
    {
    }

    public PositionedStack copy()
    {
        return new PositionedStack(items, relx, rely);
    }

    public void setPermutationToRender(int index)
    {
    }

    public boolean contains(ItemStack ingredient)
    {
        return false;
    }

    public boolean contains(Item ingred)
    {
        return false;
    }
}
