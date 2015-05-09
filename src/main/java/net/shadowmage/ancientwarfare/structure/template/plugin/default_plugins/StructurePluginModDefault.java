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

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.inventory.IInventory;
import net.shadowmage.ancientwarfare.structure.api.IStructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.plugin.StructureContentPlugin;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleBlockLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.block_rules.TemplateRuleModBlocks;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityHanging;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleEntityLogic;
import net.shadowmage.ancientwarfare.structure.template.plugin.default_plugins.entity_rules.TemplateRuleVanillaEntity;

import java.util.Map;

public class StructurePluginModDefault implements StructureContentPlugin {

    private final String mod;
    public StructurePluginModDefault(String id) {
        this.mod = id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addHandledBlocks(IStructurePluginManager manager) {
        for (Block aBlock : (Iterable<Block>) GameData.getBlockRegistry()) {
            if(aBlock!=null && GameData.getBlockRegistry().getNameForObject(aBlock).startsWith(mod)) {
                if (aBlock.hasTileEntity(0)) {
                    manager.registerBlockHandler("modContainerDefault", aBlock, TemplateRuleBlockLogic.class);
                } else {
                    manager.registerBlockHandler("modBlockDefault", aBlock, TemplateRuleModBlocks.class);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void addHandledEntities(IStructurePluginManager manager) {
        for (Object key : EntityList.stringToClassMapping.keySet()) {
            if(key.toString().startsWith(mod)) {
                Class<? extends Entity> clazz = (Class<? extends Entity>) EntityList.stringToClassMapping.get(key);
                if (EntityHanging.class.isAssignableFrom(clazz)) {
                    manager.registerEntityHandler("modHangingDefault", clazz, TemplateRuleEntityHanging.class);
                } else if (EntityAnimal.class.isAssignableFrom(clazz)) {
                    manager.registerEntityHandler("modAnimalDefault", clazz, TemplateRuleVanillaEntity.class);
                } else if (EntityLiving.class.isAssignableFrom(clazz) || IInventory.class.isAssignableFrom(clazz)) {
                    manager.registerEntityHandler("modEquippedDefault", clazz, TemplateRuleEntityLogic.class);
                }
            }
        }
    }

}
