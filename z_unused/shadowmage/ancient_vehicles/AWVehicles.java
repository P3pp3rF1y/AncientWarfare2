/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_vehicles;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import net.minecraftforge.common.MinecraftForge;
import shadowmage.ancient_framework.AWFramework;
import shadowmage.ancient_framework.AWMod;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.proxy.CommonProxy;
import shadowmage.ancient_vehicles.client.render.RenderTest;
import shadowmage.ancient_vehicles.common.config.AWVehicleStatics;
import shadowmage.ancient_vehicles.common.item.AWVehiclesItemLoader;
import shadowmage.ancient_vehicles.common.vehicle.VehicleRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod( modid = "AncientVehicles", name="Ancient Vehicles", version=Statics.VERSION, dependencies="required-after:AncientWarfareCore")
@NetworkMod
(
clientSideRequired = true,
serverSideRequired = true,
versionBounds="["+Statics.VERSION+",)"
)
public class AWVehicles extends AWMod
{

@SidedProxy(clientSide = "shadowmage.ancient_vehicles.client.proxy.ClientProxyVehicle", serverSide = "shadowmage.ancient_framework.common.proxy.CommonProxy")
public static CommonProxy proxy;
@Instance("AncientVehicles")
public static AWVehicles instance;  

@Override
public void loadConfiguration(File config, Logger log)
  {
  this.config = new AWVehicleStatics(config, log, Statics.VERSION);
  }

@Override
@EventHandler
public void preInit(FMLPreInitializationEvent evt)
  {
  this.loadConfiguration(evt.getSuggestedConfigurationFile(), evt.getModLog());
  this.config.log("Ancient Warfare Vehicles Pre-Init started.");
  AWFramework.loadedVehicles = true;
  VehicleRegistry.loadVehicles();
  AWVehiclesItemLoader.instance().registerItems();
  VehicleRegistry.registerVehicleItemData(AWVehiclesItemLoader.vehicleSpawner);
  try
    {
    MinecraftForge.EVENT_BUS.register(new RenderTest());
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  this.config.log("Ancient Warfare Vehicles Pre-Init finished.");
  }

@Override
@EventHandler
public void init(FMLInitializationEvent evt)
  {
  this.config.log("Ancient Warfare Vehicles Init started.");
  this.config.log("Ancient Warfare Vehicles Init finished.");
  }

@Override
@EventHandler
public void postInit(FMLPostInitializationEvent evt)
  {
  this.config.log("Ancient Warfare Vehicles Post-Init started.");
  

  config.saveConfig();
  this.config.log("Ancient Warfare Vehicles Post-Init finished.");
  }

@Override
@EventHandler
public void serverPreStart(FMLServerAboutToStartEvent evt){}

@Override
@EventHandler
public void serverStarting(FMLServerStartingEvent evt){}

@Override
@EventHandler
public void serverStarted(FMLServerStartedEvent evt){}

@Override
@EventHandler
public void serverStopping(FMLServerStoppingEvent evt){}

@Override
@EventHandler
public void serverStopped(FMLServerStoppedEvent evt){}

}
