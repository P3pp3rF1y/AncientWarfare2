package net.shadowmage.ancientwarfare.npc.trade;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class FactionTrade
{

private List<ItemStack> input;
private List<ItemStack> output;
private int refillFrequency;
private int ticksTilRefill;
private int maxAvailable;
private int currentAvailable;

/**
 * No param nbt-constructor.
 */
public FactionTrade()
  {
  input = new ArrayList<ItemStack>();
  output = new ArrayList<ItemStack>();
  refillFrequency = 20*60*5;//five minutes per item refilled
  maxAvailable = 1;
  currentAvailable = 1;
  }

public FactionTrade(List<ItemStack> input, List<ItemStack> output, int maxAvailable, int refillFrequency)
  {
  this();
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  
  }

public void updateTrade(int ticks)
  {
  ticksTilRefill+=ticks;
  while(ticksTilRefill>=refillFrequency)
    {
    ticksTilRefill-=refillFrequency;
    if(currentAvailable<maxAvailable){currentAvailable++;}
    }
  }

}
