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

package net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins;

import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlocks;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.api.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleAutomationLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleRotable;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleTorqueMultiblock;

public class StructurePluginAutomation implements StructureContentPlugin {

	public StructurePluginAutomation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addHandledBlocks(IStructurePluginManager manager) {
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteCropFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteAnimalFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteFishFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteForestry, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteMushroomFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteReedFarm, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteQuarry, TemplateRuleRotable.class);
		manager.registerBlockHandler("awWorksite", AWAutomationBlocks.worksiteWarehouse, TemplateRuleRotable.class);

		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.torqueShaft, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.torqueJunction, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.torqueDistributor, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.flywheel, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.stirlingGenerator, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.torqueGeneratorWaterwheel, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.handCrankedGenerator, TemplateRuleRotable.class);
		manager.registerBlockHandler("awTorqueTile", AWAutomationBlocks.windmillControl, TemplateRuleRotable.class);

		manager.registerBlockHandler("awTorqueMulti", AWAutomationBlocks.flywheelStorage, TemplateRuleTorqueMultiblock.class);
		manager.registerBlockHandler("awTorqueMulti", AWAutomationBlocks.windmillBlade, TemplateRuleTorqueMultiblock.class);

		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.warehouseCrafting, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.warehouseInterface, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.warehouseStockViewer, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.warehouseStorageBlock, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.chunkLoaderSimple, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.chunkLoaderDeluxe, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.mailbox, TemplateRuleAutomationLogic.class);
		manager.registerBlockHandler("awAutomationLogic", AWAutomationBlocks.worksiteAutoCrafting, TemplateRuleAutomationLogic.class);
	}

	@Override
	public void addHandledEntities(IStructurePluginManager manager) {
		//noop, no entities in automation module
	}

}
