package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;

public class BCProxy
{

public static BCProxy instance;

public static void loadInstance()
  {
  if(ModuleStatus.buildCraftLoaded)
    {
    try
      {
      instance = (BCProxy) BCProxy.class.forName("net.shadowmage.ancientwarfare.automation.proxy.BCProxyActual").newInstance();
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

public void transferPower(World world, int x, int y, int z, ITorqueGenerator generator)
  {
  
  }

public boolean isPowerPipe(World world, TileEntity te)
  {
  return false;
  }

}
