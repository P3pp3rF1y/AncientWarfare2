/**
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

import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleAutomationLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleTorqueMultiblock;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleTorqueTile;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleWorksite;

public class StructurePluginAutomation extends StructureContentPlugin
{


public StructurePluginAutomation()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void addHandledBlocks(IStructurePluginManager manager)
  {
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteCropFarm, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteAnimalFarm, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteFishFarm, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteForestry, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteMushroomFarm, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteReedFarm, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteQuarry, TemplateRuleWorksite.class);
  manager.registerBlockHandler("awWorksite", AWAutomationBlockLoader.worksiteWarehouse, TemplateRuleWorksite.class);
  
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.torqueShaft, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.torqueConduit, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.torqueDistributor, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.flywheel, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.torqueGeneratorSterling, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.torqueGeneratorWaterwheel, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.handCrankedEngine, TemplateRuleTorqueTile.class);
  manager.registerBlockHandler("awTorqueTile", AWAutomationBlockLoader.windmillControl, TemplateRuleTorqueTile.class);
  
  manager.registerBlockHandler("awTorqueMulti", AWAutomationBlockLoader.flywheelStorage, TemplateRuleTorqueMultiblock.class);
  manager.registerBlockHandler("awTorqueMulti", AWAutomationBlockLoader.windmillBlade, TemplateRuleTorqueMultiblock.class);
  
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.warehouseCrafting, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.warehouseInterface, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.warehouseStockViewer, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.warehouseStorageBlock, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.chunkLoaderSimple, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.chunkLoaderDeluxe, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.mailbox, TemplateRuleAutomationLogic.class);
  manager.registerBlockHandler("awAutomationLogic", AWAutomationBlockLoader.worksiteAutoCrafting, TemplateRuleAutomationLogic.class);   
  }

@Override
public void addHandledEntities(IStructurePluginManager manager)
  {
  //noop, no entities in automation module
  }

}
