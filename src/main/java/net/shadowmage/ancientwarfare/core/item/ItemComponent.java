package net.shadowmage.ancientwarfare.core.item;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemComponent extends Item
{

public static int WOODEN_GEAR_SET = 0;
public static int IRON_GEAR_SET = 1;
public static int WORKER_EQUIPMENT_BUNDLE;
public static int COMBAT_EQUIPMENT_BUNDLE;
public static int NPC_FOOD_BUNDLE;

Set<Integer> subItems = new HashSet<Integer>();

public ItemComponent(String regName)
  {
  this.setUnlocalizedName(regName);
  this.setHasSubtypes(true);
  }

@Override
public String getUnlocalizedName(ItemStack par1ItemStack)
  {
  return super.getUnlocalizedName(par1ItemStack)+"."+par1ItemStack.getItemDamage();
  }

@Override
public void getSubItems(Item item, CreativeTabs p_150895_2_, List list)
  {
  for(Integer num : subItems)
    {
    list.add(new ItemStack(item,1,num));
    }
  }

public void addSubItem(int num){subItems.add(num);}

}
