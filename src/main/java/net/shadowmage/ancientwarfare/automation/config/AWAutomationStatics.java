/*
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.shadowmage.ancientwarfare.automation.config;

import net.minecraftforge.common.config.Property;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;

public class AWAutomationStatics extends ModConfiguration {
	//TODO config annotations
	public static int conduitRenderID = -1;

	public static int fishFarmRescanTicks = 200;
	public static int animalFarmRescanTicks = 200;

	/*
	 * Travel time per block when sending/receiving items using the mailbox system<br>
	 * Distances are calculated as a floating point distance and rounded to the nearest whole<br>
	 */
	public static int mailboxTimePerBlock = 10;

	/*
	 * Travel time for mail using mailboxes when items are being sent/received in different dimensions
	 */
	public static int mailboxTimeForDimension = 100;

	/*
	 * used to reduce network updates
	 */
	public static int energyMinNetworkUpdateFrequency = 5;//default 4 updates/sec max; less if not needed

	public static boolean enable_energy_network_updates = true;
	public static boolean enable_energy_client_updates = true;

	public static double low_efficiency_factor = 0.99990d;
	public static double med_efficiency_factor = 0.99995d;
	public static double high_efficiency_factor = 0.99999d;

	public static double low_transfer_max = 32.d;
	public static double med_transfer_max = 192.d;
	public static double high_transfer_max = 1024.d;

	public static double low_conduit_energy_max = 32.d;
	public static double med_conduit_energy_max = 192.d;
	public static double high_conduit_energy_max = 1024.f;

	public static double low_storage_energy_max = 9600.d;
	public static double med_storage_energy_max = 14400.d;
	public static double high_storage_energy_max = 24000.d;

	public static double stirling_generator_output = 1.d;
	public static double waterwheel_generator_output = 1.d;
	public static double hand_cranked_generator_output = 1.d;
	public static double windmill_per_size_output = 1.d;

	public static final float rpmToRpt = (float) (360.d / 60.d / 20.d);
	public static final float low_quality_rpm = 100;
	public static final float low_rpt = low_quality_rpm * rpmToRpt;

	public static double rfToTorque = 1.d / 10.d;
	public static final double mjToTorque = 1.d;

	public static double torqueToRf = 10.d;
	public static final double torqueToMj = 1.d;

	public static Property renderWorkBounds;

	public static String[] animal_farm_pickups = new String[] {"minecraft:leather",
			"minecraft:beef",
			"minecraft:cooked_beef",
			"minecraft:chicken",
			"minecraft:cooked_chicken",
			"minecraft:feather",
			"minecraft:egg",
			"minecraft:wool",
			"minecraft:cooked_porkchop",
			"minecraft:porkchop",
			"harderwildlife:rawleather"};

	public AWAutomationStatics(String mod) {
		super(mod);
	}

	@Override
	public void initializeCategories() {
		config.addCustomCategoryComment(generalOptions, "General Options\n" + "Affect both client and server.\nThese configs must match for client and server, or strange and probably BAD things WILL happen.");

		config.addCustomCategoryComment(serverOptions, "Server Options\n" + "Affect only server-side operations.\nWill need to be set for dedicated servers, and single player (or LAN worlds).\nClients playing on remote servers can ignore these settings.");

		config.addCustomCategoryComment(clientOptions, "Client Options\n" + "Affect only client-side operations.\nMany of these options can be set from the in-game Options GUI.\n" + "Server admins can ignore these settings.");
	}

	@Override
	public void initializeValues() {

		double temp = config.get(generalOptions, "rf_energy_conversion_factor", torqueToRf, "How much rf energy units for one unit of torque based energy.\n" + "Default= " + torqueToRf + "\n" + "Zero or negative values stop rf energy from being used in automated blocks.").getDouble();
		if (temp <= 0.0D) {
			torqueToRf = 0;
			rfToTorque = 0;
		} else {
			torqueToRf = temp;
			rfToTorque = 1 / temp;
		}

		energyMinNetworkUpdateFrequency = config.get(generalOptions, "energy_network_update_frequency", energyMinNetworkUpdateFrequency, "Alter the frequency at which network updates are sent to clients.\n" + "Default= " + energyMinNetworkUpdateFrequency + "\n" + "Lower values send data more often.\nHigher values send less often.\nZero or negative values send every tick.").getInt();

		enable_energy_network_updates = config.get(serverOptions, "enable_server_energy_network", enable_energy_network_updates, "Enable/Disable Sending network updates for energy tiles.\n" + "Default = " + enable_energy_network_updates + "\n" + "Disabling may improve server network performance on congested/low-bandwith deployments.").getBoolean();

		mailboxTimePerBlock = config.get(serverOptions, "mailbox_travel_time_per_block", mailboxTimePerBlock, "Ticks per block to be traveled for teleporting items.\n" + "Default= " + mailboxTimePerBlock + "\n" +  "Higher values increase travel time for items.\nLower values reduce travel time.\n" + "Zero or negative values result in instant transfer.").getInt();

		mailboxTimeForDimension = config.get(serverOptions, "mailbox_travel_time_per_dimension", mailboxTimeForDimension, "Ticks for dimensional travel for teleporting items.\n" + "Default= " + mailboxTimeForDimension + "\n" +  "Higher values increase travel time for items.\nLower values reduce travel time.\n" + "Zero or negative values result in instant transfer.").getInt();

		low_efficiency_factor = config.get(serverOptions, "low_quality_tile_energy_drain", low_efficiency_factor, "Factor applied to base drain algorithm to determine energy loss for low-quality torque tiles.\n" + "Default = " + low_efficiency_factor + "\n" +  "Higher values result in more energy drain.\nLower values result in less.\n" + "Negative values will result in a feedback loop of free/infinite power.").getDouble();

		med_efficiency_factor = config.get(serverOptions, "med_quality_tile_energy_drain", med_efficiency_factor, "Factor applied to base drain algorithm to determine energy loss for medium-quality torque tiles.\n" + "Default = " + med_efficiency_factor + "\n" +  "Higher values result in more energy drain.\nLower values result in less.\n" + "Negative values will result in a feedback loop of free/infinite power.").getDouble();

		high_efficiency_factor = config.get(serverOptions, "high_quality_tile_energy_drain", high_efficiency_factor, "Factor applied to base drain algorithm to determine energy loss for high-quality torque tiles.\n" + "Default = " + high_efficiency_factor + "\n" +  "Higher values result in more energy drain.\nLower values result in less.\n" + "Negative values will result in a feedback loop of free/infinite power.").getDouble();

		low_transfer_max = config.get(serverOptions, "low_quality_tile_energy_transfer", low_transfer_max, "How much energy may be output per tick by low-quality torque tiles.\n" + "Default = " + low_transfer_max + "\n" +  "Higher values result in more thoroughput of energy network.\nLower values result in less.\n" + "Negative values will cause energy transfer to cease functioning.").getDouble();

		med_transfer_max = config.get(serverOptions, "med_quality_tile_energy_transfer", med_transfer_max, "How much energy may be output per tick by medium-quality torque tiles.\n" + "Default = " + med_transfer_max + "\n" +  "Higher values result in more thoroughput of energy network.\nLower values result in less.\n" + "Negative values will cause energy transfer to cease functioning.").getDouble();

		high_transfer_max = config.get(serverOptions, "high_quality_tile_energy_transfer", high_transfer_max, "How much energy may be output per tick by high-quality torque tiles.\n" + "Default = " + high_transfer_max + "\n" +  "Higher values result in more thoroughput of energy network.\nLower values result in less.\n" + "Negative values will cause energy transfer to cease functioning.").getDouble();

		low_conduit_energy_max = config.get(serverOptions, "low_quality_conduit_energy_max", low_conduit_energy_max, "How much energy may be stored in low-quality energy transport tiles.\n" + "Default = " + low_conduit_energy_max + "\n" + "Directly sets the amount of torque/MJ that a transport conduit may store internally.").getDouble();

		med_conduit_energy_max = config.get(serverOptions, "med_quality_conduit_energy_max", med_conduit_energy_max, "How much energy may be stored in medium-quality energy transport tiles.\n" + "Default = " + med_conduit_energy_max + "\n" + "Directly sets the amount of torque/MJ that a transport conduit may store internally.").getDouble();

		high_conduit_energy_max = config.get(serverOptions, "high_quality_conduit_energy_max", high_conduit_energy_max, "How much energy may be stored in high-quality energy transport tiles.\n" + "Default = " + high_conduit_energy_max + "\n" + "Directly sets the amount of torque/MJ that a transport conduit may store internally.").getDouble();

		low_storage_energy_max = config.get(serverOptions, "low_quality_storage_energy_max", low_storage_energy_max, "How much energy may be stored in low-quality energy storage tiles.\n" + "Default = " + low_storage_energy_max + "\n" + "Directly sets the amount of torque/MJ that a storage tile may store internally.").getDouble();

		med_storage_energy_max = config.get(serverOptions, "med_quality_storage_energy_max", med_storage_energy_max, "How much energy may be stored in medium-quality energy storage tiles.\n" + "Default = " + med_storage_energy_max + "\n" + "Directly sets the amount of torque/MJ that a storage tile may store internally.").getDouble();

		high_storage_energy_max = config.get(serverOptions, "high_quality_storage_energy_max", high_storage_energy_max, "How much energy may be stored in high-quality energy storage tiles.\n" + "Default = " + high_storage_energy_max + "\n" + "Directly sets the amount of torque/MJ that a storage tile may store internally.").getDouble();

		stirling_generator_output = config.get(serverOptions, "stirling_generator_output_factor", stirling_generator_output, "Factor applied to energy output from stirling generator.\n" + "Default = " + stirling_generator_output + "\n" +  "Lower values reduce output.\nHigher values increase output.\n" + "Zero or negative values will result in no energy output").getDouble();

		waterwheel_generator_output = config.get(serverOptions, "waterwheel_generator_output_factor", waterwheel_generator_output, "Factor applied to energy output from waterwheel generator.\n" + "Default = " + waterwheel_generator_output + "\n" +  "Lower values reduce output.\nHigher values increase output.\n" + "Zero or negative values will result in no energy output").getDouble();

		hand_cranked_generator_output = config.get(serverOptions, "hand_cranked_generator_output_factor", hand_cranked_generator_output, "Factor applied to energy output from hand-cranked generator.\n" + "Default = " + hand_cranked_generator_output + "\n" +  "Lower values reduce output.\nHigher values increase output.\n" + "Zero or negative values will result in no energy output").getDouble();
		windmill_per_size_output = config.get(serverOptions, "windmill_per_size_output_factor", windmill_per_size_output, "Factor applied to energy output from windmill based on size.\n" + "Default = " + windmill_per_size_output + "\n" +  "Lower values reduce output.\nHigher values increase output.\n" + "Zero or negative values will result in no energy output").getDouble();

		enable_energy_client_updates = config.getBoolean("enable_client_energy_animations", clientOptions, enable_energy_client_updates, "Enable client-side animation of power tiles.\nDisabling may improve rendering performance on low-end machines");
		renderWorkBounds = config.get(clientOptions, "render_work_bounds", true);

		animal_farm_pickups = config.get(serverOptions, "animal_farm_pickups", animal_farm_pickups, "List of items that Animal Farms can pickup\n" + "Animal farms will periodically pickup any of these items detected (if they're not caught on-demand by the farmer culling animals).\n" + "Use the format modid:itemid[:itemDamage]").getStringList();
	}

}
