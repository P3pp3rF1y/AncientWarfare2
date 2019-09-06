package net.shadowmage.ancientwarfare.vehicle;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicle;
import net.shadowmage.ancientwarfare.vehicle.container.ContainerVehicleInventory;
import net.shadowmage.ancientwarfare.vehicle.init.AWVehicleEntities;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAimUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAmmoSelect;
import net.shadowmage.ancientwarfare.vehicle.network.PacketAmmoUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketFireUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketPackCommand;
import net.shadowmage.ancientwarfare.vehicle.network.PacketSingleAmmoUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketTurretAnglesUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketUpgradeUpdate;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleInput;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleMove;
import net.shadowmage.ancientwarfare.vehicle.proxy.CommonProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare Vehicles", modid = AncientWarfareVehicles.MOD_ID, version = "@VERSION@", dependencies = "required-after:ancientwarfare")

public class AncientWarfareVehicles {
	public static final String MOD_ID = "ancientwarfarevehicle";

	public static final CreativeTabs TAB = new AWVehicleTab();

	@Instance(value = MOD_ID)
	public static AncientWarfareVehicles instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.vehicle.proxy.ClientProxy", serverSide = "net.shadowmage.ancientwarfare.vehicle.proxy.CommonProxy")
	public static CommonProxy proxy;

	public static AWVehicleStatics statics;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		statics = new AWVehicleStatics("AncientWarfareVehicle");

		AWVehicleEntities.load();

		PacketBase.registerPacketType(NetworkHandler.PACKET_AIM_UPDATE, PacketAimUpdate.class, PacketAimUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_AMMO_SELECT, PacketAmmoSelect.class, PacketAmmoSelect::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_AMMO_UPDATE, PacketAmmoUpdate.class, PacketAmmoUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_FIRE_UPDATE, PacketFireUpdate.class, PacketFireUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_PACK_COMMAND, PacketPackCommand.class, PacketPackCommand::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_SINGLE_AMMO_UPDATE, PacketSingleAmmoUpdate.class, PacketSingleAmmoUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_TURRET_ANGLES_UPDATE, PacketTurretAnglesUpdate.class, PacketTurretAnglesUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_UPGRADE_UPDATE, PacketUpgradeUpdate.class, PacketUpgradeUpdate::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_INPUT, PacketVehicleInput.class, PacketVehicleInput::new);
		PacketBase.registerPacketType(NetworkHandler.PACKET_VEHICLE_MOVE, PacketVehicleMove.class, PacketVehicleMove::new);

		NetworkHandler.registerContainer(NetworkHandler.GUI_VEHICLE_INVENTORY, ContainerVehicleInventory.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_VEHICLE_AMMO_SELECTION, ContainerVehicle.class);

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {
		proxy.init();

		statics.save();
	}
}
