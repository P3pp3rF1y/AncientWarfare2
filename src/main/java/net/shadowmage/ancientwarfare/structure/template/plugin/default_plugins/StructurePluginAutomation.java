package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins;

import net.shadowmage.ancientwarfare.automation.init.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleAutomationLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleRotable;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleTorqueMultiblock;

public class StructurePluginAutomation implements StructureContentPlugin {
	private static final String WORKSITE_PLUGIN_NAME = "awWorksite";
	private static final String TORQUE_TILE_PLUGIN_NAME = "awTorqueTile";
	private static final String TORQUE_MULTI_PLUGIN_NAME = "awTorqueMulti";
	private static final String AUTOMATION_LOGIC_PLUGIN_NAME = "awAutomationLogic";

	@Override
	public void addHandledBlocks(IStructurePluginManager manager) {
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.CROP_FARM, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.ANIMAL_FARM, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.FISH_FARM, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.TREE_FARM, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.QUARRY, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_CONTROL, TemplateRuleRotable.class);

		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.TORQUE_SHAFT, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.TORQUE_JUNCTION, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.TORQUE_DISTRIBUTOR, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.FLYWHEEL_CONTROLLER, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.STIRLING_GENERATOR, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.WATERWHEEL_GENERATOR, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.HAND_CRANKED_GENERATOR, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.WINDMILL_GENERATOR, TemplateRuleRotable.class);

		manager.registerBlockHandler(TORQUE_MULTI_PLUGIN_NAME, AWAutomationBlocks.FLYWHEEL_STORAGE, TemplateRuleTorqueMultiblock.class);
		manager.registerBlockHandler(TORQUE_MULTI_PLUGIN_NAME, AWAutomationBlocks.WINDMILL_BLADE, TemplateRuleTorqueMultiblock.class);

		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_CRAFTING, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_INTERFACE, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_STOCK_VIEWER, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.WAREHOUSE_STORAGE, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.CHUNK_LOADER_SIMPLE, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.CHUNK_LOADER_DELUXE, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.MAILBOX, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.AUTO_CRAFTING, TemplateRuleAutomationLogic.class);
	}

	@Override
	public void addHandledEntities(IStructurePluginManager manager) {
		//noop, no entities in automation module
	}

}
