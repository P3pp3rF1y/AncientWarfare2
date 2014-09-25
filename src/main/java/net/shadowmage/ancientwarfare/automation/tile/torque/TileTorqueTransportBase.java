package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.ISidedBatteryProvider;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value=
  {
  @Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true),
  @Optional.Interface(iface="buildcraft.api.mj.ISidedBatteryProvider",modid="BuildCraft|Core",striprefs=true)
  })
public abstract class TileTorqueTransportBase extends TileTorqueBase implements IPowerEmitter, ISidedBatteryProvider
{

@Optional.Method(modid="BuildCraft|Core")
@Override
public boolean canEmitPowerFrom(ForgeDirection side)
  {
  return canOutput(side);
  }

@Optional.Method(modid="BuildCraft|Core")
@Override
public IBatteryObject getMjBattery(String kind, ForgeDirection direction)
  {  
  return (IBatteryObject) BCProxy.instance.getBatteryObject(kind, this, direction);
  }

@Override
protected boolean buildConnection(ForgeDirection d, TileEntity te)
  {
  if(te==null){return false;}
  if(BCProxy.instance.isPowerPipe(worldObj, te))//always connect to BC pipes, who knows what direction the power is flowing....
    {
    return true;      
    }
  else if(canOutput(d))
    {
    if(te instanceof ITorqueTile)
      {
      ITorqueTile rec = (ITorqueTile)te;
      if(rec.canInput(d.getOpposite()))
        {
        return true;
        }         
      }
    }
  else if(canInput(d))
    {
    if(te instanceof ITorqueTile)
      {
      ITorqueTile gen = (ITorqueTile)te;
      if(gen.canOutput(d.getOpposite()))
        {
        return true;        
        }
      }
    } 
  return false;
  }

@Override
public boolean cascadedInput()
  {
  return false;
  }

}
