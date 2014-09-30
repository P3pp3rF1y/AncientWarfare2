package net.shadowmage.ancientwarfare.automation.proxy;

import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;

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

private RFProxy()
  {
  // TODO Auto-generated constructor stub
  }

}
