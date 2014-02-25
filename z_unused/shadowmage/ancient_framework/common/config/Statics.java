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

public class Statics
{

public static final String CONFIG_PATH = "config/AWConfig";//cannot have beginning / (probably because of external file)
public static final String ASSETS_PATH = "/assets/ancientwarfare";//needs beginning / (probably because of internal file)
public static final String TEXTURE_PATH = "textures/";
public static final String MOD_PREFIX = "ancientwarfare";
public static final String VERSION="@VERSION@";
public static final boolean DEBUG = true;

public static final int guiOptions = 0;
public static final int guiTeamControl = 1;
public static final int guiResearch = 2;
public static final int guiCrafting = 3;
public static final int guiInfo = 4;

public static final int guiStructureBuilderCreative = 10;
public static final int guiStructureScannerCreative = 11;
public static final int guiSpawnerPlacer = 12;

public static final int guiVehicleInventory = 20;

public static final int guiNpcInventory = 30;

public static final int guiCivicInventory = 40;

}
