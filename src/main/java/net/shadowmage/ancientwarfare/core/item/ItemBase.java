package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;

/*
 * Handle subtypes through ItemStack damage values
 */
public class ItemBase extends Item {

    private final HashMap<Integer, String> subItems = new HashMap<>();
//    private final HashMap<Integer, IIcon> subItemIcons = new HashMap<>();

    public ItemBase() {
        super();
        this.setHasSubtypes(true);
    }

    @Override
    public boolean getShareTag() {
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (Integer num : subItems.keySet()) {
            items.add(new ItemStack(this, 1, num));
        }
    }

//    @Override
//    public IIcon getIconFromDamage(int par1) {
//        return subItemIcons.get(par1);
//    }
//
//    @Override
//    public void registerIcons(IIconRegister par1IconRegister) {
//        for (Integer num : subItems.keySet()) {
//            subItemIcons.put(num, par1IconRegister.registerIcon(subItems.get(num)));
//        }
//    }

    public void addSubItem(int num, String texture) {
        if (!subItems.containsKey(num))
            subItems.put(num, texture);
    }

    public void addSubItem(int num, String text, String ore){
        addSubItem(num, text);
        OreDictionary.registerOre(ore, new ItemStack(this, 1, num));
    }

    public ItemStack getSubItem(int num){
        return new ItemStack(this, 1, num);
    }
}
