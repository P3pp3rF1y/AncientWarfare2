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
package net.shadowmage.ancientwarfare.structure.template.scan;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class TemplateScanner {

    /**
     * @param turns # of turns for proper orientation
     */
    @SuppressWarnings("unchecked")
    public static StructureTemplate scan(World world, BlockPos min, BlockPos max, BlockPos key, int turns, String name) {
        int xSize = max.x - min.x + 1;
        int ySize = max.y - min.y + 1;
        int zSize = max.z - min.z + 1;

        int xOutSize = xSize, zOutSize = zSize;
        int swap;
        for (int i = 0; i < turns; i++) {
            swap = xOutSize;
            xOutSize = zOutSize;
            zOutSize = swap;
        }
        key = BlockTools.rotateInArea(key.sub(min), xSize, zSize, turns);

        short[] templateRuleData = new short[xSize * ySize * zSize];


        HashMap<String, List<TemplateRuleBlock>> pluginBlockRuleMap = new HashMap<String, List<TemplateRuleBlock>>();
        List<TemplateRule> currentRulesAll = new ArrayList<TemplateRule>();
        Block scannedBlock;
        TemplateRuleBlock scannedBlockRule = null;
        List<TemplateRuleBlock> pluginBlockRules;
        String pluginId;
        int index;
        int meta;
        int scanX, scanZ, scanY;
        BlockPos destination = new BlockPos();
        int nextRuleID = 1;
        for (scanY = min.y; scanY <= max.y; scanY++) {
            for (scanZ = min.z; scanZ <= max.z; scanZ++) {
                for (scanX = min.x; scanX <= max.x; scanX++) {
                    destination = BlockTools.rotateInArea(new BlockPos(scanX, scanY, scanZ).sub(min), xSize, zSize, turns);

                    scannedBlock = world.getBlock(scanX, scanY, scanZ);

                    if (scannedBlock != null && !AWStructureStatics.shouldSkipScan(scannedBlock) && !scannedBlock.isAir(world, scanX, scanY, scanZ)) {
                        pluginId = StructurePluginManager.INSTANCE.getPluginNameFor(scannedBlock);
                        if (pluginId != null) {
                            meta = world.getBlockMetadata(scanX, scanY, scanZ);
                            pluginBlockRules = pluginBlockRuleMap.get(pluginId);
                            if (pluginBlockRules == null) {
                                pluginBlockRules = new ArrayList<TemplateRuleBlock>();
                                pluginBlockRuleMap.put(pluginId, pluginBlockRules);
                            }
                            boolean found = false;
                            for (TemplateRuleBlock rule : pluginBlockRules) {
                                if (rule.shouldReuseRule(world, scannedBlock, meta, turns, scanX, scanY, scanZ)) {
                                    scannedBlockRule = rule;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                scannedBlockRule = StructurePluginManager.INSTANCE.getRuleForBlock(world, scannedBlock, turns, scanX, scanY, scanZ);
                                if(scannedBlockRule!=null) {
                                    scannedBlockRule.ruleNumber = nextRuleID;
                                    nextRuleID++;
                                    pluginBlockRules.add(scannedBlockRule);
                                    currentRulesAll.add(scannedBlockRule);
                                }
                            }
                            index = StructureTemplate.getIndex(destination.x, destination.y, destination.z, xOutSize, ySize, zOutSize);
                            templateRuleData[index] = (short) scannedBlockRule.ruleNumber;
                        }
                    }
                }//end scan x-level for
            }//end scan z-level for
        }//end scan y-level for

        List<TemplateRuleEntity> scannedEntityRules = new ArrayList<TemplateRuleEntity>();
        List<Entity> entitiesInAABB = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1));
        nextRuleID = 0;
        for (Entity e : entitiesInAABB) {
            int ex = MathHelper.floor_double(e.posX);
            int ey = MathHelper.floor_double(e.posY);
            int ez = MathHelper.floor_double(e.posZ);
            TemplateRuleEntity scannedEntityRule = StructurePluginManager.INSTANCE.getRuleForEntity(world, e, turns, ex, ey, ez);
            if (scannedEntityRule != null) {
                destination = BlockTools.rotateInArea(new BlockPos(ex, ey, ez).sub(min), xSize, zSize, turns);
                scannedEntityRule.ruleNumber = nextRuleID;
                scannedEntityRule.setPosition(destination);
                scannedEntityRules.add(scannedEntityRule);
                nextRuleID++;
            }
        }

        TemplateRule[] templateRules = new TemplateRule[currentRulesAll.size() + 1];
        for (int i = 0; i < currentRulesAll.size(); i++)//offset by 1 -- we want a null rule for 0==air
        {
            templateRules[i + 1] = currentRulesAll.get(i);
        }

        TemplateRuleEntity[] entityRules = new TemplateRuleEntity[scannedEntityRules.size()];
        for (int i = 0; i < scannedEntityRules.size(); i++) {
            entityRules[i] = scannedEntityRules.get(i);
        }

        StructureTemplate template = new StructureTemplate(name, xOutSize, ySize, zOutSize, key.x, key.y, key.z);
        template.setTemplateData(templateRuleData);
        template.setRuleArray(templateRules);
        template.setEntityRules(entityRules);
        return template;
    }

}
