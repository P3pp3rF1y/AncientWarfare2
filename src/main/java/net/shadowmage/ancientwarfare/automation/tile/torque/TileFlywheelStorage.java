package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileFlywheelStorage extends TileEntity
{

BlockPosition controllerPos;
TileTorqueStorageFlywheelController controller;
boolean init = false;//has run init code to check for controller tile from controllerPos

public void blockBroken()
  {
  if(controller!=null)
    {
    controller.initializeFromStoragePlaced();
    }
  else
    {
    TileEntity te;
    ForgeDirection d;
    int x, y, z;
    for(int i = 0; i < 6; i++)
      {
      d = ForgeDirection.getOrientation(i);
      x = xCoord+d.offsetX;
      y = yCoord+d.offsetY;
      z = zCoord+d.offsetZ;
      te = worldObj.getTileEntity(x, y, z);
      if(te instanceof TileFlywheelStorage)
        {
        ((TileFlywheelStorage) te).blockPlaced();
        }    
      }
    }
  }

public void blockPlaced()
  {
  findConnectedBlocks();
  }

public void setController(TileTorqueStorageFlywheelController controller)
  {
  this.controller = controller;
  this.controllerPos = controller==null ? null : new BlockPosition(controller.xCoord, controller.yCoord, controller.zCoord);
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

protected void findConnectedBlocks()
  {
  Set<BlockPosition> connectedPosSet = new HashSet<BlockPosition>();  
  BlockFinder.findConnectedSixWay(worldObj, xCoord, yCoord, zCoord, getBlockType(), getBlockMetadata(), connectedPosSet);
  int minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE, minZ=Integer.MAX_VALUE, maxX=Integer.MIN_VALUE, maxY=Integer.MIN_VALUE, maxZ=Integer.MIN_VALUE;
  boolean valid;
  for(BlockPosition pos : connectedPosSet)
    {
    if(pos.x<minX){minX=pos.x;}
    if(pos.x>maxX){maxX=pos.x;}
    if(pos.y<minY){minY=pos.y;}
    if(pos.y>maxY){maxY=pos.y;}
    if(pos.z<minZ){minZ=pos.z;}
    if(pos.z>maxZ){maxZ=pos.z;}
    }
  int w = maxX-minX + 1;
  int l = maxZ-minZ + 1;
  int h = maxY-minY + 1;
  int cube = w*l*h;
  if(cube==connectedPosSet.size() && ((w==1 && l==1) || (w==3 && l==3)) ){valid = true;}
  else
    {
    valid=false;
    TileEntity te;
    TileFlywheelStorage st;
    for(BlockPosition pos : connectedPosSet)
      {
      te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
      if(te instanceof TileFlywheelStorage)
        {
        st = (TileFlywheelStorage) te;
        if(st.controller!=null)
          {
          st.controller.initializeFromStoragePlaced();
          }
        }
      }
    }
  if(valid)    
    {
    int cx = w==1? minX : minX + 1;
    int cz = l==1? minZ : minZ + 1;
    int cy = maxY + 1;    
    TileEntity te = worldObj.getTileEntity(cx, cy, cz);
    if(te instanceof TileTorqueStorageFlywheelController)
      {
      ((TileTorqueStorageFlywheelController) te).initializeFromStoragePlaced();
      }
    }  
  }

@Override
public void updateEntity()
  {  
  super.updateEntity();
  if(!init)
    {
    init=true;
    if(!worldObj.isRemote && controllerPos!=null)
      {
      TileEntity te = worldObj.getTileEntity(controllerPos.x, controllerPos.y, controllerPos.z);
      if(te instanceof TileTorqueStorageFlywheelController)
        {
        controller = (TileTorqueStorageFlywheelController) te;
        }
      }
    }
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  if(controllerPos!=null){tag.setTag("controllerPos", controllerPos.writeToNBT(new NBTTagCompound()));}
  return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  controller = null;
  controllerPos = null;
  NBTTagCompound tag =pkt.func_148857_g();
  if(tag.hasKey("controllerPos"))
    {
    controllerPos = new BlockPosition(tag.getCompoundTag("controllerPos"));
    TileEntity te = worldObj.getTileEntity(controllerPos.x, controllerPos.y, controllerPos.z);
    if(te instanceof TileTorqueStorageFlywheelController)
      {
      controller = (TileTorqueStorageFlywheelController) te;
      }
    }  
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  if(tag.hasKey("controllerPos")){controllerPos = new BlockPosition(tag.getCompoundTag("controllerPos"));}
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(controllerPos!=null){tag.setTag("controllerPos", controllerPos.writeToNBT(new NBTTagCompound()));}
  }

}
