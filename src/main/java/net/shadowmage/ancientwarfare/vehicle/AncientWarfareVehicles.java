package net.shadowmage.ancientwarfare.vehicle;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.crafting.AWVehicleCrafting;
import net.shadowmage.ancientwarfare.vehicle.item.AWVehicleItemLoader;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicle;
import net.shadowmage.ancientwarfare.vehicle.refactoring.ballistics.TrajectoryPlotter;
import net.shadowmage.ancientwarfare.vehicle.refactoring.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.refactoring.entity.AWVehicleEntityLoader;
import net.shadowmage.ancientwarfare.vehicle.refactoring.network.PacketInputReply;
import net.shadowmage.ancientwarfare.vehicle.refactoring.network.PacketInputState;
import net.shadowmage.ancientwarfare.vehicle.refactoring.proxy.VehicleCommonProxy;
import org.apache.logging.log4j.Logger;

/*
@Mod
        (
                name = "Ancient Warfare Vehicles",
                modid = AncientWarfareVehicles.modID,
                version = "@VERSION@",
                dependencies = "required-after:ancientwarfare"
        )
*/

public class AncientWarfareVehicles {
    public static final String modID = "ancientwarfarevehicle";

//    @Instance(value = modID)
    public static AncientWarfareVehicles instance;

/*
    @SidedProxy
            (
                    clientSide = "VehicleClientProxy",
                    serverSide = "VehicleCommonProxy"
            )
*/
    public static VehicleCommonProxy proxy;

    public static AWVehicleStatics statics;

    public static Logger log;

//    @EventHandler
    public void preInit(FMLPreInitializationEvent evt) {

        log = evt.getModLog();

        ModuleStatus.vehiclesLoaded = true;

        /*
         * setup module-owned config file and config-access class
         */
        statics = new AWVehicleStatics("AncientWarfareVehicle");

        /*
         * load pre-init (items, blocks, entities)
         */
        proxy.preInit();
        AWVehicleEntityLoader.load();
        AWVehicleItemLoader.load();

        /*
         * register tick-handlers
         */
        PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_INPUT_STATE, PacketInputState.class);
        PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_INPUT_RESPONSE, PacketInputReply.class);
        PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE, PacketVehicle.class);
        for (int i = 0; i < 100; i++) {
            TrajectoryPlotter.loadTest();
        }
    }

//    @EventHandler
    public void init(FMLInitializationEvent evt) {
        proxy.init();

        /*
         * construct recipes, load plugins
         */
        AWVehicleCrafting.loadRecipes();
        /*
         * save config for any changes that were made during loading stages
         */
        statics.save();
    }
}
