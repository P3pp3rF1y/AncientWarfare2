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
package shadowmage.ancient_framework;

import java.io.File;
import java.util.logging.Logger;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.ModConfiguration;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;


public abstract class AWMod
{

public ModConfiguration config;

public abstract void loadConfiguration(File config, Logger log);

@EventHandler
public abstract void preInit(FMLPreInitializationEvent evt);

@EventHandler
public abstract void init(FMLInitializationEvent evt);

@EventHandler
public abstract void postInit(FMLPostInitializationEvent evt);

@EventHandler
public abstract void serverPreStart(FMLServerAboutToStartEvent evt);

@EventHandler
public abstract void serverStarting(FMLServerStartingEvent evt);

@EventHandler
public abstract void serverStarted(FMLServerStartedEvent evt);

@EventHandler
public abstract void serverStopping(FMLServerStoppingEvent evt);

@EventHandler
public abstract void serverStopped(FMLServerStoppedEvent evt);

public void logError(String info)
  {
  AWLog.logError(info);
  }

public void log(String info)
  {
  AWLog.log(info);
  }

public void logDebug(String info)
  {
  AWLog.logDebug(info);
  }

}
