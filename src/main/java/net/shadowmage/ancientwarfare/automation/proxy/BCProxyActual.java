package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import buildcraft.api.mj.BatteryObject;
import buildcraft.api.mj.IBatteryIOObject;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.IOMode;
import buildcraft.api.mj.MjAPI;
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
  return tile.getMaxTorque(dir)-tile.getTorqueStored(dir);
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
  return tile.getTorqueStored(dir);
  }

@Override
public void setEnergyStored(double mj)
  {
  tile.addTorque(dir, mj);
  }

@Override
public double maxCapacity()
  {
  return tile.getMaxTorque(dir);
  }

@Override
public double minimumConsumption()
  {  
  return 0;
  }

@Override
public double maxReceivedPerCycle()
  {
  return tile.getMaxTorqueInput(dir);
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
  return tile.canOutputTorque(dir);
  }

@Override
public boolean canReceive()
  {
  return tile.canInputTorque(dir);
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
public boolean isPowerTile(TileEntity te)
  {
  if(te==null){return false;}
  if(te instanceof TileGenericPipe)
    {
    TileGenericPipe tgp = (TileGenericPipe)te;
    if(tgp.pipe!=null && tgp.pipe.transport instanceof PipeTransportPower)
      {
      AWLog.logDebug("returning is pipe==true");
      return true;
      }
    }
  IBatteryObject bat = MjAPI.getMjBattery(te);
  return bat!=null;
  }

@Override
public double transferPower(ITorqueTile generator, ForgeDirection to, TileEntity target)
  {
  if(target!=null)
    {
    AWLog.logDebug("processing BC power output towards: "+to);
    IBatteryObject ibo = MjAPI.getMjBattery(target, MjAPI.DEFAULT_POWER_FRAMEWORK, to.getOpposite());
    if(ibo==null){return 0;}
    double trans = generator.drainTorque(to, ibo.addEnergy(generator.getMaxTorqueOutput(to)));    
    AWLog.logDebug("pushed energy: "+trans +" new e: "+ibo.getEnergyStored());
    }
  return 0;
  }

}
