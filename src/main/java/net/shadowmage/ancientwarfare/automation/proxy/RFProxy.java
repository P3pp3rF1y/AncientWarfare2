package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class RFProxy
{

public static RFProxy instance;

public static void loadInstance()
  {
  if(ModuleStatus.redstoneFluxEnabled)
    {
    try
      {
      instance = (RFProxy) Class.forName("net.shadowmage.ancientwarfare.automation.proxy.RFProxyActual").newInstance();
      AWLog.logDebug("Set rf proxy to rf-proxy actual");
      } 
    catch (Exception e)
      {    
      e.printStackTrace();
      instance = new RFProxy();
      }
    }
  else
    {
    instance = new RFProxy();
    }
  }

protected RFProxy()
  {
  // TODO Auto-generated constructor stub
  }

public boolean isRFTile(TileEntity te)
  {
  return false;
  }

public double transferPower(ITorqueTile generator, ForgeDirection from, TileEntity target)
  {
  return 0;
  }

}
