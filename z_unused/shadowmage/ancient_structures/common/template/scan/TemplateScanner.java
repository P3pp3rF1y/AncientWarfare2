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
package shadowmage.ancient_structures.common.template.scan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.BlockPosition;
import shadowmage.ancient_framework.common.utils.BlockTools;
import shadowmage.ancient_structures.AWStructures;
import shadowmage.ancient_structures.common.template.StructureTemplate;
import shadowmage.ancient_structures.common.template.rule.TemplateRule;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleBlock;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleEntity;

public class TemplateScanner
{

TemplateRule[] rules;
    
public TemplateScanner()
  {
  
  }

/**
 * 
 * @param world
 * @param min
 * @param max
 * @param key 
 * @param turns # of turns for proper orientation
 * @return
 */
public StructureTemplate scan(World world, BlockPosition min, BlockPosition max, BlockPosition key, int turns, String name)
  {
  int xSize = max.x - min.x+1;
  int ySize = max.y - min.y+1;
  int zSize = max.z - min.z+1;
  
  int xOutSize, zOutSize;
  xOutSize = xSize;
  zOutSize = zSize;
    
  int swap;
  for(int i = 0; i < turns; i++)
    {
    swap = xOutSize;
    xOutSize = zOutSize;
    zOutSize = swap;
    }    
    
  key.x = key.x - min.x;
  key.y = key.y - min.y;
  key.z = key.z - min.z; 
    
  short[] templateRuleData = new short[xSize*ySize*zSize];
  BlockTools.rotateInArea(key, xSize, zSize, turns);      
  HashMap<String, List<TemplateRuleBlock>> pluginBlockRuleMap = new HashMap<String, List<TemplateRuleBlock>>();  
  List<TemplateRule> currentRulesAll = new ArrayList<TemplateRule>();
  Block scannedBlock;
  TemplateRuleBlock scannedBlockRule = null;    
  List<TemplateRuleBlock> pluginBlockRules;  
  String pluginId;
  int index;
  int meta;
  TileEntity te;
  int scanX, scanZ, scanY;
  BlockPosition destination = new BlockPosition();
  int nextRuleID = 1;
  for(scanY = min.y; scanY<=max.y; scanY++)  
    {
    for(scanZ = min.z; scanZ<=max.z; scanZ++)
      {
      for(scanX = min.x; scanX<=max.x; scanX++)
        {
        destination.x = scanX - min.x;
        destination.y = scanY - min.y; 
        destination.z = scanZ - min.z;
        BlockTools.rotateInArea(destination, xSize, zSize, turns);
        scannedBlock = Block.blocksList[world.getBlockId(scanX, scanY, scanZ)];
        if(scannedBlock!=null)
          {   
          pluginId = AWStructures.instance.pluginManager.getPluginNameFor(scannedBlock);
          if(pluginId!=null)
            {
            meta = world.getBlockMetadata(scanX, scanY, scanZ);
            te = world.getBlockTileEntity(scanX, scanY, scanZ);
            pluginBlockRules = pluginBlockRuleMap.get(pluginId);
            if(pluginBlockRules==null)
              {
              pluginBlockRules = new ArrayList<TemplateRuleBlock>();
              pluginBlockRuleMap.put(pluginId, pluginBlockRules);
              }
            boolean found = false;
            for(TemplateRuleBlock rule : pluginBlockRules)
              {
              if(rule.shouldReuseRule(world, scannedBlock, meta, turns, te, scanX, scanY, scanZ))
                {
                scannedBlockRule = rule;
                found = true;
                break;
                }
              }
            if(!found)
              {
              scannedBlockRule = (TemplateRuleBlock) AWStructures.instance.pluginManager.getRuleForBlock(world, scannedBlock, turns, scanX, scanY, scanZ);
              scannedBlockRule.ruleNumber = nextRuleID;
              nextRuleID++;
              pluginBlockRules.add(scannedBlockRule);
              currentRulesAll.add(scannedBlockRule);
              }
            index = StructureTemplate.getIndex(destination.x, destination.y, destination.z, xOutSize, ySize, zOutSize);
            templateRuleData[index] = (short) scannedBlockRule.ruleNumber;
            }
          }
        else
          {
          }//end entity-scan else block
        }//end scan x-level for
      }
    }  
  

  TemplateRuleEntity scannedEntityRule;
  List<TemplateRuleEntity> scannedEntityRules = new ArrayList<TemplateRuleEntity>();
  int ex, ey, ez; 
  List<Entity> entitiesInAABB = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1));          
  String entityPluginId = null; 
  nextRuleID = 0;
  for(Entity e : entitiesInAABB)
    {
    ex = MathHelper.floor_double(e.posX);
    ey = MathHelper.floor_double(e.posY);
    ez = MathHelper.floor_double(e.posZ);
    entityPluginId = AWStructures.instance.pluginManager.getPluginNameForEntity(e.getClass());
    if(entityPluginId==null){continue;}
    scannedEntityRule = AWStructures.instance.pluginManager.getRuleForEntity(world, e, turns, ex, ey, ez);
    if(scannedEntityRule!=null)
      {
      destination.x = ex - min.x;
      destination.y = ey - min.y; 
      destination.z = ez - min.z;
      BlockTools.rotateInArea(destination, xSize, zSize, turns);
      scannedEntityRule.ruleNumber = nextRuleID;
      scannedEntityRule.x = destination.x;
      scannedEntityRule.y = destination.y;
      scannedEntityRule.z = destination.z;  
      scannedEntityRules.add(scannedEntityRule);          
      nextRuleID++;
      }
    }
  
  TemplateRule[] templateRules = new TemplateRule[currentRulesAll.size()+1];
  for(int i = 0; i < currentRulesAll.size(); i++)//offset by 1 -- we want a null rule for 0==air
    {
    templateRules[i+1] = currentRulesAll.get(i);
    }
  
  TemplateRuleEntity[] entityRules = new TemplateRuleEntity[scannedEntityRules.size()];
  for(int i = 0; i < scannedEntityRules.size(); i++)
    {
    entityRules[i] = scannedEntityRules.get(i);
    }
    
  StructureTemplate template = new StructureTemplate(name, xOutSize, ySize, zOutSize, key.x, key.y, key.z);
  template.setTemplateData(templateRuleData);
  template.setRuleArray(templateRules);  
  template.setEntityRules(entityRules);
  return template;
  }

}
