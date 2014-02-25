/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package shadowmage.ancient_framework.common.registry;

import java.util.Random;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public class LootGenerator
{

private static LootGenerator INSTANCE;
private LootGenerator(){}
public static LootGenerator instance()
  {
  if(INSTANCE==null)
    {
    INSTANCE = new LootGenerator();
    }
  return INSTANCE;
  }

public void generateLootFor(IInventory inventory, int slots, int level, Random random)
  {
  switch(level)
  {
  default:
  case 0:
  WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), inventory, 3);
  break;
  case 1:
  WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), inventory, 6);
  break;
  case 2:
  WeightedRandomChestContent.generateChestContents(random, ChestGenHooks.getItems(ChestGenHooks.DUNGEON_CHEST, random), inventory, 9);
  break;
  }
  }

public void addLootToTables()
  {    
//  for(ResearchGoal g : ResearchGoal.researchGoals)
//    {
//    if(g==null){continue;}
//    if(g.isEnabled() && g.isEnabledForLoot())
//      {
//      addLoot(new ItemStack(ItemLoaderCore.researchNotes,1,g.getGlobalResearchNum()) , 1 , 1 , 1);      
//      }
//    }
//  for(IVehicleType t : VehicleType.vehicleTypes)
//    {
//    if(t==null || !t.isEnabled() || !t.isEnabledForLoot()){continue;}
//    int level = t.getMaterialType().getNumOfLevels();
//    for(int i = 0; i< t.getMaterialType().getNumOfLevels(); i++)
//      {
//      level = i;
//      level /=2;
//      if(level==0){level=1;}
//      addLoot(t.getStackForLevel(i), 1, 1, level);
//      }
//    }
//  for(IVehicleUpgradeType t : VehicleUpgradeRegistry.instance().getUpgradeList())
//    {
//    if(t==null){continue;}
//    addLoot(t.getUpgradeStack(1), 1, 1, 2);
//    }  
//  for(IAmmoType t : Ammo.ammoTypes)
//    {
//    if(t==null){continue;}
//    addLoot(t.getAmmoStack(1),5,32,2);
//    }
//  for(ICivicType c : Civic.civicList)
//    {
//    if(c==null){continue;}
//    addLoot(c.getItemToConstruct(), 1, 1, 1);
//    }
  
//  addLoot(new ItemStack(ItemLoaderCore.npcCommandBaton,1,0),1,1,15);
//  addLoot(new ItemStack(ItemLoaderCore.npcCommandBaton,1,1),1,1,5);
//    
  
  }

private void addLoot(ItemStack item, int min, int max, int weight)
  {
  ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(item, min, max, weight));
  }

}
