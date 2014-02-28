package net.shadowmage.ancientwarfare.structure.item;

import cpw.mods.fml.common.registry.GameRegistry;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;

public class AWStructuresItemLoader
{


public static final ItemClickable testItem = new ItemClickable("testItem");
public static final ItemStructureScanner scanner = new ItemStructureScanner("structureScanner");

public static void load()
  {  
  GameRegistry.registerItem(testItem, "testItem");
  GameRegistry.registerItem(scanner, "structureScanner");
  }

}
