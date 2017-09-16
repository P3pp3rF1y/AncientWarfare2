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
package net.shadowmage.ancientwarfare.structure.template.scan;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    /*
     * @param turns # of turns for proper orientation
     */

    public static StructureTemplate scan(World world, BlockPos min, BlockPos max, BlockPos key, int turns, String name) {
        int xSize = max.getX() - min.getX() + 1;
        int ySize = max.getY() - min.getY() + 1;
        int zSize = max.getZ() - min.getZ() + 1;

        int xOutSize = xSize, zOutSize = zSize;
        int swap;
        for (int i = 0; i < turns; i++) {
            swap = xOutSize;
            xOutSize = zOutSize;
            zOutSize = swap;
        }
        key = BlockTools.rotateInArea(key.subtract(min), xSize, zSize, turns);

        short[] templateRuleData = new short[xSize * ySize * zSize];


        HashMap<String, List<TemplateRuleBlock>> pluginBlockRuleMap = new HashMap<>();
        List<TemplateRule> currentRulesAll = new ArrayList<>();
        Block scannedBlock;
        TemplateRuleBlock scannedBlockRule = null;
        List<TemplateRuleBlock> pluginBlockRules;
        String pluginId;
        int index;
        int meta;
        int scanX, scanZ, scanY;
        int nextRuleID = 1;
        BlockPos destination;
        for (scanY = min.getY(); scanY <= max.getY(); scanY++) {
            for (scanZ = min.getZ(); scanZ <= max.getZ(); scanZ++) {
                for (scanX = min.getX(); scanX <= max.getX(); scanX++) {
                    destination = BlockTools.rotateInArea(new BlockPos(scanX, scanY, scanZ).subtract(min), xSize, zSize, turns);

                    BlockPos scannedPos = new BlockPos(scanX, scanY, scanZ);
                    IBlockState scannedState = world.getBlockState(scannedPos);
                    scannedBlock = scannedState.getBlock();

                    if (scannedBlock != null && !AWStructureStatics.shouldSkipScan(scannedBlock) && !world.isAirBlock(scannedPos)) {
                        pluginId = StructurePluginManager.INSTANCE.getPluginNameFor(scannedBlock);
                        if (pluginId != null) {
                            meta = scannedBlock.getMetaFromState(scannedState);
                            pluginBlockRules = pluginBlockRuleMap.get(pluginId);
                            if (pluginBlockRules == null) {
                                pluginBlockRules = new ArrayList<>();
                                pluginBlockRuleMap.put(pluginId, pluginBlockRules);
                            }
                            boolean found = false;
                            for (TemplateRuleBlock rule : pluginBlockRules) {
                                if (rule.shouldReuseRule(world, scannedBlock, meta, turns, scannedPos)) {
                                    scannedBlockRule = rule;
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                scannedBlockRule = StructurePluginManager.INSTANCE.getRuleForBlock(world, scannedBlock, turns, scannedPos);
                                if(scannedBlockRule!=null) {
                                    scannedBlockRule.ruleNumber = nextRuleID;
                                    nextRuleID++;
                                    pluginBlockRules.add(scannedBlockRule);
                                    currentRulesAll.add(scannedBlockRule);
                                }
                            }
                            index = StructureTemplate.getIndex(destination.getX(), destination.getY(), destination.getZ(), xOutSize, ySize, zOutSize);
                            templateRuleData[index] = (short) scannedBlockRule.ruleNumber;
                        }
                    }
                }//end scan x-level for
            }//end scan z-level for
        }//end scan y-level for

        List<TemplateRuleEntity> scannedEntityRules = new ArrayList<>();
        List<Entity> entitiesInAABB = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1));
        nextRuleID = 0;
        for (Entity e : entitiesInAABB) {
            int ex = MathHelper.floor(e.posX);
            int ey = MathHelper.floor(e.posY);
            int ez = MathHelper.floor(e.posZ);
            TemplateRuleEntity scannedEntityRule = StructurePluginManager.INSTANCE.getRuleForEntity(world, e, turns, ex, ey, ez);
            if (scannedEntityRule != null) {
                destination = BlockTools.rotateInArea(new BlockPos(ex, ey, ez).subtract(min), xSize, zSize, turns);
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

        StructureTemplate template = new StructureTemplate(name, xOutSize, ySize, zOutSize, key.getX(), key.getY(), key.getZ());
        template.setTemplateData(templateRuleData);
        template.setRuleArray(templateRules);
        template.setEntityRules(entityRules);
        return template;
    }

}
