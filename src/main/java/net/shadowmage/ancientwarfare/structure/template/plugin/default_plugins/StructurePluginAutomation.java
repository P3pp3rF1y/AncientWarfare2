package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins;

import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
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
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteCropFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteAnimalFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteFishFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteForestry, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteQuarry, TemplateRuleRotable.class);
		manager.registerBlockHandler(WORKSITE_PLUGIN_NAME, AWAutomationBlocks.worksiteWarehouse, TemplateRuleRotable.class);

		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.torqueShaft, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.torqueJunction, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.torqueDistributor, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.flywheel, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.stirlingGenerator, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.torqueGeneratorWaterwheel, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.handCrankedGenerator, TemplateRuleRotable.class);
		manager.registerBlockHandler(TORQUE_TILE_PLUGIN_NAME, AWAutomationBlocks.windmillControl, TemplateRuleRotable.class);

		manager.registerBlockHandler(TORQUE_MULTI_PLUGIN_NAME, AWAutomationBlocks.flywheelStorage, TemplateRuleTorqueMultiblock.class);
		manager.registerBlockHandler(TORQUE_MULTI_PLUGIN_NAME, AWAutomationBlocks.windmillBlade, TemplateRuleTorqueMultiblock.class);

		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.warehouseCrafting, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.warehouseInterface, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.warehouseStockViewer, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.warehouseStorageBlock, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.chunkLoaderSimple, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.chunkLoaderDeluxe, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.mailbox, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler(AUTOMATION_LOGIC_PLUGIN_NAME, AWAutomationBlocks.worksiteAutoCrafting, TemplateRuleAutomationLogic.class);
	}

	@Override
	public void addHandledEntities(IStructurePluginManager manager) {
		//noop, no entities in automation module
	}

}
