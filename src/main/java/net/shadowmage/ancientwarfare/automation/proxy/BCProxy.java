package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public class BCProxy
{

public static BCProxy instance;

public static void loadInstance()
  {
  if(ModuleStatus.buildCraftLoaded)
    {
    try
      {
      instance = (BCProxy) Class.forName("net.shadowmage.ancientwarfare.automation.proxy.BCProxyActual").newInstance();
      AWLog.logDebug("set bc proxy to bc-proxy actual");
      } 
    catch (Exception e)
      {    
      e.printStackTrace();
      instance = new BCProxy();
      }
    }
  else
    {
    instance = new BCProxy();
    }
  }

public double transferPower(ITorqueTile generator, ForgeDirection from, TileEntity target)
  {
  return 0;
  }

public boolean isPowerTile(TileEntity te)
  {
  return false;
  }

public Object getBatteryObject(String kind, ITorqueTile tile, ForgeDirection dir)
  {
  return null;
  }

}
