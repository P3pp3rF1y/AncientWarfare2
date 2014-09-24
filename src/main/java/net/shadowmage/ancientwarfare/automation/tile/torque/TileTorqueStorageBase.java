package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.ISidedBatteryProvider;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value=
  {
  @Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true),
  @Optional.Interface(iface="buildcraft.api.mj.ISidedBatteryProvider",modid="BuildCraft|Core",striprefs=true)
  })
public abstract class TileTorqueStorageBase extends TileTorqueBase implements IPowerEmitter, ISidedBatteryProvider
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

}
