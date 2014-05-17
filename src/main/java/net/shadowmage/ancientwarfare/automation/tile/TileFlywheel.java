package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueStorage;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import buildcraft.api.mj.IOMode;
import buildcraft.api.mj.MjBattery;
import buildcraft.energy.TileEngine.EnergyStage;


public class TileFlywheel extends TileEntity implements IWorkSite, IOwnable, ITorqueStorage
{
public static final double maxEnergyStored = 10000;
public static final double maxInputPerTick = 100;
public static final double maxOutputPerTick = 100;

private int maxWorkers = 2;
protected String owningPlayer;
private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());
@MjBattery(maxCapacity=maxEnergyStored,mode=IOMode.Both)
public double storedEnergy;

private double inputTick;

private List<TileFlywheel> wheelsToBalance = new ArrayList<TileFlywheel>();

//@Method(modid = "")
@Override
public void updateEntity()
  {
  inputTick = 0;
  if(!worldObj.isRemote)
    {
    ForgeDirection d = ForgeDirection.getOrientation(getBlockMetadata());
    TileEntity te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    if(te instanceof ITorqueReceiver)
      {  
      ITorque.transferPower(this, d, (ITorqueReceiver) te);  
      }
    else if(te instanceof TileMechanicalWorker && !workers.contains(te))
      {
      TileMechanicalWorker tem = (TileMechanicalWorker)te;
      if(tem.storedEnergy < TileMechanicalWorker.maxEnergyStored)
        {
        double d1 = TileMechanicalWorker.maxEnergyStored-tem.storedEnergy;
        if(d1 >= (AWAutomationStatics.energyPerWorkUnit/20) )
          {
          d1 = (AWAutomationStatics.energyPerWorkUnit/20);
          }
        if(d1>storedEnergy){d1=storedEnergy;}
        if(d1>0)
          {
          storedEnergy-=d1;
          tem.storedEnergy+=d1;
          AWLog.logDebug("found mechanical worker...injecting power..."+storedEnergy);
          }
        }
      }
    tryBalancingFlywheels();    
    }
  }

private void tryBalancingFlywheels()
  {
  TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
  if((te instanceof TileFlywheel))
    {
    return;
    }
  int y = yCoord-1;
  te = worldObj.getTileEntity(xCoord, y, zCoord);
  while(te instanceof TileFlywheel)
    {
    wheelsToBalance.add((TileFlywheel) te);
    y--;
    te = worldObj.getTileEntity(xCoord, y, zCoord);
    }
  wheelsToBalance.add(this);
  double totalPower = 0.d;
  double average;
  for(TileFlywheel wheel : wheelsToBalance)
    {
    totalPower+=wheel.getEnergyStored();
    }
  average = totalPower / (double)wheelsToBalance.size();
  for(TileFlywheel wheel : wheelsToBalance)
    {
    wheel.setEnergy(average);
    }
  wheelsToBalance.clear();
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit;
  AWLog.logDebug("doing work...new energy: "+storedEnergy);
  }

@Override
public boolean hasWork()
  {
  return storedEnergy<10000.d;
  }

@Override
public void doWork(IWorker worker)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit;
  AWLog.logDebug("doing work...new energy: "+storedEnergy);
  }

@Override
public boolean addWorker(IWorker worker)
  {
  if((workers.contains(worker) || workers.size()<maxWorkers) && worker.getWorkTypes().contains(getWorkType()))
    {
    workers.add(worker);
    return true;   
    }
  return false;
  }

@Override
public void removeWorker(IWorker worker)
  {
  workers.remove(worker);
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CONSTRUCTION;//TODO make a 'generic' work type
  }

@Override
public Team getTeam()
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
public List<BlockPosition> getWorkTargets()
  {
  return Collections.emptyList();
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

@Override
public void setOwnerName(String name)
  {
  this.owningPlayer = name;
  }

@Override
public String getOwnerName()
  {
  return owningPlayer;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxOutput(ForgeDirection side)
  {
  return maxOutputPerTick;
  }

@Override
public double addEnergy(double energy)
  {  
  ForgeDirection face = ForgeDirection.getOrientation(getBlockMetadata());
  double d = getMaxEnergy()-getEnergyStored();
  if(d>energy)
    {
    d=energy;
    }
  if(d>maxInputPerTick-inputTick)
    {
    d=maxInputPerTick-inputTick;
    }
  storedEnergy+=d;
  inputTick+=d;
  return d;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

@Override
public double getMaxInput(ForgeDirection side)
  {  
  return maxInputPerTick;
  }

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public EnumSet<ForgeDirection> getInputDirections()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()).getOpposite());
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()));
  }

@Override
public String toString()
  {
  return "Flywheel Energy Storage["+storedEnergy+"]";
  }

}
