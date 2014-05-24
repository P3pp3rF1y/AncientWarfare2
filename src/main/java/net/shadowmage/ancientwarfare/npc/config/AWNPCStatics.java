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

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWNPCStatics extends ModConfiguration
{

/**
 * shared settings:
 * NONE?
 */
public static final String sharedSettings = "01_shared_settings";

/**
 * server settings:
 * npc worker tick rate / ticks per work unit
 */
public static final String serverSettinngs = "02_server_settings";

/**
 * client settings:
 * --SET VIA PROXY / ClientOptions.INSTANCE
 */
public static final String clientSettings = "03_client_settings";

/**
 * what food items are edible, and the amount of food value an NPC will get from eating them
 */
public static final String foodSettings = "04_food_settings";
private HashMap<String, Integer> foodValues = new HashMap<String, Integer>();

/**
 * base aggro / target settings for combat NPCs.  Can be further
 * customized on a per-npc basis via config GUI.
 */
public static final String targetSettings = "05_target_settings";

/**
 * enable/disable specific recipes
 * enable/disable research for specific recipes
 */
public static final String recipeSettings = "06_recipe_settings";



/**
 * how often an NPC should 'tick' the worksite and add energy
 * TODO add to config file
 */
public static int npcWorkTicks = 50;

public static int foodMultiplier = 750;//ticks per food value

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
  loadFoodValues();
  this.config.save();
  }

private void loadFoodValues()
  {
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.apple), 4);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.mushroom_stew), 6);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.bread), 5);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.carrot), 4);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.potato), 1);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.baked_potato), 6);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.beef), 3);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_beef), 8);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.chicken), 2);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_chicken), 6);
//  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.fish), 6);//TODO what to do about fish / item subtypes?
//  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_fished), 6);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.porkchop), 3);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cooked_porkchop), 8);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.cookie), 2);
  config.get(foodSettings, Item.itemRegistry.getNameForObject(Items.pumpkin_pie), 8);
  
  
  ConfigCategory category = config.getCategory(foodSettings);
  
  String name;
  int value;
  for(Entry<String, Property> entry : category.entrySet())
    {
    name = entry.getKey();
    value = entry.getValue().getInt(0);
    foodValues.put(name, value);
    }
  }

/**
 * returns the food value for a single size stack of the input item stack
 * @param stack
 * @return
 */
public int getFoodValue(ItemStack stack)
  {
  if(stack==null || stack.getItem()==null){return 0;}
  int food = 0;
  String name = Item.itemRegistry.getNameForObject(stack.getItem());
  if(foodValues.containsKey(name))
    {
    food = foodValues.get(name);    
    }  
  return food*foodMultiplier;
  }

}
