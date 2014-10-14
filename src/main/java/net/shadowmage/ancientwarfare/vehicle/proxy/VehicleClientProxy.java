package net.shadowmage.ancientwarfare.vehicle.proxy;

import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.render.VehicleBBRender;
import cpw.mods.fml.client.registry.RenderingRegistry;


public class VehicleClientProxy extends VehicleCommonProxy
{

@Override
public void registerClient()
  {
  RenderingRegistry.registerEntityRenderingHandler(VehicleBase.class, new VehicleBBRender());
  registerClientOptions();
  }

public void registerClientOptions()
  {
  
  }

}
