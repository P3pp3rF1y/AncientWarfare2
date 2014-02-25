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
package shadowmage.ancient_framework.common.config;

import java.io.File;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;

/**
 * static-data configuration class.
 * Each mod will need to construct its own subclass of this, adding static fields for necessary config items
 * @author Shadowmage
 *
 */
public abstract class ModConfiguration
{

public Configuration config;
public Logger logger;
private boolean debug = false;
public static boolean updatedVersion = false;
public static boolean autoExportOnUpdate = false;
public static boolean shouldExport = false;

public ModConfiguration(File configFile, Logger log, String version)
  {
  this.setConfig(configFile);
  this.setLogger(log);
  this.initializeCategories();
  this.initializeValues();
  }

public abstract void initializeCategories();
public abstract void initializeValues();

private void setConfig(File configFile)
  {
  this.config = new Configuration(configFile);
  }

private void setLogger(Logger log)
  {
  logger = log;
  }

public void setDebug(boolean value)
  {
  this.debug = value;
  }

public Configuration getConfig()
  {
  return this.config;
  }

public void log(String info)
  {
  logger.info(info);
  }

public void logDebug(String info)
  {
  if(debug)
    {    
    logger.info(String.valueOf("[DEBUG] "+info));      
    }
  }

public void logError(String info)
  {  
  logger.severe(info);   
  }

public void saveConfig()
  {
  this.config.save();
  }

public int getItemID(String name, int defaultID)
  {
  return getItemID(name, defaultID, "");
  }

public int getItemID(String name, int defaultID, String comment)
  {
  return config.getItem(name, defaultID, comment).getInt(defaultID);
  }

public int getBlockID(String name, int defaultID)
  {
  return getBlockID(name, defaultID, "");
  }

public int getBlockID(String name, int defaultID, String comment)
  {
  return config.getBlock(name, defaultID, comment).getInt(defaultID);
  }

public int getKeyBindID(String name, int defaultID, String comment)
  {
  return config.get("keybinds", name, defaultID, comment).getInt(defaultID);
  }

public boolean updatedVersion()
  {
  return updatedVersion;
  }

public boolean autoExportOnUpdate()
  {
  return this.autoExportOnUpdate();
  }

}
