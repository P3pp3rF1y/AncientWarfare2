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

import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.entity.NpcCourier;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditTrader;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockInventory;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityNpc;

public class StructurePluginNpcs extends StructureContentPlugin
{

public StructurePluginNpcs()
  {
  
  }

@Override
public void addHandledBlocks(IStructurePluginManager manager)
  {
  manager.registerBlockHandler("awTownHall", AWNPCBlockLoader.townHall, TemplateRuleBlockInventory.class);
  }

@Override
public void addHandledEntities(IStructurePluginManager manager)
  {
  manager.registerEntityHandler("AWNpc", NpcCombat.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcWorker.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcCourier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcTrader.class, TemplateRuleEntityNpc.class);
//  manager.registerEntityHandler("AWNpc", NpcPriest.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBard.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditPriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditTrader.class, TemplateRuleEntityNpc.class);
  }

public static void load()
  {
  StructurePluginManager.instance().addPlugin(new StructurePluginNpcs());
  }

}
