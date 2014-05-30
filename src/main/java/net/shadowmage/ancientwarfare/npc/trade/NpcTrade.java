package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

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

}
