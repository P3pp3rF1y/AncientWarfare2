package net.shadowmage.ancientwarfare.structure.item;

import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import cpw.mods.fml.common.registry.GameRegistry;

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
