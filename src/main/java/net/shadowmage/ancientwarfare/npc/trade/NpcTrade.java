package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class NpcTrade
{
String[] npcTypes;
int minLevel = 0;
int maxLevel = 0;
ItemStack output;
List<ItemStack> input;

public NpcTrade(ItemStack output, List<ItemStack> input, int minLevel, int maxLevel, String[] types)
  {
  this.output = output;
  this.input = new ArrayList<ItemStack>();
  for(ItemStack stack : input)
    {
    this.input.add(stack);
    }
  this.minLevel = minLevel;
  this.maxLevel = maxLevel;
  this.npcTypes = types;
  }

@Override
public String toString()
  {
  return "NPC Trade: Output["+output+"] Input["+input+"] NpcTypes["+npcTypes+"] MinLevel["+minLevel+"] MaxLevel["+maxLevel+"]";  
  }

public ItemStack getResult()
  {
  return output;
  }

public List<ItemStack> getInput()
  {
  return input;
  }

public void removeItems(IInventory inventory)
  {
  for(ItemStack item : input)
    {
    InventoryTools.removeItems(inventory, -1, item, item.stackSize);
    }
  }

public boolean hasItems(IInventory inventory)
  {
  for(ItemStack item : input)
    {
    if(InventoryTools.getCountOf(inventory, -1, item)<item.stackSize)
      {
      return false;
      }
    }
  return true;
  }

}
