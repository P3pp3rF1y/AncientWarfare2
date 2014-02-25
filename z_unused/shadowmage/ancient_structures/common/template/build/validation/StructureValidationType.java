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
package shadowmage.ancient_structures.common.template.build.validation;

import java.util.ArrayList;
import java.util.List;

public enum StructureValidationType
{
GROUND("ground", StructureValidatorGround.class),
UNDERGROUND("underground", StructureValidatorUnderground.class, new ValidationProperty("minGenDepth", "Min Generation Depth", int.class), new ValidationProperty("maxGenDepth", "Max Generation Depth", int.class), new ValidationProperty("minOverfill", "Min Overfill Depth", int.class)),
SKY("sky", StructureValidatorSky.class, new ValidationProperty("minGenHeight","Min Generation Height: ", int.class), new ValidationProperty("maxGenHeight","Max Generation Height: ", int.class), new ValidationProperty("minFlyingHeight", "Min Flying Height: ", int.class)),
WATER("water", StructureValidatorWater.class),
UNDERWATER("underwater", StructureValidatorUnderwater.class, new ValidationProperty("minWaterDepth","Min Water Depth: ", int.class), new ValidationProperty("maxWaterDepth","Max Water Depth: ", int.class)),
HARBOR("harbor", StructureValidatorHarbor.class), 
ISLAND("island", StructureValidatorIsland.class, new ValidationProperty("minWaterDepth","Min Water Dpth: ", int.class), new ValidationProperty("maxWaterDepth","Max Water Depth: ", int.class));

private String name;
private Class<? extends StructureValidator> validatorClass;
private List<ValidationProperty> props = new ArrayList<ValidationProperty>();

StructureValidationType(String name, Class<? extends StructureValidator> validatorClass, ValidationProperty...props)
  {
  this.name = name;
  this.validatorClass = validatorClass; 
  for(ValidationProperty prop : props)
    {
    this.props.add(prop);
    }
  }

public List<ValidationProperty> getValidationProperties()
  {
  return this.props;
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
