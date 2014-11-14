package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
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
    return scanner;
    }
  };

public static final ItemStructureScanner scanner = new ItemStructureScanner("structure_scanner");
public static final ItemStructureBuilder builder = new ItemStructureBuilder("structure_builder");
public static final ItemStructureBuilderWorldGen builderWorldGen = new ItemStructureBuilderWorldGen("structure_builder_world_gen");
public static final ItemTownBuilder townBuilder = new ItemTownBuilder("town_builder");
public static final ItemSpawnerPlacer spawner = new ItemSpawnerPlacer("spawner_placer");
public static final ItemGateSpawner gateSpawner = new ItemGateSpawner("gate_spawner");
public static final ItemBlockInfo blockInfo = new ItemBlockInfo("block_info_clicker");

public static final ItemConstructionTool constructionTool = new ItemConstructionTool("construction_tool");
public static final ItemConstructionToolLakes constructionToolLakes = new ItemConstructionToolLakes("construction_tool_lakes");

public static void load()
  {  
  GameRegistry.registerItem(scanner, "structure_scanner");
  GameRegistry.registerItem(builder, "structure_builder");
  GameRegistry.registerItem(builderWorldGen, "structure_builder_world_gen");
  GameRegistry.registerItem(townBuilder, "town_builder");
  GameRegistry.registerItem(spawner, "spawner_placer");    
  GameRegistry.registerItem(gateSpawner, "gate_spawner");
  GameRegistry.registerItem(constructionTool, "construction_tool");
  GameRegistry.registerItem(constructionToolLakes, "construction_tool_lakes");
  GameRegistry.registerItem(blockInfo, "block_info_clicker");  
  }

}
