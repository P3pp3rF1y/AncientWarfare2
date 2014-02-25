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
package shadowmage.ancient_structures.common.template;

import shadowmage.ancient_structures.common.template.build.validation.StructureValidator;
import shadowmage.ancient_structures.common.template.rule.TemplateRule;
import shadowmage.ancient_structures.common.template.rule.TemplateRuleEntity;


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



}
