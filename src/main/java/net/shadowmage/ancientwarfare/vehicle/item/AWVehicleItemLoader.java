package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.api.AWItems;

public class AWVehicleItemLoader {

    public static final CreativeTabs vehicleTab = new CreativeTabs("tabs.vehicles") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(AWItems.researchBook);//TODO set an appropriate icon for the vehicles tab
        }
    };

    public static final ItemVehicleSpawner spawner = new ItemVehicleSpawner("vehicle_spawner");

    public static void load() {
        //TODO registration
        //GameRegistry.registerItem(spawner, "vehicle_spawner");
    }

}
