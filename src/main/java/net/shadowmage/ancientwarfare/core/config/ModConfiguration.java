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
package net.shadowmage.ancientwarfare.core.config;

import java.io.File;
import java.util.logging.Logger;

import net.minecraftforge.common.config.Configuration;

/**
 * static-data configuration class.
 * Each mod will need to construct its own subclass of this, adding static fields for necessary config items
 * @author Shadowmage
 *
 */
public abstract class ModConfiguration
{

public Configuration config;
private boolean debug = false;
public static boolean updatedVersion = false;
public static boolean autoExportOnUpdate = false;
public static boolean shouldExport = false;

public ModConfiguration(Configuration config)
  {
  this.config = config;
  }

public void load()
  {
  initializeCategories();
  initializeValues();
  }

protected abstract void initializeCategories();
protected abstract void initializeValues();

public void setDebug(boolean value)
  {
  this.debug = value;
  }

public Configuration getConfig()
  {
  return this.config;
  }

public void saveConfig()
  {
  this.config.save();
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
