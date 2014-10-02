package net.shadowmage.ancientwarfare.automation.proxy;

import cofh.api.energy.IEnergyConnection;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class RFProxyActual extends RFProxy
{

protected RFProxyActual()
  {
  
  }

@Override
public boolean isRFTile(TileEntity te)
  {
  return te instanceof IEnergyConnection;
  }

@Override
public double transferPower(ITorqueTile generator, ForgeDirection from, TileEntity target)
  {
  IEnergyConnection iec = (IEnergyConnection)target;
  if(iec instanceof IEnergyHandler)
    {
    IEnergyHandler h = (IEnergyHandler)iec;
    return generator.drainTorque(from, (double)(h.receiveEnergy(from.getOpposite(), (int) (generator.getMaxTorqueOutput(from)*AWAutomationStatics.torqueToRf), false)*AWAutomationStatics.rfToTorque));
    }
  return 0;
  }

}
