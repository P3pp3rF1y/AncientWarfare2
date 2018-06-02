package net.shadowmage.ancientwarfare.core.datafixes;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

import java.util.HashMap;
import java.util.Map;

public class TileIdFixer implements IFixableData {
	private Map<String, String> idMapping = new HashMap<>();

	public TileIdFixer() {
		idMapping.put("minecraft:quarry_tile", "ancientwarfareautomation:quarry_tile");
		idMapping.put("minecraft:forestry_tile", "ancientwarfareautomation:forestry_tile");
		idMapping.put("minecraft:crop_farm_tile", "ancientwarfareautomation:crop_farm_tile");
		idMapping.put("minecraft:fruit_farm_tile", "ancientwarfareautomation:fruit_farm_tile");
		idMapping.put("minecraft:mushroom_farm_tile", "ancientwarfareautomation:mushroom_farm_tile");
		idMapping.put("minecraft:animal_farm_tile", "ancientwarfareautomation:animal_farm_tile");
		idMapping.put("minecraft:fish_farm_tile", "ancientwarfareautomation:fish_farm_tile");
		idMapping.put("minecraft:reed_farm_tile", "ancientwarfareautomation:reed_farm_tile");
		idMapping.put("minecraft:warehouse_control_tile", "ancientwarfareautomation:warehouse_control_tile");
		idMapping.put("minecraft:warehouse_storage_medium_tile", "ancientwarfareautomation:warehouse_storage_medium_tile");
		idMapping.put("minecraft:warehouse_storage_small_tile", "ancientwarfareautomation:warehouse_storage_small_tile");
		idMapping.put("minecraft:warehouse_storage_large_tile", "ancientwarfareautomation:warehouse_storage_large_tile");
		idMapping.put("minecraft:warehouse_interface_tile", "ancientwarfareautomation:warehouse_interface_tile");
		idMapping.put("minecraft:warehouse_crafting_tile", "ancientwarfareautomation:warehouse_crafting_tile");
		idMapping.put("minecraft:warehouse_stock_viewer_tile", "ancientwarfareautomation:warehouse_stock_viewer_tile");
		idMapping.put("minecraft:auto_crafting_tile", "ancientwarfareautomation:auto_crafting_tile");
		idMapping.put("minecraft:mailbox_tile", "ancientwarfareautomation:mailbox_tile");
		idMapping.put("minecraft:flywheel_light_tile", "ancientwarfareautomation:flywheel_light_tile");
		idMapping.put("minecraft:flywheel_medium_tile", "ancientwarfareautomation:flywheel_medium_tile");
		idMapping.put("minecraft:flywheel_large_tile", "ancientwarfareautomation:flywheel_large_tile");
		idMapping.put("minecraft:flywheel_storage_tile", "ancientwarfareautomation:flywheel_storage_tile");
		idMapping.put("minecraft:torque_junction_light_tile", "ancientwarfareautomation:torque_junction_light_tile");
		idMapping.put("minecraft:torque_junction_medium_tile", "ancientwarfareautomation:torque_junction_medium_tile");
		idMapping.put("minecraft:torque_junction_heavy_tile", "ancientwarfareautomation:torque_junction_heavy_tile");
		idMapping.put("minecraft:torque_shaft_light_tile", "ancientwarfareautomation:torque_shaft_light_tile");
		idMapping.put("minecraft:torque_shaft_medium_tile", "ancientwarfareautomation:torque_shaft_medium_tile");
		idMapping.put("minecraft:torque_shaft_heavy_tile", "ancientwarfareautomation:torque_shaft_heavy_tile");
		idMapping.put("minecraft:torque_distributor_light_tile", "ancientwarfareautomation:torque_distributor_light_tile");
		idMapping.put("minecraft:torque_distributor_medium_tile", "ancientwarfareautomation:torque_distributor_medium_tile");
		idMapping.put("minecraft:torque_distributor_heavy_tile", "ancientwarfareautomation:torque_distributor_heavy_tile");
		idMapping.put("minecraft:stirling_generator_tile", "ancientwarfareautomation:stirling_generator_tile");
		idMapping.put("minecraft:waterwheel_generator_tile", "ancientwarfareautomation:waterwheel_generator_tile");
		idMapping.put("minecraft:hand_cranked_generator_tile", "ancientwarfareautomation:hand_cranked_generator_tile");
		idMapping.put("minecraft:windmill_blade_tile", "ancientwarfareautomation:windmill_blade_tile");
		idMapping.put("minecraft:windmill_generator_tile", "ancientwarfareautomation:windmill_generator_tile");
		idMapping.put("minecraft:chunk_loader_simple_tile", "ancientwarfareautomation:chunk_loader_simple_tile");
		idMapping.put("minecraft:chunk_loader_deluxe_tile", "ancientwarfareautomation:chunk_loader_deluxe_tile");
		idMapping.put("minecraft:engineering_station_tile", "ancientwarfare:engineering_station_tile");
		idMapping.put("minecraft:research_station_tile", "ancientwarfare:research_station_tile");
		idMapping.put("minecraft:town_hall_tile", "ancientwarfarenpc:town_hall_tile");
		idMapping.put("minecraft:advanced_spawner_tile", "ancientwarfarestructure:advanced_spawner_tile");
		idMapping.put("minecraft:gate_proxy_tile", "ancientwarfarestructure:gate_proxy_tile");
		idMapping.put("minecraft:drafting_station_tile", "ancientwarfarestructure:drafting_station_tile");
		idMapping.put("minecraft:structure_builder_ticked_tile", "ancientwarfarestructure:structure_builder_ticked_tile");
		idMapping.put("minecraft:sound_block_tile", "ancientwarfarestructure:sound_block_tile");

	}

	@Override
	public int getFixVersion() {
		return 2;
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound compound) {
		String oldId = compound.getString("id");
		if (idMapping.containsKey(oldId)) {
			compound.setString("id", idMapping.get(oldId));
		}
		return compound;
	}
}
