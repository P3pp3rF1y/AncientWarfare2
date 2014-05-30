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

import net.shadowmage.ancientwarfare.core.config.AWLog;
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
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityLogic;

public class StructurePluginNpcs extends StructureContentPlugin
{

public StructurePluginNpcs()
  {
  
  }

@Override
public void addHandledBlocks(IStructurePluginManager manager)
  {

  }

@Override
public void addHandledEntities(IStructurePluginManager manager)
  {
  manager.registerEntityHandler("AWNpc", NpcCombat.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcWorker.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcCourier.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcTrader.class, TemplateRuleEntityLogic.class);
//  manager.registerEntityHandler("AWNpc", NpcPriest.class, TemplateRuleEntityLogic.class);
//  manager.registerEntityHandler("AWNpc", NpcBard.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBanditArcher.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBanditSoldier.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBanditLeader.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBanditPriest.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBanditTrader.class, TemplateRuleEntityLogic.class);
  }

public static void load()
  {
  StructurePluginManager.instance().addPlugin(new StructurePluginNpcs());
  }

}
