package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;

public class StructurePluginVehicles implements StructureContentPlugin {

	public StructurePluginVehicles() {

	}

	@Override
	public void addHandledBlocks(StructurePluginManager manager) {

	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		//TODO add when vehicles are tuned
		//manager.registerEntityHandler("ancientWarfareVehicle", EntityVehicle.class, TemplateRuleEntityLogic.class);
	}
}
