package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWVehicleItemLoader
{

public static final CreativeTabs vehicleTab = new CreativeTabs("tabs.vehicles")
  {
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {  
    return AWItems.researchBook;//TODO set an appropriate icon for the vehicles tab
    }  
  };

public static final ItemVehicleSpawner spawner = new ItemVehicleSpawner("vehicle_spawner");

public static void load()
  {
  GameRegistry.registerItem(spawner, "vehicle_spawner");
  }

}
