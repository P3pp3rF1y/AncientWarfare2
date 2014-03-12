package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.item.ItemClickable;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWStructuresItemLoader
{

public static final CreativeTabs structureTab = new CreativeTabs("tabs.structures")
  {    
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {
    return Items.stick;
    }
  };

public static final ItemClickable testItem = new ItemClickable("test_item");
public static final ItemStructureScanner scanner = new ItemStructureScanner("structure_scanner");
public static final ItemStructureBuilder builder = new ItemStructureBuilder("structure_builder");
public static final ItemSpawnerPlacer spawner = new ItemSpawnerPlacer("spawner_placer");

public static void load()
  {  
  GameRegistry.registerItem(testItem, "test_item");
  GameRegistry.registerItem(scanner, "structure_scanner");
  GameRegistry.registerItem(builder, "structure_builder");
  GameRegistry.registerItem(spawner, "spawner_placer");  
  }

}
