package net.shadowmage.ancientwarfare.core.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileResearchStation extends TileEntity implements IWorkSite, IInventory, ITorqueReceiver, IOwnable, IInteractableTile
{

protected String owningPlayer = "";

public InventoryBasic bookInventory = new InventoryBasic(1);
public InventoryBasic resourceInventory = new InventoryBasic(9);

int startCheckDelay = 0;
int startCheckDelayMax = 40;

public boolean useAdjacentInventory;

double maxEnergyStored = 1600;
double maxInput = 100;
private double storedEnergy;

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
public boolean canUpdate()
  {
  return true;
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}    
  if(startCheckDelay>0)
    {
    startCheckDelay--;
    }
  if(storedEnergy>=AWCoreStatics.energyPerResearchUnit)
    {
    storedEnergy -= AWCoreStatics.energyPerResearchUnit;
    workTick(1);
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  owningPlayer=tag.getString("owningPlayer");
  InventoryTools.readInventoryFromNBT(bookInventory, tag.getCompoundTag("bookInventory"));
  InventoryTools.readInventoryFromNBT(resourceInventory, tag.getCompoundTag("resourceInventory"));
  this.useAdjacentInventory = tag.getBoolean("useAdjacentInventory");
  this.storedEnergy = tag.getDouble("storedEnergy");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("owningPlayer", owningPlayer);
  tag.setTag("bookInventory", InventoryTools.writeInventoryToNBT(bookInventory, new NBTTagCompound()));  
  tag.setTag("resourceInventory", InventoryTools.writeInventoryToNBT(resourceInventory, new NBTTagCompound()));  
  tag.setBoolean("useAdjacentInventory", useAdjacentInventory);  
  tag.setDouble("storedEnergy", storedEnergy);
  }

@Override
public boolean hasWork()
  {
  return storedEnergy<maxEnergyStored;
//  if(storedEnergy>=maxEnergyStored){return false;}
//  String name = getCrafterName();
//  if(name==null){return false;}
//  int goal = ResearchTracker.instance().getCurrentGoal(worldObj, name);
//  if(goal>=0){return true;}
//  List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, name);  
//  if(!queue.isEmpty() && startCheckDelay==0){return true;}
//  return false;
  }

private void workTick(int tickCount)
  {
  String name = getCrafterName();
  if(name==null){return;}
  int goal = ResearchTracker.instance().getCurrentGoal(worldObj, name);
  if(goal>=0)
    {
    ResearchGoal g1 = ResearchGoal.getGoal(goal);
    int progress = ResearchTracker.instance().getProgress(worldObj, name);
    progress+=tickCount;
    if(progress>=g1.getTotalResearchTime())
      {
      ResearchTracker.instance().finishResearch(worldObj, getCrafterName(), goal);
      tryStartNextResearch(name);
      }
    else
      {
      ResearchTracker.instance().setProgress(worldObj, name, progress);
      }
    }
  else
    {
    tryStartNextResearch(name);
    }    
  }

private void tryStartNextResearch(String name)
  {
  List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, name);    
  if(!queue.isEmpty())
    {
    int g = queue.get(0);
    ResearchGoal g1 = ResearchGoal.getGoal(g);
    if(g1==null){return;}
    if(g1.tryStart(resourceInventory, -1))
      {
      ResearchTracker.instance().startResearch(worldObj, getCrafterName(), g);        
      }
    else if(useAdjacentInventory)
      {
      TileEntity t;
      boolean started = false;
      if((t=worldObj.getTileEntity(xCoord-1, yCoord, zCoord)) instanceof IInventory)
        {
        started = g1.tryStart((IInventory)t, -1);
        }
      if(!started && (t=worldObj.getTileEntity(xCoord+1, yCoord, zCoord)) instanceof IInventory)
        {
        started = g1.tryStart((IInventory)t, -1);
        }
      if(!started && (t=worldObj.getTileEntity(xCoord, yCoord, zCoord-1)) instanceof IInventory)
        {
        started = g1.tryStart((IInventory)t, -1);
        }
      if(!started && (t=worldObj.getTileEntity(xCoord, yCoord, zCoord+1)) instanceof IInventory)
        {
        started = g1.tryStart((IInventory)t, -1);
        }
      }
    } 
  startCheckDelay = startCheckDelayMax;
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.RESEARCH;
  }

@Override
public final Team getTeam()
  {  
  return worldObj.getScoreboard().getPlayersTeam(owningPlayer);
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  return null;
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

@Override
public int getSizeInventory()
  {
  return resourceInventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return resourceInventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return resourceInventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return resourceInventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  resourceInventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return resourceInventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return resourceInventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return resourceInventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return resourceInventory.isUseableByPlayer(var1);
  }

@Override
public void openInventory()
  {
  resourceInventory.openInventory();
  }

@Override
public void closeInventory()
  {
  resourceInventory.closeInventory();
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return resourceInventory.isItemValidForSlot(var1, var2);
  }

@Override
public void addEnergyFromWorker(IWorker worker)
  {
  storedEnergy += AWCoreStatics.energyPerWorkUnit * worker.getWorkEffectiveness(getWorkType());
  if(storedEnergy>getMaxEnergy()){storedEnergy = getMaxEnergy();}
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  storedEnergy+=AWCoreStatics.energyPerWorkUnit;
  if(storedEnergy>getMaxEnergy()){storedEnergy=getMaxEnergy();}
  }

@Override
public void setOwnerName(String name)
  {
  this.owningPlayer=name==null ? "" : name;
  }

@Override
public String getOwnerName()
  {
  return owningPlayer;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  //TODO validate team/owner status
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_RESEARCH_STATION, xCoord, yCoord, zCoord);    
    }
  return false;
  }

}
