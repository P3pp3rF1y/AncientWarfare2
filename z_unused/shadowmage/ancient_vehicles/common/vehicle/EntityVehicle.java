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

import shadowmage.ancient_framework.common.interfaces.IEntityPacketHandler;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityVehicle extends Entity implements IEntityAdditionalSpawnData, IEntityPacketHandler
{

private VehicleType vehicleType;
private VehicleFiringHelper firingHelper;
private VehicleMoveHelper moveHelper;
private VehicleStats vehicleStats;

public EntityVehicle(World par1World)
  {
  super(par1World);
  this.vehicleStats = new VehicleStats(this);
  }

public EntityVehicle setVehicleType(VehicleType type)
  {
  this.vehicleType = type;
  return this;
  }

public EntityVehicle setFiringHelper(VehicleFiringHelper firingHelper)
  {
  this.firingHelper = firingHelper;
  return this;
  }

public EntityVehicle setMoveHelper(VehicleMoveHelper helper)
  {
  this.moveHelper = helper;
  return this;
  }

public VehicleType getVehicleType(){return this.vehicleType;}
public VehicleMoveHelper getMoveHelper(){return moveHelper;}
public VehicleFiringHelper getFiringHelper(){return firingHelper;}
public VehicleStats getVehicleStats(){return this.vehicleStats;}

@Override
public void onUpdate()
  {    
  super.onUpdate();
  this.firingHelper.onUpdate();
  this.moveHelper.onUpdate();
  }

@Override
protected void entityInit()
  {

  }

@Override
public void updateRiderPosition()
  {
  this.moveHelper.updateRiderPosition();
  }

@Override
protected void readEntityFromNBT(NBTTagCompound tag)
  {
  String typeName = tag.getString("vehicleType");
  this.setVehicleType(VehicleType.getVehicleType(typeName));
  this.setFiringHelper(this.getVehicleType().getNewFiringHelper(this));
  NBTTagCompound firingVars = tag.getCompoundTag("firingVars");
  //TODO have firing helper read from nbt
  //TODO finish reading vehicle from nbt -- e.g. vehicleStats
  }

@Override
protected void writeEntityToNBT(NBTTagCompound tag)
  {
  tag.setString("vehicleType", this.getVehicleType().getName());
  tag.setCompoundTag("firingVars", null);//TODO this will crash...blah..need to make firingVars class
  //TODO have firing helper write out to NBT
  }

@Override
public void writeSpawnData(ByteArrayDataOutput data)
  {
  //TODO create an NBTTag of spawn data
  //write that tag onto stream
  }

@Override
public void readSpawnData(ByteArrayDataInput data)
  {
  //read nbt tag from stream
  //read vars from tag...will need to mimic readEntityFromNBT
  }

@Override
public void onPacketDataReceived(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  
  }

}
