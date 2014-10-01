package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.ISidedBatteryProvider;
import buildcraft.api.power.IPowerEmitter;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value=
  {
  @Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true),
  @Optional.Interface(iface="buildcraft.api.mj.ISidedBatteryProvider",modid="BuildCraft|Core",striprefs=true),
  @Optional.Interface(iface="cofh.api.energy.IEnergyHandler", modid="CoFHCore",striprefs=true)
  })
public abstract class TileTorqueTransportBase extends TileTorqueBase implements IPowerEmitter, ISidedBatteryProvider, IEnergyHandler
{

@Optional.Method(modid="BuildCraft|Core")
@Override
public boolean canEmitPowerFrom(ForgeDirection side)
  {
  return canOutputTorque(side);
  }

@Optional.Method(modid="BuildCraft|Core")
@Override
public IBatteryObject getMjBattery(String kind, ForgeDirection direction)
  {  
  return (IBatteryObject) BCProxy.instance.getBatteryObject(kind, this, direction);
  }

@Optional.Method(modid="CoFHCore")
@Override
public int getEnergyStored(ForgeDirection from)
  {
  return (int) storedEnergy*10;
  }

@Optional.Method(modid="CoFHCore")
@Override
public int getMaxEnergyStored(ForgeDirection from)
  {
  return (int) maxEnergy*10;
  }

@Optional.Method(modid="CoFHCore")
@Override
public boolean canConnectEnergy(ForgeDirection from)
  {
  return canOutputTorque(from) || canInputTorque(from);//TODO verify what this expects
  }

@Optional.Method(modid="CoFHCore")
@Override
public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
  {
  if(!canOutputTorque(from)){return 0;}  
  double d = getTorqueStored(null);
  double d1 = getMaxTorqueOutput(null);
  double d2 = Math.min(d1, d);  
  int d3 = (int)(d2*10.d);  
  maxExtract = Math.min(d3, maxExtract);    
  if(!simulate)
    {
    d2 = (double)maxExtract / 10.d;
    setTorqueEnergy(d-d2);
    }
  return maxExtract;
  }

@Optional.Method(modid="CoFHCore")
@Override
public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
  {
  if(!canInputTorque(from)){return 0;}
  double d = getTorqueStored(null);
  double d1 = getMaxTorqueOutput(null);
  double d2 = Math.min(d, d1);
  int d3 = (int)(d2*10.d);
  maxReceive = Math.min(d3, maxReceive);
  if(!simulate)
    {
    d2 = (double)maxReceive / 10.d;  
    }  
  return maxReceive;
  }

@Override
protected boolean buildConnection(ForgeDirection side, TileEntity te)
  {
  if(te==null){return false;}
  if(canOutputTorque(side))
    {
    if(BCProxy.instance.isPowerPipe(worldObj, te))//always connect to BC pipes, who knows what direction the power is flowing....
      {
      return true;      
      }
    if(te instanceof ITorqueTile)
      {
      ITorqueTile rec = (ITorqueTile)te;
      if(rec.canInputTorque(side.getOpposite()))
        {
        return true;
        }         
      }
    }
  else if(canInputTorque(side))
    {
    if(BCProxy.instance.isPowerPipe(worldObj, te))//always connect to BC pipes, who knows what direction the power is flowing....
      {
      return true;      
      }
    if(te instanceof ITorqueTile)
      {
      ITorqueTile gen = (ITorqueTile)te;
      if(gen.canOutputTorque(side.getOpposite()))
        {
        return true;        
        }
      }
    } 
  return false;
  }

@Override
public boolean useClientRotation()
  {
  return true;
  }

@Override
public boolean cascadedInput()
  {
  return true;
  }

}
