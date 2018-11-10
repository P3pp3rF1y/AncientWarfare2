package net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins;

import net.shadowmage.ancientwarfare.automation.init.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleRotatable;
import net.shadowmage.ancientwarfare.structure.template.plugin.defaultplugins.blockrules.TemplateRuleTorqueMultiblock;

public class StructurePluginAutomation implements StructureContentPlugin {
	@Override
	public void addHandledBlocks(StructurePluginManager manager) {
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.CROP_FARM, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.ANIMAL_FARM, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.FISH_FARM, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.TREE_FARM, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.QUARRY, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_CONTROL, TemplateRuleRotatable::new, TemplateRuleRotatable::new);

		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.TORQUE_SHAFT, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.TORQUE_JUNCTION, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.TORQUE_DISTRIBUTOR, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.FLYWHEEL_CONTROLLER, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.STIRLING_GENERATOR, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.WATERWHEEL_GENERATOR, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.HAND_CRANKED_GENERATOR, TemplateRuleRotatable::new, TemplateRuleRotatable::new);
		manager.registerBlockHandler(TemplateRuleRotatable.PLUGIN_NAME, AWAutomationBlocks.WINDMILL_GENERATOR, TemplateRuleRotatable::new, TemplateRuleRotatable::new);

		manager.registerBlockHandler(TemplateRuleTorqueMultiblock.PLUGIN_NAME, AWAutomationBlocks.FLYWHEEL_STORAGE, TemplateRuleTorqueMultiblock::new, TemplateRuleTorqueMultiblock::new);
		manager.registerBlockHandler(TemplateRuleTorqueMultiblock.PLUGIN_NAME, AWAutomationBlocks.WINDMILL_BLADE, TemplateRuleTorqueMultiblock::new, TemplateRuleTorqueMultiblock::new);
	}

	@Override
	public void addHandledEntities(StructurePluginManager manager) {
		//noop, no entities in automation module
	}

}
