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
package net.shadowmage.ancientwarfare.structure.template;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRule;
import net.shadowmage.ancientwarfare.structure.template.rule.TemplateRuleEntity;



public class StructureTemplate
{

/**
 * base datas
 */
public final String name;
public final int xSize, ySize, zSize;
public final int xOffset, yOffset, zOffset;

/**
 * stored template data
 */
private TemplateRule[] templateRules;
private TemplateRuleEntity[] entityRules;
private short[] templateData;
List<ItemStack> resourceList;

/**
 * world generation placement validation settings
 */
private StructureValidator validator;

public StructureTemplate(String name, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset)
  {
  if(name==null){throw new IllegalArgumentException("cannot have null name for structure");}
  this.name = name;
  this.xSize = xSize;
  this.ySize = ySize;
  this.zSize = zSize;
  this.xOffset = xOffset;
  this.yOffset = yOffset;
  this.zOffset = zOffset;  
  }

public TemplateRuleEntity[] getEntityRules()
  {
  return entityRules;
  }

public TemplateRule[] getTemplateRules()
  {
  return templateRules;
  }

public short[] getTemplateData()
  {
  return templateData;
  }

public StructureValidator getValidationSettings()
  {
  return validator;
  }

public void setRuleArray(TemplateRule[] rules)
  {
  this.templateRules = rules;
  }

public void setEntityRules(TemplateRuleEntity[] rules)
  {
  this.entityRules = rules;
  }

public void setTemplateData(short[] datas)
  {
  this.templateData = datas;
  }

public void setValidationSettings(StructureValidator settings)
  {
  this.validator = settings;
  }

public TemplateRule getRuleAt(int x, int y, int z)
  {
  int index = getIndex(x, y, z, xSize, ySize, zSize);
  int ruleIndex = index >=0 && index < templateData.length ? templateData[index]: -1;
  return ruleIndex >= 0 && ruleIndex < templateRules.length ? templateRules[ruleIndex] : null;
  }

public static int getIndex(int x, int y, int z, int xSize, int ySize, int zSize)
  {
  return (y * xSize * zSize) + (z * xSize) + x; 
  }

@Override
public String toString()
  {
  StringBuilder b = new StringBuilder();
  b.append("name: ").append(name).append("\n");
  b.append("size: ").append(xSize).append(", ").append(ySize).append(", ").append(zSize).append("\n");
  b.append("buildKey: ").append(xOffset).append(", ").append(yOffset).append(", ").append(zOffset);
  return b.toString();
  }

public List<ItemStack> getResourceList()
  {
  if(resourceList==null)
    {
    TemplateRule rule;
    List<ItemStack> stacks = new ArrayList<ItemStack>();
    for(int x = 0; x < this.xSize; x++)
      {
      for(int y = 0; y < this.ySize; y++)
        {
        for(int z = 0; z < this.zSize; z++)
          {
          rule = getRuleAt(x, y, z);
          if(rule!=null)
            {
            rule.addResources(stacks);
            }
          }
        }
      }
    compactStackList(stacks);
    resourceList = new ArrayList<ItemStack>();
    resourceList.addAll(stacks);
    }
  return resourceList;
  }

private void compactStackList(List<ItemStack> stacks)
  {
  List<ItemStack> out = new ArrayList<ItemStack>();
  Item item;
  int dmg;
  int transfer;
  for(ItemStack inStack : stacks)
    {
    item = inStack.getItem();
    dmg = inStack.getItemDamage();
    for(ItemStack outStack : out)
      {
      if(outStack.stackSize < outStack.getMaxStackSize() && item==outStack.getItem() && dmg==outStack.getItemDamage() && ItemStack.areItemStackTagsEqual(inStack, outStack))
        {
        transfer = inStack.stackSize;
        transfer = transfer + outStack.stackSize > outStack.getMaxStackSize() ? outStack.getMaxStackSize()-outStack.stackSize: transfer;
        inStack.stackSize-=transfer;
        outStack.stackSize+=transfer;        
        if(inStack.stackSize<=0)
          {
          break;//break outStack iterator loop, as inStack has been used up
          }
        }
      }        
    if(inStack.stackSize>0)
      {
      out.add(new ItemStack(item, inStack.stackSize, dmg));
      }
    }  
  stacks.clear();
  stacks.addAll(out);  
  }

}
