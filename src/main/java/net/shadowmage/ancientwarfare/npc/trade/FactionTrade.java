package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class FactionTrade
{

private ItemStack[] input;
private ItemStack[] output;
private int refillFrequency;
private int ticksTilRefill;
private int maxAvailable;
private int currentAvailable;

public FactionTrade()
  {
  input = new ItemStack[9];
  output = new ItemStack[9];
  refillFrequency = 20*60*5;//five minutes per item refilled
  ticksTilRefill = refillFrequency;
  maxAvailable = 1;
  currentAvailable = 1;
  }

public ItemStack[] getInput(){return input;}

public ItemStack[] getOutput(){return output;}

public int getRefillFrequency(){return refillFrequency;}

public int getMaxAvailable(){return maxAvailable;}

public int getCurrentAvailable(){return currentAvailable;}

public void setRefillFrequency(int refill)
  {
  refillFrequency=refill;
  ticksTilRefill = refillFrequency;
  }

public void setMaxAvailable(int max)
  {
  maxAvailable=max;
  currentAvailable = max;
  }

public NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("refillFrequency", refillFrequency);
  tag.setInteger("ticksTilRefill", ticksTilRefill);
  tag.setInteger("maxAvailable", maxAvailable);
  tag.setInteger("currentAvailable", currentAvailable);
  
  NBTTagList list = new NBTTagList();
  NBTTagCompound itemTag;
  ItemStack stack;
  
  for(int i = 0; i < input.length; i++)
    {
    if(input[i]==null){continue;}
    itemTag = new NBTTagCompound();
    stack = input[i];
    InventoryTools.writeItemStack(stack, itemTag);
    tag.setInteger("slot", i);
    list.appendTag(itemTag);
    }
  tag.setTag("inputItems", list);
  
  list = new NBTTagList();
  for(int i = 0; i < output.length; i++)
    {
    if(output[i]==null){continue;}
    itemTag = new NBTTagCompound();
    stack = output[i];
    InventoryTools.writeItemStack(stack, itemTag);
    tag.setInteger("slot", i);
    list.appendTag(itemTag);
    }
  tag.setTag("outputItems", list);
  return tag;
  }

public void readFromNBT(NBTTagCompound tag)
  {
  refillFrequency = tag.getInteger("refillFrequency");
  ticksTilRefill = tag.getInteger("ticksTilRefill");
  maxAvailable = tag.getInteger("maxAvailable");
  currentAvailable = tag.getInteger("currentAvailable");
    
  ItemStack stack;
  NBTTagCompound itemTag;
  
  NBTTagList inputList = tag.getTagList("inputItems", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < inputList.tagCount(); i++)
    {
    itemTag = inputList.getCompoundTagAt(i);
    stack = InventoryTools.readItemStack(itemTag);
    input[itemTag.getInteger("slot")]=stack;
    }
  
  NBTTagList outputList = tag.getTagList("outputItems", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < outputList.tagCount(); i++)
    {
    itemTag = outputList.getCompoundTagAt(i);
    stack = InventoryTools.readItemStack(itemTag);
    output[itemTag.getInteger("slot")]=stack;  
    }
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
