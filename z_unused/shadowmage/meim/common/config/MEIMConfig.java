/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public Licence.
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
package shadowmage.meim.common.config;

import java.io.File;
import java.util.logging.Logger;

import net.minecraftforge.common.Configuration;

public class MEIMConfig 
{
//*******************************************************FIELDS**********************************************//

public static final String VERSION = "1.0.001-beta-MC164";


/**
 * should debug features be enabled? (debug keybinds, debug overlay rendering, load and enable debug items)
 */
public static final boolean DEBUG = false;

private static Configuration config;
private static Logger logger;

private static String configDir;

//***************************************************SINGLETON************************************************//
private MEIMConfig(){};
private static MEIMConfig INSTANCE;
public static MEIMConfig instance()
 {
 if(INSTANCE==null)
   {
   INSTANCE = new MEIMConfig();
   }
 return INSTANCE;
 }

//**************************************************LOGGER*****************************************************//

public static void log(String info)
  {
  if(logger!=null)
    {
    logger.info(info);
    }  
  }

public static void logDebug(String info)
  {
  if(logger!=null && DEBUG)
    {    
    logger.info(String.valueOf("[DEBUG] "+info));        
    }
  }

public static void logError(String info)
  {
  //System.out.println("AWCORE SEVERE ERROR: "+info);
  if(logger!=null)
    {
    logger.severe(info);
    }
  }

public static void setLogger(Logger log)
  {
  logger = log;
  }

//**************************************************CONFIG*****************************************************//

public static void loadConfig(File inputFile)
  {  
  config = new Configuration(inputFile);
  configDir = inputFile.getParent();  
  createDirs();
  config.save();
  }

private static void createDirs()
  {
  File f = new File(getTexExportDir());
  if(!f.exists())
    {
    f.mkdirs();
    }  
  f = new File(getTexLoadDir());
  if(!f.exists())
    {
    f.mkdirs();
    }
  f = new File(getJavaExportDir());
  if(!f.exists())
    {
    f.mkdirs();
    }
  f = new File(getModelSaveDir());
  if(!f.exists())
    {
    f.mkdirs();
    }
  }

public static void saveConfig()
  {
  if(config!=null)
    {
    config.save();
    }
  }

public static String getTexExportDir()
  {
  return configDir +"\\MEIM\\UVExport";
  }

public static String getJavaExportDir()
  {
  return configDir +"\\MEIM\\JavaExport";
  }

public static String getModelSaveDir()
  {
  return configDir +"\\MEIM\\Models";
  }

public static String getTexLoadDir()
  {
  return configDir +"\\MEIM\\Texture";
  }

public static int getItemID(String name, int defaultID)
  {
  return config.getItem(name, defaultID).getInt(defaultID);
  }

public static int getItemID(String name, int defaultID, String comment)
  {
  return config.getItem(name, defaultID, comment).getInt(defaultID);
  }

public static int getBlockID(String name, int defaultID)
  {
  return config.getBlock(name, defaultID).getInt(defaultID);
  }

public static int getBlockID(String name, int defaultID, String comment)
  {
  return config.getBlock(name, defaultID, comment).getInt(defaultID);
  }


}
