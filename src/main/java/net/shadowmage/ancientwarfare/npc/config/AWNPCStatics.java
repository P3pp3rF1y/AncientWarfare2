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
package net.shadowmage.ancientwarfare.npc.config;

import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWNPCStatics extends ModConfiguration
{

/**
 * shared settings:
 * NONE?
 */
public static final String sharedSettings = "1_shared_settings";

/**
 * server settings:
 * npc worker tick rate / ticks per work unit
 */
public static final String serverSettinngs = "2_server_settings";

/**
 * client settings:
 * --SET VIA PROXY / ClientOptions.INSTANCE
 */
public static final String clientSettings = "3_client_settings";

/**
 * what food items are edible, and the amount of food value an NPC will get from eating them
 */
public static final String foodSettings = "3_food_settings";

/**
 * base aggro / target settings for combat NPCs.  Can be further
 * customized on a per-npc basis via config GUI.
 */
public static final String targetSettings = "4_target_settings";

/**
 * enable/disable specific recipes
 * enable/disable research for specific recipes
 */
public static final String recipeSettings = "5_recipe_settings";



/**
 * how often an NPC should 'tick' the worksite and add energy
 * TODO add to config file
 */
public static int npcWorkTicks = 50;



public AWNPCStatics(Configuration config)
  {
  super(config);
  }


@Override
public void initializeCategories()
  {
 
  }

@Override
public void initializeValues()
  { 
  this.config.save();
  }

}
