package net.shadowmage.ancientwarfare.core.tile;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileResearchStation extends TileEntity implements IWorkSite, IInventory, ITorqueTile, IOwnable, IInteractableTile,  IRotatableTile
{

protected String owningPlayer = "";
ForgeDirection orientation = ForgeDirection.NORTH;//default for old blocks

@Override
public void onBlockBroken()
  {
  // TODO
  }

public InventoryBasic bookInventory = new InventoryBasic(1);
public InventoryBasic resourceInventory = new InventoryBasic(9);

int startCheckDelay = 0;
int startCheckDelayMax = 40;

public boolean useAdjacentInventory;
public ForgeDirection inventoryDirection = ForgeDirection.NORTH;
public ForgeDirection inventorySide = ForgeDirection.NORTH;

double maxEnergyStored = 1600;
double maxInput = 100;
private double storedEnergy;

@Override
public int getBoundsMaxWidth(){return 0;}

@Override
public int getBoundsMaxHeight(){return 0;}

@Override
public EnumSet<WorksiteUpgrade> getUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}//NOOP

@Override
public EnumSet<WorksiteUpgrade> getValidUpgrades(){return EnumSet.noneOf(WorksiteUpgrade.class);}//NOOP

@Override
public void onPostBoundsAdjusted(){}//NOOP

@Override
public void addUpgrade(WorksiteUpgrade upgrade){}//NOOP

@Override
public void removeUpgrade(WorksiteUpgrade upgrade){}//NOOP

@Override
public void setBounds(BlockPosition p1, BlockPosition p2){}//NOOP

@Override
public void setWorkBoundsMax(BlockPosition max){}//NOOP

@Override
public void setWorkBoundsMin(BlockPosition min){}//NOOP

@Override
public void onBoundsAdjusted(){}//NOOP

@Override
public boolean userAdjustableBlocks(){return false;}//NOOP

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return 0;
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return false;
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return 0;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return false;
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  if(canInputTorque(from))
    {
    if(energy+getTorqueStored(null)>getMaxTorque(null))
      {
      energy = getMaxTorque(null)-getTorqueStored(null);
      }
    if(energy>getMaxTorqueInput(null))
      {
      energy = getMaxTorqueInput(null);
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return maxEnergyStored;
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return storedEnergy;
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return maxInput;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
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
  String name = getCrafterName();
  if(name==null){return;}  
  int goal = ResearchTracker.instance().getCurrentGoal(worldObj, name);
  boolean started = goal>=0;  
  if(started && storedEnergy>=AWCoreStatics.energyPerResearchUnit)
    {
    workTick(name, goal, 1);
    }
  else if(!started)
    {
    startCheckDelay--;
    if(startCheckDelay<=0)
      {
      tryStartNextResearch(name);      
      }
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
  if(tag.hasKey("orientation")){this.orientation = ForgeDirection.values()[tag.getInteger("orientation")];}  
  this.inventoryDirection = ForgeDirection.getOrientation(tag.getInteger("inventoryDirection"));
  this.inventorySide = ForgeDirection.getOrientation(tag.getInteger("inventorySide"));
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
  tag.setInteger("orientation", orientation.ordinal());
  tag.setInteger("inventoryDirection", inventoryDirection.ordinal());
  tag.setInteger("inventorySide", inventorySide.ordinal());
  }

@Override
public boolean hasWork()
  {
  return storedEnergy<maxEnergyStored;
  }

private void workTick(String name, int goal, int tickCount)
  {
  ResearchGoal g1 = ResearchGoal.getGoal(goal);
  int progress = ResearchTracker.instance().getProgress(worldObj, name);
  progress+=tickCount;
  if(progress >= g1.getTotalResearchTime())
    {
    ResearchTracker.instance().finishResearch(worldObj, getCrafterName(), goal);
    tryStartNextResearch(name);      
    }
  else
    {
    ResearchTracker.instance().setProgress(worldObj, name, progress);
    }
  storedEnergy-=AWCoreStatics.energyPerResearchUnit;
  }

private void tryStartNextResearch(String name)
  {
  List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, name);   
  if(!queue.isEmpty())
    {
    int goalId = queue.get(0);
    ResearchGoal goalInstance = ResearchGoal.getGoal(goalId);
    if(goalInstance==null){return;}
    if(goalInstance.tryStart(resourceInventory, -1))
      {
      ResearchTracker.instance().startResearch(worldObj, name, goalId);
      }
    else if(useAdjacentInventory)
      {
      TileEntity t;
      boolean started = false;
      int x = xCoord + inventoryDirection.offsetX;
      int y = yCoord + inventoryDirection.offsetY;
      int z = zCoord + inventoryDirection.offsetZ;
      int side = inventorySide.ordinal();
      
      if((t=worldObj.getTileEntity(x, y, z)) instanceof IInventory)
        {
        started = goalInstance.tryStart((IInventory)t, side);
        }
      if(started)
        {
        ResearchTracker.instance().startResearch(worldObj, name, goalId);
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
  if(storedEnergy>getMaxTorque(null)){storedEnergy = getMaxTorque(null);}
  }

@Override
public void addEnergyFromPlayer(EntityPlayer player)
  {
  storedEnergy+=AWCoreStatics.energyPerWorkUnit;
  if(storedEnergy>getMaxTorque(null)){storedEnergy=getMaxTorque(null);}
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

@Override
public ForgeDirection getPrimaryFacing()
  {
  return orientation;
  }

@Override
public void setPrimaryFacing(ForgeDirection face)
  {
  this.orientation = face;
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return 0;
  }

}
