package net.shadowmage.ancientwarfare.core.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.structure.item.ItemSpawnerPlacer;

public class AWCoreBlockLoader
{

public static final AWCoreBlockLoader INSTANCE = new AWCoreBlockLoader();
private AWCoreBlockLoader(){}

public static final CreativeTabs coreTab = new CreativeTabs("tabs.awcore")
  {  
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {
    return Items.beef;
    }
  };

public void load()
  {
  AWBlocks.engineeringStation = new BlockEngineeringStation("engineering_station");
  GameRegistry.registerBlock(AWBlocks.engineeringStation, "engineering_station");
  GameRegistry.registerTileEntity(TileEngineeringStation.class, "engineering_station_tile");
  }

}
