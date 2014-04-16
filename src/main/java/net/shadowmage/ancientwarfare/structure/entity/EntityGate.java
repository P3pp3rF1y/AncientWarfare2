/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.structure.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.gates.types.Gate;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

/**
 * an class to represent ALL gate types
 * @author Shadowmage
 *
 */
public class EntityGate extends Entity implements IEntityAdditionalSpawnData, IEntityPacketHandler
{

public BlockPosition pos1;
public BlockPosition pos2;

public float edgePosition;//the bottom/opening edge of the gate (closed should correspond to pos1)
public float edgeMax;//the 'fully extended' position of the gate

public float openingSpeed = 0.f;//calculated speed of the opening gate -- used during animation

Gate gateType = Gate.basicWood;

String ownerName;
int health = 0;
public int hurtAnimationTicks = 0;
byte gateStatus = 0;
public byte gateOrientation = 0;
public int hurtInvulTicks = 0;

boolean hasSetWorldEntityRadius = false;
public boolean wasPoweredA = false;
public boolean wasPoweredB = false;
/**
 * @param par1World
 */
public EntityGate(World par1World)
  {
  super(par1World);
  this.yOffset = 0;
  this.ignoreFrustumCheck = true;
  }

public void setOwnerName(String name)
  {
  this.ownerName = name;
  }

public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
  }

public Gate getGateType()
  {
  return this.gateType;
  }

public void setGateType(Gate type)
  {
  this.gateType = type;    
  }

@Override
protected void entityInit()
  {
  
  }

public void repackEntity()
  {
  if(worldObj.isRemote || isDead){return;}
  gateType.onGateStartOpen(this);//catch gates that have proxy blocks still in the world
  gateType.onGateStartClose(this);//
  ItemStack item = Gate.getItemToConstruct(this.gateType.getGlobalID());
  EntityItem entity = new EntityItem(worldObj);
  entity.setEntityItemStack(item);
  entity.setPosition(posX, posY+0.5d, posZ);
  this.worldObj.spawnEntityInWorld(entity);
  this.setDead();
  }

@Override
public void setDead()
  {
  super.setDead();
  if(!this.worldObj.isRemote)
    {
    //catch gates that have proxy blocks still in the world
    gateType.onGateStartOpen(this);
    gateType.onGateStartClose(this);
    }
  }

protected void setOpeningStatus(byte op)
  {
  this.gateStatus = op;  
  if(!this.worldObj.isRemote)
    {
    this.worldObj.setEntityState(this, op);
    }  
  if(op==-1)
    {
    this.gateType.onGateStartClose(this);
    }
  else if(op==1)
    {
    this.gateType.onGateStartOpen(this);
    }
  }

@Override
public int getBrightnessForRender(float par1)
  {
  int i = MathHelper.floor_double(this.posX);
  int j = MathHelper.floor_double(this.posZ);
  if (this.worldObj.blockExists(i, 0, j))
    {
    int k = MathHelper.floor_double(this.posY);
    return this.worldObj.getLightBrightnessForSkyBlocks(i, k, j, 0);
    }
  else
    {
    return 0;
    }
  }

@Override
public void handleHealthUpdate(byte par1)
  {  
  if(worldObj.isRemote)
    {
    if(par1==-1 || par1==0 || par1==1)
      {
      this.setOpeningStatus(par1);
      }
    }  
  super.handleHealthUpdate(par1);
  }

public byte getOpeningStatus()
  {
  return this.gateStatus;
  }

public int getHealth()
  {
  return this.health;
  }

public void setHealth(int val)
  {
  if(val<0)
    {
    val = 0;
    }
  if(val< health)
    {
    this.hurtAnimationTicks = 20;
    }
  if(val<health && !this.worldObj.isRemote)
    {
    PacketEntity pkt = new PacketEntity(this);
    pkt.packetData.setInteger("health", val);
    NetworkHandler.sendToAllTracking(this, pkt);
    }
  this.health = val;
  }

@Override
public void setPosition(double par1, double par3, double par5)
  {  
  this.posX = par1;
  this.posY = par3;
  this.posZ = par5;  
  if(this.gateType!=null)
    {
  	this.gateType.setCollisionBoundingBox(this);  
    }
  else
    {
    this.boundingBox.setBounds(par1, par3, par5, par1, par3, par5);
    }
  }

@Override
public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9)
  {
  this.setPosition(par1, par3, par5);
  this.setRotation(par7, par8);
  }

@Override
public boolean interactFirst(EntityPlayer par1EntityPlayer)
  {
  if(this.worldObj.isRemote)
    {
    return false;
    }
  boolean canInteract = par1EntityPlayer.getTeam()==this.getTeam();
  if(par1EntityPlayer.isSneaking() && canInteract)
    {
    NetworkHandler.INSTANCE.openGui(par1EntityPlayer, NetworkHandler.GUI_GATE_CONTROL, getEntityId(), 0, 0);
    }
  else if(canInteract)
    {
    this.activateGate();
    return true;
    }
  else
    {
    par1EntityPlayer.addChatMessage(new ChatComponentText("guistrings.gate.use_error"));
    }
  return false;
  }

public void activateGate()
  {
  if(this.gateStatus==1 && this.gateType.canActivate(this, false))
    {
    this.setOpeningStatus((byte) -1);
    }
  else if(this.gateStatus==-1 && this.gateType.canActivate(this, true))
    {
    this.setOpeningStatus((byte) 1);
    }
  else if(this.edgePosition == 0 && this.gateType.canActivate(this, true))
    {
    this.setOpeningStatus((byte)1);
    }
  else if(this.gateType.canActivate(this, false))//gate is already open/opening, set to closing
    {
    this.setOpeningStatus((byte)-1 );
    }
  }

@Override
public void onUpdate()
  {  
  super.onUpdate();  
  this.gateType.onUpdate(this);
  float prevEdge = this.edgePosition;
  this.setPosition(posX, posY, posZ);
  if(this.hurtInvulTicks>0)
    {
    this.hurtInvulTicks--;
    }
  this.checkForPowerUpdates();
  if(this.hurtAnimationTicks>0)
    {
    this.hurtAnimationTicks--;
    }
  if(this.gateStatus==1)
    {
    this.edgePosition += this.gateType.getMoveSpeed();
    if(this.edgePosition>=this.edgeMax)
      {
      this.edgePosition = this.edgeMax;
      this.gateStatus = 0;
      this.gateType.onGateFinishOpen(this);
      }    
    }
  else if(this.gateStatus==-1)
    {
    this.edgePosition -= this.gateType.getMoveSpeed();
    if(this.edgePosition<=0)
      {
      this.edgePosition = 0;
      this.gateStatus = 0;
      this.gateType.onGateFinishClose(this);
      }
    }
  this.openingSpeed = prevEdge - this.edgePosition;
  
  if(!hasSetWorldEntityRadius)
    {
    hasSetWorldEntityRadius = true;
    BlockPosition min = BlockTools.getMin(pos1, pos2);
    BlockPosition max = BlockTools.getMax(pos1, pos2);
    int xSize = max.x - min.x +1;
    int zSize = max.z - min.z +1;
    int ySize = max.y - min.y +1;
    int largest = xSize > ySize ? xSize : ySize;
    largest = largest > zSize ? largest : zSize;
    largest = (largest/2) + 1;
    if(World.MAX_ENTITY_RADIUS < largest)
      {
      World.MAX_ENTITY_RADIUS = largest;    
      }    
    }
  }

protected void checkForPowerUpdates()
  {
  if(this.worldObj.isRemote)
    {
    return;
    } 
  boolean foundPowerA = false;
  boolean foundPowerB = false;
  boolean activate = false;
  int y = pos1.y;
  y = pos2.y < y ? pos2.y : y;
  foundPowerA = this.worldObj.isBlockIndirectlyGettingPowered(pos1.x, y, pos1.z);
  foundPowerB = this.worldObj.isBlockIndirectlyGettingPowered(pos2.x, y, pos2.z);  
  if(foundPowerA && !wasPoweredA)
    {
    activate = true;
    }
  if(foundPowerB && !wasPoweredB)
    {
    activate = true;
    }
  this.wasPoweredA = foundPowerA;
  this.wasPoweredB = foundPowerB;
  if(activate)
    {
    this.activateGate();
    }
  }

@Override
public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
  {
  if(this.worldObj.isRemote)
    {
    return true;
    }
//  if(Config.gatesOnlyDamageByRams)
//    {
//    if(par1DamageSource.getEntity()==null || !(par1DamageSource.getEntity() instanceof VehicleBase))  
//      {
//      return !this.isDead;
//      }
//    VehicleBase vehicle = (VehicleBase) par1DamageSource.getEntity();
//    if(vehicle.vehicleType.getGlobalVehicleType()!=VehicleRegistry.BATTERING_RAM.getGlobalVehicleType())
//      {
//      return !this.isDead;
//      }
//    }
  if(this.hurtInvulTicks>0)
    {
    return !this.isDead;
    }
  this.hurtInvulTicks = 10;
  int health = this.getHealth();
  health -= par2;
  this.setHealth(health);
  
  if(health<=0)
    {
    this.setDead();
    }
  return !this.isDead;
  }

@Override
public boolean canBeCollidedWith()
  {
  return true;
  }

@Override
public boolean canBePushed()
  {
  return false;
  }

public String getTexture()
  {
  return "textures/" + "models/gate/"+gateType.getTexture();
  }

@Override
public float getShadowSize()
  {
  return 0.f;
  }

@Override
protected void readEntityFromNBT(NBTTagCompound tag)
  {
  this.pos1 = new BlockPosition(tag.getCompoundTag("pos1"));
  this.pos2 = new BlockPosition(tag.getCompoundTag("pos2"));
  this.setGateType(Gate.getGateByID(tag.getInteger("type")));
  this.ownerName = tag.getString("owner");
  this.edgePosition = tag.getFloat("edge");
  this.edgeMax = tag.getFloat("edgeMax");
  this.setHealth(tag.getInteger("health"));
  this.gateStatus = tag.getByte("status");
  this.gateOrientation = tag.getByte("orient");
  this.wasPoweredA = tag.getBoolean("power");
  this.wasPoweredB = tag.getBoolean("power2");
  }

@Override
protected void writeEntityToNBT(NBTTagCompound tag)
  {
  tag.setTag("pos1", pos1.writeToNBT(new NBTTagCompound()));
  tag.setTag("pos2", pos2.writeToNBT(new NBTTagCompound()));
  tag.setInteger("type", this.gateType.getGlobalID());
  tag.setString("owner", ownerName);
  tag.setFloat("edge", this.edgePosition);
  tag.setFloat("edgeMax", this.edgeMax);
  tag.setInteger("health", this.getHealth());
  tag.setByte("status", this.gateStatus);
  tag.setByte("orient", gateOrientation);
  tag.setBoolean("power", this.wasPoweredA);
  tag.setBoolean("power2", this.wasPoweredB);
  }

@Override
public void writeSpawnData(ByteBuf data)
  {
  data.writeInt(pos1.x);
  data.writeInt(pos1.y);
  data.writeInt(pos1.z);
  data.writeInt(pos2.x);
  data.writeInt(pos2.y);
  data.writeInt(pos2.z);
  data.writeInt(this.gateType.getGlobalID());  
  data.writeFloat(this.edgePosition);
  data.writeFloat(this.edgeMax);
  data.writeByte(this.gateStatus);
  data.writeByte(this.gateOrientation);
  data.writeInt(health);
  }

@Override
public void readSpawnData(ByteBuf data)
  {
  this.pos1 = new BlockPosition(data.readInt(), data.readInt(), data.readInt());
  this.pos2 = new BlockPosition(data.readInt(), data.readInt(), data.readInt());
  this.gateType = Gate.getGateByID(data.readInt());
  this.edgePosition = data.readFloat();
  this.edgeMax = data.readFloat();
  this.gateStatus = data.readByte();
  this.gateOrientation = data.readByte();
  this.health = data.readInt();
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("health"))
    {
    this.health = tag.getInteger("health");
    this.hurtAnimationTicks = 20;
    }  
  }

}
