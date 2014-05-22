package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;

public class TileTorqueGeneratorWaterwheel extends TileEntity implements ITorqueGenerator, IInteractableTile
{

public float rotationAngle;
public float rotationSpeed;
public static double maxEnergy = 100;
public static double maxOutput = 10;
private double storedEnergy;
private int updateTick;
protected TileEntity[] neighborTileCache = null;

@Override
public void updateEntity()
  {
  rotationAngle+=rotationSpeed*10.f;
  if(worldObj.isRemote){return;}
  if(storedEnergy>1.f)
    {
    ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
    }
  updateTick++;
  if(updateTick<20){return;}
  updateTick=0;  
  ForgeDirection face = ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  int x = xCoord+face.offsetX, y = yCoord+face.offsetY, z = zCoord+face.offsetZ;  
  Block blockMid = worldObj.getBlock(x, y, z);
  int metaMid, metaRight, metaLeft;
  float mid, right, left;
  int leftY = yCoord, rightY = yCoord, midY = yCoord;
  Block blockRight, blockLeft;
  boolean flowsLeft;
  if(blockMid.getMaterial()==Material.water)
    {
    metaMid = worldObj.getBlockMetadata(x, y, z);
    mid = 1.f-BlockLiquid.getLiquidHeightPercent(metaMid);
    
    
    face = face==ForgeDirection.NORTH ? ForgeDirection.EAST : face==ForgeDirection.EAST? ForgeDirection.SOUTH : face == ForgeDirection.SOUTH? ForgeDirection.WEST :ForgeDirection.NORTH;
    rightY = getWaterYLevel(x+face.offsetX, y+face.offsetY, z+face.offsetZ);
    
    if(rightY>0)
      {
      blockRight = worldObj.getBlock(x+face.offsetX, rightY, z+face.offsetZ);
      metaRight = worldObj.getBlockMetadata(x+face.offsetX, rightY, z+face.offsetZ);
      right = 1.f-BlockLiquid.getLiquidHeightPercent(metaRight);
      }
    else
      {
      blockRight = Blocks.air;
      right = mid;
      }
    
    face = face.getOpposite();
    leftY = getWaterYLevel(x+face.offsetX, y+face.offsetY, z+face.offsetZ);
    if(leftY>0)
      {
      blockLeft = worldObj.getBlock(x+face.offsetX, leftY, z+face.offsetZ);
      metaLeft = worldObj.getBlockMetadata(x+face.offsetX, leftY, z+face.offsetZ);
      left = 1.f-BlockLiquid.getLiquidHeightPercent(metaLeft);
      }
    else
      {
      left = mid;
      blockLeft = Blocks.air;
      }
//    AWLog.logDebug("left, mid, right: "+left+" :: "+mid+" :: "+right);
//    AWLog.logDebug("leftY, midY, rightY: "+leftY+" :: "+midY+" :: "+rightY);
    if(blockRight.getMaterial()==Material.water && blockLeft.getMaterial()==Material.water && ((left>mid && right<=mid) || (right>mid && left<=mid) || leftY!=midY || rightY!=midY))
      {
      float diff = 0;
      flowsLeft = right>mid || left<mid || leftY<midY || rightY>midY;
      if(mid<0.5f){flowsLeft = !flowsLeft;}
      if(left>mid){diff+=left-mid;}
      if(right>mid){diff+=right-mid;}
      if(left<mid){diff+=mid-left;}
      if(right<mid){diff+=mid-right;}
      if(leftY!=midY){diff+=1.f;}
      if(rightY!=midY){diff+=1.f;}
//      AWLog.logDebug("total diff: "+diff + "flowsLeft: "+flowsLeft);
      
      storedEnergy+=diff;
      if(storedEnergy>maxEnergy){storedEnergy=maxEnergy;}
      
      int speed = (int)(diff*1000.f);
      if(!flowsLeft){speed*=-1;}
      worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 2, speed);
      }
    }
  else
    {
    worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 2, 0);
    }
  
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  super.receiveClientEvent(a, b);
  if(a==2)
    {
    rotationSpeed = (float)b/1000.f;
//    AWLog.logDebug("set speed to: "+rotationSpeed);
    }
  return true;
  }

private int getWaterYLevel(int x, int y, int z)
  {
  if(worldObj.getBlock(x, y+1, z).getMaterial()==Material.water){return y+1;}
  else if(worldObj.getBlock(x, y, z).getMaterial()==Material.water){return y;}
  else if(worldObj.getBlock(x, y-1, z).getMaterial()==Material.water){return y-1;}
  return -1;
  }

@Override
public void setEnergy(double energy)
  {
  storedEnergy = energy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==ForgeDirection.getOrientation(getBlockMetadata());
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  return false;
  }

@Override
public String toString()
  {
  return "Torque Generator Waterwheel["+storedEnergy+"]";
  }

@Override
public double getMaxEnergy()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getMaxOutput()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public TileEntity[] getNeighbors()
  {
  if(neighborTileCache==null){buildNeighborCache();}
  return neighborTileCache;
  }

@Override
public void validate()
  {  
  super.validate();
  neighborTileCache = null;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  neighborTileCache = null;
  }

/**
 * should be called from the containing block when it receives a 'onNeighbotUpdate' callback 
 */
public void onBlockUpdated()
  {
  AWLog.logDebug("torque tile update...");
  buildNeighborCache();
  }

protected void buildNeighborCache()
  {
  this.neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    this.neighborTileCache[i] = te;
    }
  worldObj.theProfiler.endSection();    
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  setEnergy(tag.getDouble("storedEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", getEnergyStored());
  }

}
