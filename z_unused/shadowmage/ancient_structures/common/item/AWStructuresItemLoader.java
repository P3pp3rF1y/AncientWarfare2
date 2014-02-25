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
package shadowmage.ancient_structures.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;
import shadowmage.ancient_framework.common.item.AWItemBase;
import shadowmage.ancient_structures.AWStructures;

public class AWStructuresItemLoader
{

private AWStructuresItemLoader(){}
private static AWStructuresItemLoader instance = new AWStructuresItemLoader();
public static AWStructuresItemLoader instance(){return instance;}
private static Configuration config = AWStructures.instance.config.getConfig();
public static CreativeTabs structureTab = new CreativeTabs("Ancient Structures")
{

@Override
public Item getTabIconItem()
  {  
  return structureScanner;
  }

@Override
public String getTranslatedTabLabel()
  {
  return super.getTabLabel();
  }  
};//need to declare this instance prior to any items that use it as their creative tab

public static AWItemBase structureScanner = new ItemStructureScanner(config, "item.structurescanner");
public static AWItemBase structureBuilderCreative = new ItemBuilderCreative(config, "item.structurebuilder");
public static AWItemBase spawnerPlacer = new ItemSpawnerPlacer(config, "item.spawnerplacer");
public static AWItemBase structureGenerator = new ItemStructureGenerator(config, "item.structuregenerator");

public void registerItems()
  {
  structureScanner.addDisplayStack(0, new ItemStack(structureScanner));
  structureScanner.addDisplayName(0, structureScanner.getUnlocalizedName());
  structureScanner.addIcon(0, "ancientwarfare:structure/structureScanner");
  
  structureBuilderCreative.addDisplayStack(0, new ItemStack(structureBuilderCreative));
  structureBuilderCreative.addDisplayName(0, structureBuilderCreative.getUnlocalizedName());
  structureBuilderCreative.addIcon(0, "ancientwarfare:structure/structureBuilder");

  spawnerPlacer.addDisplayStack(0, new ItemStack(spawnerPlacer));
  spawnerPlacer.addDisplayName(0, spawnerPlacer.getUnlocalizedName());
  spawnerPlacer.addIcon(0, "ancientwarfare:structure/spawnerPlacer");
  
  structureGenerator.addDisplayStack(0, new ItemStack(structureGenerator));
  structureGenerator.addDisplayName(0, structureGenerator.getUnlocalizedName());
  structureGenerator.addIcon(0, "ancientwarfare:structure/structureGenerator");
  }

}
