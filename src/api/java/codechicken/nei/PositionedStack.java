package codechicken.nei;

import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Simply an {@link ItemStack} with position.
 * Mainly used in the recipe handlers.
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

        if(genPerms)
            generatePermutations();
        else
            setPermutationToRender(0);
    }

    public PositionedStack(Object object, int x, int y)
    {
        this(object, x, y, true);
    }

    public void generatePermutations()
    {
        if(permutated)
            return;

        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
        for(ItemStack item : items)
        {
            if(item == null || item.getItem() == null)
                continue;

            if(item.getItemDamage() == Short.MAX_VALUE)
            {
                continue;
            }

            stacks.add(item.copy());
        }
        items = stacks.toArray(new ItemStack[0]);

        if(items.length == 0)
            items = new ItemStack[]{new ItemStack(Blocks.fire)};

        permutated = true;
        setPermutationToRender(0);
    }

    public void setMaxSize(int i)
    {
        for(ItemStack item : items)
            if(item.stackSize > i)
                item.stackSize = i;
    }

    public PositionedStack copy()
    {
        return new PositionedStack(items, relx, rely);
    }

    public void setPermutationToRender(int index)
    {
        item = items[index].copy();
        if(item.getItem() == null)
            item = new ItemStack(Blocks.fire);
        else if(item.getItemDamage() == OreDictionary.WILDCARD_VALUE)
            item.setItemDamage(0);
    }

    public boolean contains(ItemStack ingredient)
    {
        return false;
    }

    public boolean contains(Item ingred)
    {
        for(ItemStack item : items)
            if(item.getItem() == ingred)
                return true;

        return false;
    }
}
