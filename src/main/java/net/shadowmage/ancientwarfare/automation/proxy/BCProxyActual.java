package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import buildcraft.api.mj.IBatteryIOObject;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.IOMode;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.transport.PipeTransportPower;
import buildcraft.transport.TileGenericPipe;

public class BCProxyActual extends BCProxy
{

public static class TorqueMJBattery implements IBatteryIOObject
{
ITorqueTile tile;
IOMode mode;
ForgeDirection dir;
public TorqueMJBattery(ITorqueTile tile, IOMode mode, ForgeDirection dir)
  {
  this.tile = tile;
  this.mode = mode;
  this.dir = dir;
  }

@Override
public double getEnergyRequested()
  {
  return tile.getMaxTorque(null)-tile.getTorqueStored(null);
  }

@Override
public double addEnergy(double mj)
  {
  return addEnergy(mj, false);
  }

@Override
public double addEnergy(double mj, boolean ignoreCycleLimit)
  {
  /**
   * also, NOOP the ignoreCycleLimit -- theres fucking limits for a goddamn reason.
   * 
   * ALSO  FUCK THIS STUPID BATTERY BULLSHIT __ IT SHOULD NOT BE THIS HARD TO CREATE A GODDAMN BATTERY INTERFACE
   * WHY THE FUCK DOES BC NOT DO PROPER SIDED ENERGY INPUT/OUTPUT HANDLING?
   */
  return tile.addTorque(dir, mj);
  }

@Override
public double getEnergyStored()
  {
  return tile.getTorqueStored(null);
  }

@Override
public void setEnergyStored(double mj)
  {
  tile.setTorqueEnergy(mj);
  }

@Override
public double maxCapacity()
  {
  return tile.getMaxTorque(null);
  }

@Override
public double minimumConsumption()
  {  
  return 0;
  }

@Override
public double maxReceivedPerCycle()
  {
  return tile.getMaxTorqueInput(null);
  }

@Override
public IBatteryObject reconfigure(double maxCapacity, double maxReceivedPerCycle, double minimumConsumption)
  {
  //NOOP -- can't fucking reconfigure my batteries @ runtime...F-YOU
  return this;
  }

@Override
public String kind()
  {
  return MjAPI.DEFAULT_POWER_FRAMEWORK;
  }

@Override
public IOMode mode()
  {
  return mode;
  }

@Override
public boolean canSend()
  {
  return mode.canSend;
  }

@Override
public boolean canReceive()
  {
  return mode.canReceive;
  }
}

@Override
public IBatteryObject getBatteryObject(String kind, ITorqueTile tile, ForgeDirection dir)
  {
  boolean send = false, recieve = false;
  recieve = tile.canInputTorque(dir);
  send = tile.canOutputTorque(dir);
  IOMode mode = send && recieve ? IOMode.Both : send ? IOMode.Send : recieve ? IOMode.Receive : IOMode.None;
  return new TorqueMJBattery(tile, mode, dir);
  }

@Override
public boolean isPowerPipe(World world, TileEntity te)
  {
  if(te==null){return false;}
  if(te instanceof TileGenericPipe)
    {
    TileGenericPipe tgp = (TileGenericPipe)te;
    if(tgp.pipe!=null && tgp.pipe.transport instanceof PipeTransportPower)
      {
      return true;
      }
    }
  return false;
  }

@Override
public void transferPower(World world, int x, int y, int z, ITorqueTile generator)
  {
  if(generator.getMaxTorqueOutput(null)!=0){return;}
  if(!(generator instanceof IPowerEmitter)){return;}
  world.theProfiler.startSection("AW-BC-PowerUpdate");
  double[] requestedEnergy = new double[6];
  
  IBatteryObject[] targets = new IBatteryObject[6];
  TileEntity[] tes = generator.getNeighbors();
  TileEntity te;
  
  IBatteryObject target;
  
  double maxOutput = generator.getMaxTorqueOutput(null);
  if(maxOutput>generator.getTorqueStored(null)){maxOutput = generator.getTorqueStored(null);}
  if(maxOutput<1)
    {
    world.theProfiler.endSection();
    return;
    }  
  double request;
  double totalRequest = 0;
  
  ForgeDirection d;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    if(!generator.canOutputTorque(d)){continue;}
    te = tes[i];//world.getTileEntity(x+d.offsetX, y+d.offsetY, z+d.offsetZ);
    if(te instanceof ITorqueTile){continue;}//skip torque tiles, transfer is handled in torque tile power update
    target = MjAPI.getMjBattery(te);
    if(target==null){continue;}
    targets[d.ordinal()]=target;  
    request = target.maxReceivedPerCycle();
    if(request +target.getEnergyStored() > target.maxCapacity()){request = target.maxCapacity()-target.getEnergyStored();}
    if(request>0)
      {
      requestedEnergy[d.ordinal()]=request;
      totalRequest += request;          
      } 
    }
  if(totalRequest>0)
    {
    double percentFullfilled = maxOutput / totalRequest;  
    for(int i = 0; i<6; i++)
      {
      if(targets[i]==null){continue;}
      target = targets[i];
      request = requestedEnergy[i];
      request *= percentFullfilled;
      request = target.addEnergy(request);
      generator.setTorqueEnergy(generator.getTorqueStored(null)-request);  
      }
    }
  world.theProfiler.endSection();
  }

}
