package net.shadowmage.ancientwarfare.vehicle.proxy;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.render.VehicleBBRender;
import cpw.mods.fml.client.config.DummyConfigElement.DummyCategoryElement;
import cpw.mods.fml.client.config.IConfigElement;
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
  InputHandler.instance().registerKeybind(InputHandler.KEY_VEHICLE_FORWARD, AWVehicleStatics.keybindForward.getInt(), null);//TODO add input callback
  InputHandler.instance().registerKeybind(InputHandler.KEY_VEHICLE_REVERSE, AWVehicleStatics.keybindReverse.getInt(), null);
  InputHandler.instance().registerKeybind(InputHandler.KEY_VEHICLE_LEFT, AWVehicleStatics.keybindLeft.getInt(), null);
  InputHandler.instance().registerKeybind(InputHandler.KEY_VEHICLE_RIGHT, AWVehicleStatics.keybindRight.getInt(), null);
  InputHandler.instance().registerKeybind(InputHandler.KEY_VEHICLE_FIRE, AWVehicleStatics.keybindFire.getInt(), null);  
  ConfigManager.registerConfigCategory(new VehicleCategory("awconfig.vehicle_keybinds", "awconfig.vehicle_keybinds"));
  }

@SuppressWarnings("rawtypes")
public static final class VehicleCategory extends DummyCategoryElement
{

@SuppressWarnings("unchecked")
public VehicleCategory(String name, String tooltipkey)
  {
  super(name, tooltipkey, getElementList());
  }

private static List<IConfigElement> getElementList()
  {
  ArrayList<IConfigElement> list = new ArrayList<IConfigElement>();
  list.add(new ConfigElement(AWVehicleStatics.keybindForward));  
  list.add(new ConfigElement(AWVehicleStatics.keybindReverse));  
  list.add(new ConfigElement(AWVehicleStatics.keybindLeft));  
  list.add(new ConfigElement(AWVehicleStatics.keybindRight));  
  list.add(new ConfigElement(AWVehicleStatics.keybindFire));  
  return list;
  }
}
}
