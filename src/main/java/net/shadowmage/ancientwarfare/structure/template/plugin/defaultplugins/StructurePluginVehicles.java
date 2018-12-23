package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.entityrules.TemplateRuleVehicle;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

public class StructurePluginVehicles implements StructureContentPlugin {
	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		//noop
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		manager.registerEntityHandler(TemplateRuleVehicle.PLUGIN_NAME, VehicleBase.class, TemplateRuleVehicle::new, TemplateRuleVehicle::new);
	}
}
