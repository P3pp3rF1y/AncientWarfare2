package net.shadowmage.ancientwarfare.structure.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;

public class TileDraftingStation extends TileEntity
{

private String structureName;//structure pulled from live structure list anytime a ref is needed
private boolean isStarted;//has started compiling resources -- will need input to cancel
private ArrayList<ItemStack> neededResources = new ArrayList<ItemStack>();
private boolean isFinished;//is finished compiling resources, awaiting output-slot availability
private int remainingTime;

public InventoryBasic inputSlots = new InventoryBasic(27);
public InventoryBasic outputSlot = new InventoryBasic(1);

public TileDraftingStation()
  {
  
  }

public String getCurrentTemplateName()
  {
  return structureName;
  }

public boolean isStarted()
  {
  return isStarted;
  }

public boolean isFinished()
  {
  return isFinished;
  }

public int getRemainingTime()
  {
  return remainingTime;
  }

public List<ItemStack> getNeededResources()
  {
  return neededResources;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  { 
  super.readFromNBT(tag);
  inputSlots.readFromNBT(tag.getCompoundTag("inputInventory"));
  outputSlot.readFromNBT(tag.getCompoundTag("outputInventory"));
  if(tag.hasKey("structureName"))
    {
    structureName = tag.getString("structureName");
    }
  else
    {
    structureName = null;
    }
  isStarted = tag.getBoolean("isStarted");
  isFinished = tag.getBoolean("isFinished");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagCompound tag1 = new NBTTagCompound();
  inputSlots.writeToNBT(tag1);
  tag.setTag("inputInventory", tag1);
  
  tag1 = new NBTTagCompound();
  outputSlot.writeToNBT(tag1);
  tag.setTag("outputInventory", tag1);
  
  if(structureName!=null){tag.setString("structureName", structureName);}
  tag.setBoolean("isStarted", isStarted);
  tag.setBoolean("isFinished", isFinished);  
  }

}
