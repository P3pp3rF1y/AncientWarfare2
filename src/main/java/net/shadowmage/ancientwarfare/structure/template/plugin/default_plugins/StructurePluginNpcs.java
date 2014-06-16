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
import net.shadowmage.ancientwarfare.npc.entity.NpcPriest;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.entity.NpcWorker;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcBanditTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcDesertTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativePriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcNativeTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPiratePriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcPirateTrader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingLeader;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingMountedArcher;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingMountedSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingPriest;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingSoldier;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcVikingTrader;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityNpc;

public class StructurePluginNpcs extends StructureContentPlugin
{

public StructurePluginNpcs()
  {
  
  }

@Override
public void addHandledBlocks(IStructurePluginManager manager)
  {
  manager.registerBlockHandler("awTownHall", AWNPCBlockLoader.townHall, TemplateRuleBlockLogic.class);
  }

@Override
public void addHandledEntities(IStructurePluginManager manager)
  {
  manager.registerEntityHandler("AWNpc", NpcCombat.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcWorker.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcCourier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPriest.class, TemplateRuleEntityLogic.class);
  manager.registerEntityHandler("AWNpc", NpcBard.class, TemplateRuleEntityNpc.class);
    
  manager.registerEntityHandler("AWNpc", NpcBanditArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditPriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditMountedArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcBanditMountedSoldier.class, TemplateRuleEntityNpc.class);
  
  manager.registerEntityHandler("AWNpc", NpcDesertArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertPriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertMountedArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcDesertMountedSoldier.class, TemplateRuleEntityNpc.class);
  
  manager.registerEntityHandler("AWNpc", NpcVikingArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingPriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingMountedArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcVikingMountedSoldier.class, TemplateRuleEntityNpc.class);
  
  manager.registerEntityHandler("AWNpc", NpcPirateArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPirateSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPirateLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPiratePriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPirateTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPirateMountedArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcPirateMountedSoldier.class, TemplateRuleEntityNpc.class);
  
  manager.registerEntityHandler("AWNpc", NpcNativeArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativeSoldier.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativeLeader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativePriest.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativeTrader.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativeMountedArcher.class, TemplateRuleEntityNpc.class);
  manager.registerEntityHandler("AWNpc", NpcNativeMountedSoldier.class, TemplateRuleEntityNpc.class);
  }

public static void load()
  {
  StructurePluginManager.instance().addPlugin(new StructurePluginNpcs());
  }

}
