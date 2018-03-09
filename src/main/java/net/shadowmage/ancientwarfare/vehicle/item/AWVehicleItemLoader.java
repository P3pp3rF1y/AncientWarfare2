package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.vehicle.registry.AmmoRegistry;
import net.shadowmage.ancientwarfare.vehicle.registry.ArmorRegistry;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.modID)
public class AWVehicleItemLoader {

    public static final CreativeTabs vehicleTab = new CreativeTabs("tabs.vehicles") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(AWItems.researchBook);//TODO set an appropriate icon for the vehicles tab
        }
    };

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemSpawner());
        registry.register(new ItemBaseVehicle("upgrade"));
        registry.register(new ItemBaseVehicle("flame_charge"));
        registry.register(new ItemBaseVehicle("explosive_charge"));
        registry.register(new ItemBaseVehicle("rocket_charge"));
        registry.register(new ItemBaseVehicle("cluster_charge"));
        registry.register(new ItemBaseVehicle("napalm_charge"));
        registry.register(new ItemBaseVehicle("clay_casing"));
        registry.register(new ItemBaseVehicle("iron_casing"));
        registry.register(new ItemBaseVehicle("mobility_unit"));
        registry.register(new ItemBaseVehicle("turret_components"));
        registry.register(new ItemBaseVehicle("torsion_unit"));
        registry.register(new ItemBaseVehicle("counter_weight_unit"));
        registry.register(new ItemBaseVehicle("powder_case"));
        registry.register(new ItemBaseVehicle("equipment_bay"));

        AmmoRegistry.registerAmmo(registry);
        ArmorRegistry.registerArmorTypes(registry);
    }
}
