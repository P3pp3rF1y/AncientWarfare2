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

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.shadowmage.ancientwarfare.npc.block.AWNPCBlockLoader;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityNpc;

public class StructurePluginNpcs implements StructureContentPlugin {

    public StructurePluginNpcs() {

    }

    @Override
    public void addHandledBlocks(IStructurePluginManager manager) {
        manager.registerBlockHandler("awTownHall", AWNPCBlockLoader.townHall, TemplateRuleBlockLogic.class);
    }

    @Override
    public void addHandledEntities(IStructurePluginManager manager) {
        for (Object obj : EntityList.classToStringMapping.keySet()) {
            Class<? extends Entity> clazz = (Class<? extends Entity>) obj;
            if (NpcBase.class.isAssignableFrom(clazz)) {
                manager.registerEntityHandler("AWNpc", clazz, TemplateRuleEntityNpc.class);
            }
        }
    }

}
