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
package net.shadowmage.ancientwarfare.structure.template.build.validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public enum StructureValidationType
{
GROUND("ground", StructureValidatorGround.class),
UNDERGROUND("underground", StructureValidatorUnderground.class, new StructureValidationProperty("minGenDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxGenDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("minOverfill", StructureValidationProperty.DATA_TYPE_INT, 0)),
SKY("sky", StructureValidatorSky.class, new StructureValidationProperty("minGenHeight", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxGenHeight", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("minFlyingHeight", StructureValidationProperty.DATA_TYPE_INT, 0)),
WATER("water", StructureValidatorWater.class),
UNDERWATER("underwater", StructureValidatorUnderwater.class, new StructureValidationProperty("minWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0)),
HARBOR("harbor", StructureValidatorHarbor.class), 
ISLAND("island", StructureValidatorIsland.class, new StructureValidationProperty("minWaterDepth",StructureValidationProperty.DATA_TYPE_INT, 0), new StructureValidationProperty("maxWaterDepth", StructureValidationProperty.DATA_TYPE_INT, 0));

private String name;
private Class<? extends StructureValidator> validatorClass;

private List<StructureValidationProperty> properties = new ArrayList<StructureValidationProperty>(); 

StructureValidationType(String name, Class<? extends StructureValidator> validatorClass, StructureValidationProperty...props)
  {
  this.name = name;
  this.validatorClass = validatorClass; 

  properties.add(new StructureValidationProperty("survival", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
  properties.add(new StructureValidationProperty("enableWorldGen", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
  properties.add(new StructureValidationProperty("unique", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
  properties.add(new StructureValidationProperty("preserveBlocks", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
  
  properties.add(new StructureValidationProperty("selectionWeight", StructureValidationProperty.DATA_TYPE_INT, 0));
  properties.add(new StructureValidationProperty("clusterValue", StructureValidationProperty.DATA_TYPE_INT, 0));
  properties.add(new StructureValidationProperty("minDuplicateDistance", StructureValidationProperty.DATA_TYPE_INT, 0));
  properties.add(new StructureValidationProperty("borderSize", StructureValidationProperty.DATA_TYPE_INT, 0));
  properties.add(new StructureValidationProperty("maxLeveling", StructureValidationProperty.DATA_TYPE_INT, 0));
  properties.add(new StructureValidationProperty("maxFill", StructureValidationProperty.DATA_TYPE_INT, 0));
  
  properties.add(new StructureValidationProperty("biomeWhiteList", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
  properties.add(new StructureValidationProperty("dimensionWhiteList", StructureValidationProperty.DATA_TYPE_BOOLEAN, false));
    
  properties.add(new StructureValidationProperty("biomeList", StructureValidationProperty.DATA_TYPE_STRING_SET, new HashSet<String>()));
  properties.add(new StructureValidationProperty("blockList", StructureValidationProperty.DATA_TYPE_STRING_SET, new HashSet<String>()));
  properties.add(new StructureValidationProperty("dimensionList", StructureValidationProperty.DATA_TYPE_INT_ARRAY, new int[]{}));
  for(StructureValidationProperty prop : props)
    {
    properties.add(prop);
    }
  }

public List<StructureValidationProperty> getValidationProperties()
  {
  return this.properties;
  }

public String getName()
  {
  return name;
  }

public StructureValidator getValidator()
  {
  try
    {
    return validatorClass.newInstance();
    } 
  catch (InstantiationException e)
    {
    e.printStackTrace();
    } 
  catch (IllegalAccessException e)
    {
    e.printStackTrace();
    }
  return null;
  }

public static StructureValidationType getTypeFromName(String name)
  {
  if(name==null){return null;}
  name = name.toLowerCase();
  if(name.equals(GROUND.name)){return GROUND;}
  else if(name.equals(UNDERGROUND.name)){return UNDERGROUND;}
  else if(name.equals(SKY.name)){return SKY;}
  else if(name.equals(WATER.name)){return WATER;}
  else if(name.equals(UNDERWATER.name)){return UNDERWATER;}
  else if(name.equals(HARBOR.name)){return HARBOR;}
  else if(name.equals(ISLAND.name)){return ISLAND;}
  return null;
  }

/**
 * validation types:
 * ground:
 *    validate border edge blocks for depth and leveling
 *    validate border target blocks
 * 
 * underground:
 *    validate min/max overfill height is met
 *    validate border target blocks
 *    
 * water:
 *    validate water depth along edges
 *    
 * underwater:
 *    validate min/max water depth at placement x/z
 *    validate border edge blocks for depth and leveling
 *    
 * sky:
 *    validate min flying height along edges
 * 
 * harbor:
 *    validate edges--front all land, sides land/water, back all water. validate edge-depth and leveling *    
 * 
 * island:
 *    validate min/max water depth at placement x/z
 *    validate border edge blocks for depth and leveling
 *   
 */

public static class ValidationProperty
{
public String displayName;
public String propertyName;
public Class clz;//property class -- boolean or int for most

public ValidationProperty(String reg, String display, Class clz)
  {
  this.propertyName = reg;
  this.displayName = display;
  this.clz = clz;
  }
}

}
