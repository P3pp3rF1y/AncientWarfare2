package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;

public abstract class TileWorksiteBase extends TileEntity implements IWorkSite, IInventory, ISidedInventory, IInteractableTile, IBoundedTile, IOwnable
{
protected String owningPlayer = "";

protected ArrayList<ItemStack> inventoryOverflow = new ArrayList<ItemStack>();

private double maxEnergyStored = AWAutomationStatics.energyPerWorkUnit;
private double maxInput = maxEnergyStored;
private double storedEnergy;

public TileWorksiteBase()
  {
  
  }

protected abstract boolean processWork();

protected abstract boolean hasWorksiteWork();

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public String toString()
  {
  return "Worksite Base["+storedEnergy+"]";
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return true;
  }

@Override
public boolean hasWork()
  {
  return hasWorksiteWork() && !worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) && inventoryOverflow.isEmpty();  
  }

@Override
public String getOwnerName()
  {  
  return owningPlayer;
  }

@Override
public final boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  worldObj.theProfiler.startSection("AWWorksite");
  if(!inventoryOverflow.isEmpty())
    {
    updateOverflowInventory();
    } 
  if(hasWork() && getEnergyStored()>=getMaxEnergy())
    {
    if(processWork())
      {
      storedEnergy -= AWAutomationStatics.energyPerWorkUnit;
      if(storedEnergy<0){storedEnergy = 0.d;}
      }    
    }
  worldObj.theProfiler.endSection();
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit * worker.getWorkEffectiveness();
  }

protected abstract void updateOverflowInventory();

@Override
public abstract boolean onBlockClicked(EntityPlayer player);

public final String getOwningPlayer()
  {
  return owningPlayer;
  }

@Override
public void setOwnerName(String name)
  {
  if(name==null){name="";}
  this.owningPlayer = name;  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  if(owningPlayer!=null)
    {
    tag.setString("owner", owningPlayer);
    }
  if(!inventoryOverflow.isEmpty())
    {
    NBTTagList list = new NBTTagList();
    NBTTagCompound stackTag;
    for(ItemStack item : inventoryOverflow)
      {
      stackTag = new NBTTagCompound();
      stackTag = item.writeToNBT(stackTag);
      list.appendTag(stackTag);
      }
    tag.setTag("inventoryOverflow", list);
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  storedEnergy = tag.getDouble("storedEnergy");
  if(tag.hasKey("owner"))
    {
    owningPlayer = tag.getString("owner");
    }
  if(tag.hasKey("inventoryOverflow"))
    {
    NBTTagList list = tag.getTagList("inventoryOverflow", Constants.NBT.TAG_COMPOUND);
    NBTTagCompound itemTag;
    ItemStack stack;
    for(int i = 0; i < list.tagCount(); i++)
      {
      itemTag = list.getCompoundTagAt(i);
      stack = ItemStack.loadItemStackFromNBT(itemTag);
      if(stack!=null)
        {
        inventoryOverflow.add(stack);
        }
      }
    }
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
  }

}
