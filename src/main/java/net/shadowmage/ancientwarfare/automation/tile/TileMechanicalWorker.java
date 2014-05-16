package net.shadowmage.ancientwarfare.automation.tile;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.common.Optional;

@Optional.Interface(iface="buildcraft.api.transport.IPipeConnection",modid="BuildCraft|Core",striprefs=true)
public class TileMechanicalWorker extends TileEntity implements IWorker, IInventory, IPipeConnection, IInteractableTile, IOwnable
{

private static final double maxEnergyStored = 1500.d;
private static final double maxReceivedPerTick = 100.d;
private static final double idleConsumption = 0.d;

/**
 * Used to store MJ energy from BC integration.<br>
 * Also used to store energy from direct coal burning when mechanical-worker fuel use is enabled.<br>
 */
@MjBattery(
    maxCapacity=maxEnergyStored,
    maxReceivedPerCycle=maxReceivedPerTick,
    minimumConsumption=idleConsumption
    )
double storedEnergy;

//TODO swap this to a block-position with an accessor method
WeakReference<IWorkSite> workSite = new WeakReference<IWorkSite>(null);

private int searchDelay;
private int burnTicks;
private int originalBurnTicks;

String ownerName;//TODO load/save

InventoryBasic fuelInventory = new InventoryBasic(1)
  {
  public boolean isItemValidForSlot(int var1, net.minecraft.item.ItemStack var2) 
    {
    return TileEntityFurnace.getItemBurnTime(var2)>0;
    }
  };

public TileMechanicalWorker()
  {
  
  }

public void onBlockBroken()
  {
  if(this.workSite!=null && this.workSite.get()!=null)
    {
    this.workSite.get().removeWorker(this);
    }
  }

private void setWorkSite(IWorkSite site)
  {
  workSite = new WeakReference<IWorkSite>(site);
  if(site==null)
    {
    searchDelay = 40;
    }
  }

public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public IWorkSite getWorkSite()
  {
  return workSite==null ? null : workSite.get();
  }

@Override
public boolean canUpdate()
  {  
  return true;
  }

@Override
public void updateEntity()
  {  
  if(worldObj.isRemote)
    {
    return;
    }
  if(searchDelay>0)
    {
    searchDelay--;
    }  
  if(searchDelay==0 && getWorkSite()==null)
    {
    if(findWorkSite())
      {
      searchDelay = -1;
      }
    else
      {
      searchDelay = 40;
      }
    }
  if(AWAutomationStatics.enableMechanicalWorkerFuelUse)
    {
    if(burnTicks<=0 && storedEnergy < maxEnergyStored)
      {
      if(fuelInventory.getStackInSlot(0)!=null)
        {
        //if fuel, consume one, set burn-ticks to fuel value
        int ticks = TileEntityFurnace.getItemBurnTime(fuelInventory.getStackInSlot(0));
        if(ticks>0)
          {
          fuelInventory.decrStackSize(0, 1);
          burnTicks = ticks;
          originalBurnTicks = ticks;
          }
        }
      }
    else if(burnTicks>0)
      {
      storedEnergy++;
      burnTicks--;
      }    
    }
  if(storedEnergy >= AWAutomationStatics.energyPerWorkUnit && getWorkSite()!=null)
    {
    attemptWork();
    }
  else
    {
//    AWLog.logDebug("energy: "+storedEnergy);
    }
  }

private boolean findWorkSite()
  {
  ForgeDirection face = ForgeDirection.getOrientation(getBlockMetadata());
  
  AWLog.logDebug("attempting to find worksite... at: "+face);
  TileEntity te;
  IWorkSite site;
  te = worldObj.getTileEntity(xCoord+face.offsetX, yCoord+face.offsetY, zCoord+face.offsetZ);
  if(te instanceof IWorkSite)
    {
    AWLog.logDebug("found potential site, attempting to add as worker..");
    site = (IWorkSite)te;
    if(site.addWorker(this))
      {
      AWLog.logDebug("set worksite to: "+site + " at: "+face);
      this.setWorkSite(site);
      return true;
      }
    }  
  return false;
  }

@Override
public float getWorkEffectiveness()
  {
  return 1.f;
  }

@Override
public Team getTeam()
  {
  //TODO set team to that of owning player/placing player
  return null;
  }

@Override
public EnumSet<WorkType> getWorkTypes()
  {
  return EnumSet.allOf(WorkType.class);
  }

@Override
public BlockPosition getPosition()
  {
  return new BlockPosition(xCoord,yCoord,zCoord);
  }

@Override
public void clearWorkSite()
  {
  this.setWorkSite(null);
  }  

protected void attemptWork()
  {  
  AWLog.logDebug("Mechanical Worker has worksite, attempting work.. stored energy: "+storedEnergy);
  if(getWorkSite().hasWork())
    {
    AWLog.logDebug("Mechanical Worker had energy and worksite had work, processing work");
    storedEnergy -= AWAutomationStatics.energyPerWorkUnit;
    getWorkSite().doWork(this);      
    }       
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  storedEnergy = tag.getDouble("storedEnergy");
  burnTicks = tag.getInteger("burnTicks");
  originalBurnTicks = tag.getInteger("burnTicksBase");
  if(tag.hasKey("inventory"))
    {
    fuelInventory.readFromNBT(tag.getCompoundTag("inventory"));
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  tag.setInteger("burnTicks", burnTicks);
  tag.setInteger("burnTicksBase", originalBurnTicks);
  tag.setTag("inventory", fuelInventory.writeToNBT(new NBTTagCompound()));
  }

//TODO is this actually needed since the MjBattery annotation works now?
@Optional.Method(modid="BuildCraft|Core")
@Override
public ConnectOverride overridePipeConnection(PipeType arg0, ForgeDirection arg1)
  {
  return arg0==PipeType.POWER ? ConnectOverride.CONNECT : ConnectOverride.DEFAULT;
  }

@Override
public int getSizeInventory()
  {
  return fuelInventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return fuelInventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return fuelInventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return fuelInventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  fuelInventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return fuelInventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return fuelInventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return fuelInventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return fuelInventory.isUseableByPlayer(var1);
  }

@Override
public void openInventory()
  {
  fuelInventory.openInventory();
  }

@Override
public void closeInventory()
  {
  fuelInventory.closeInventory();
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  return fuelInventory.isItemValidForSlot(var1, var2);
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_MECHANICAL_WORKER, xCoord, yCoord, zCoord);
    }
  return false;
  }

public final void setOwnerName(String name)
  {
  this.ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

public int getBurnTime()
  {
  return burnTicks;
  }

public int getBurnTimeBase()
  {
  return originalBurnTicks;
  }

public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

}
