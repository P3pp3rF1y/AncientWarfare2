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
package shadowmage.ancient_vehicles.common.vehicle;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import shadowmage.ancient_framework.common.utils.StringTools;

public class VehicleType
{

public static final HashMap<String, VehicleType> vehicleTypesByName = new HashMap<String, VehicleType>();
public static final HashMap<Integer, VehicleType> vehicleTypeByID = new HashMap<Integer, VehicleType>();

protected ItemStack vehicleItem;
/**
 * basic variables / helpers
 */
String name;//translation key/unique registered name for this vehicle type
int vehicleId;//global type-number ID used for item-stack identification -- must be checked on client login to verify it matches server configs
/**
 * client-side rendering information
 */
String modelId;//used by rendering to select the proper model
String renderId;//used by rendering to..well..render
String modelTexture;//used by rendering to bind the proper texture

Class<? extends VehicleFiringHelper> firingHelper;//reference to the firing helper type for this vehicle, for constructing new vehicle entities
Object movementType;//enum / flag for movement type for this vehicle

boolean survival;//available in survival through crafting (forces creative=true)
boolean creative;//available in creative in menu for ops/admins/creative play (loaded, but no recipe)

/**
 * base stats for this vehicle
 */
int maxHealth;//max health for this vehicle type
float mass;//un-adjusted mass for this vehicle
float thrust;//used with mass to determine acceleration/handling for this vehicle
float firePower;//used with ammo mass to determine final projectile velocity

float width;//used for collision detection
float height;//used for collision detection
float length;//used for collision detection

float turretX;//used to offset rider and initial projectile transform
float turretY;//used to offset rider and initial projectile transform
float turretZ;//used to offset rider and initial projectile transform

float projectileX;//used for final projectile transform
float projectileY;//used for final projectile transform
float projectileZ;//used for final projectile transform

float riderX;//rider offset from either vehicle origin or turret origin
float riderY;//rider offset from either vehicle origin or turret origin
float riderZ;//rider offset from either vehicle origin or turret origin
boolean riderMovesWithTurret;//determines if should use turret origin or vehicle origin for rider offset

float flagX;//offset of team-flag from vehicle origin
float flagY;
float flagZ;
boolean flagMovesWithTurret;

/**
 * loaded from separate csvs
 */
List<String> tooltips;
Set<Integer> researchGoals;
Set<String> upgrades;
Set<String> ammos;

public VehicleType(String name, int id)
  {
  this.name = name;
  this.vehicleId = id;
  vehicleTypesByName.put(name, this);
  vehicleTypeByID.put(id, this);
  tooltips = new ArrayList<String>();
  researchGoals = new HashSet<Integer>();
  upgrades = new HashSet<String>();
  ammos = new HashSet<String>();
  }

public static final VehicleType getVehicleType(String name)
  {
  return vehicleTypesByName.get(name);
  }

public String getName(){return name;}
public int getId(){return vehicleId;}
public boolean isSuvivalEnabled(){return survival;}
public boolean isCreativeEnabled(){return creative;}
public String getTextureName(){return modelTexture;}
public String getModelId(){return modelId;}
public String getRenderId(){return renderId;}

public VehicleFiringHelper getNewFiringHelper(EntityVehicle vehicle)
  {
  try
    {
    return firingHelper.getDeclaredConstructor(EntityVehicle.class).newInstance(vehicle);
    } 
  catch (InstantiationException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 
  catch (IllegalAccessException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 
  catch (IllegalArgumentException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 
  catch (InvocationTargetException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 
  catch (NoSuchMethodException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    } 
  catch (SecurityException e)
    {
    // TODO Auto-generated catch block
    e.printStackTrace();
    }
  return null;
  }

public static EntityVehicle createVehicle(World world, String typeName)
  {
  VehicleType type = VehicleType.getVehicleType(typeName);  
  EntityVehicle entity = new EntityVehicle(world).setVehicleType(type);
  entity.setFiringHelper(type.getNewFiringHelper(entity));
  return entity;
  }

public static VehicleType parseFromCSV(String[] csv)
  {
  int csvLen = 30;
  if(csv==null || csv.length<csvLen)
    {
    throw new IllegalArgumentException("Could not parse vehicle type from:\n+"+csv+"\ndid not contain enough elements to parse");
    }
  VehicleType type = null;
  type = new VehicleType(csv[0].trim(), StringTools.safeParseInt(csv[1].trim()));
  type.modelId = csv[2].trim();
  type.renderId = csv[3].trim();
  type.modelTexture = csv[4].trim();
  type.firingHelper = VehicleRegistry.getFiringHelperClass(csv[5].trim());
  type.movementType = VehicleRegistry.getMoveType(csv[6].trim());
  type.survival = StringTools.safeParseBoolean(csv[7].trim());
  type.creative = StringTools.safeParseBoolean(csv[8].trim());  
  type.maxHealth = StringTools.safeParseInt(csv[9].trim());
  type.mass = StringTools.safeParseInt(csv[10].trim());
  type.thrust = StringTools.safeParseInt(csv[11].trim());
  type.firePower = StringTools.safeParseInt(csv[12].trim());
  /**
   * TODO read the rest of vehicle-type stats (need to determine what the stats stored in csv will be)
   */
  return type;
  }

public void parseTooltips(String[] csv)
  {
  for(int i = 1; i < csv.length; i++)//start at 1 to skip vehicle name
    {
    if(!csv[i].isEmpty())
      {
      tooltips.add(csv[i]);      
      }
    }
  }

public void parseResearch(String[] csv)
  {
  for(int i = 1; i < csv.length; i++)//start at 1 to skip vehicle name
    {
    if(!csv[i].isEmpty())
      {
      researchGoals.add(StringTools.safeParseInt(csv[i]));      
      }
    }
  }

public void parseUpgrades(String[] csv)
  {
  for(int i = 1; i < csv.length; i++)//start at 1 to skip vehicle name
    {
    if(!csv[i].isEmpty())
      {
      upgrades.add(csv[i]);      
      }
    }
  }

public void parseAmmoTypes(String[] csv)
  {
  for(int i = 1; i < csv.length; i++)//start at 1 to skip vehicle name
    {
    if(!csv[i].isEmpty())
      {
      ammos.add(csv[i]);      
      }
    }
  }

}
