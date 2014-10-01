package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class TileTorqueGeneratorWaterwheel extends TileTorqueGeneratorBase implements IInteractableTile
{

public float wheelRotation;
public float prevWheelRotation;

private float maxWheelRpm;
private float rotTick;
private byte rotationDirection=1;

private int updateTick;
protected TileEntity[] neighborTileCache = null;

public boolean validSetup = false;

public TileTorqueGeneratorWaterwheel()
  {  
  maxWheelRpm = 20;
  rotTick = (maxWheelRpm * 360)/60/20;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    {
    updateTick--;
    if(updateTick<=0)
      {
      updateTick=20;
      boolean valid = validateBlocks();
      if(valid!=validSetup)
        {
        validSetup = valid;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
      }
    if(validSetup)//server, update power gen
      {
      storedEnergy += 1.d * AWAutomationStatics.waterwheel_generator_output_factor;
      if(storedEnergy>maxEnergy){storedEnergy=maxEnergy;}
      }
    }
  else if(validSetup)//client world, update for render
    {
    prevWheelRotation = wheelRotation;
    wheelRotation += rotTick * (float)rotationDirection;
    }
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setBoolean("validSetup", validSetup);
  tag.setByte("rotationDirection", rotationDirection);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  validSetup = tag.getBoolean("validSetup");
  rotationDirection = tag.getByte("rotationDirection");
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

private ForgeDirection getRight(ForgeDirection in)
  {
  switch(in)
  {
  case NORTH:
    {
    return ForgeDirection.EAST;
    }
  case EAST:
    {
    return ForgeDirection.SOUTH;
    }
  case SOUTH:
    {
    return ForgeDirection.WEST;
    }
  case WEST:
    {
    return ForgeDirection.NORTH;
    }
  default:
  return ForgeDirection.NORTH;
  }
  }

private boolean validateBlocks()
  {
  ForgeDirection d = orientation.getOpposite();
  ForgeDirection dr = getRight(d);
  ForgeDirection dl = dr.getOpposite();  
  int x = xCoord+d.offsetX;
  int y = yCoord+d.offsetY;
  int z = zCoord+d.offsetZ;
  int x1 = x+dr.offsetX;
  int z1 = z+dr.offsetZ;
  int x2 = x+dl.offsetX;
  int z2 = z+dl.offsetZ;
  if(!worldObj.isAirBlock(x, y, z) || !worldObj.isAirBlock(x, y+1, z) ||
     !worldObj.isAirBlock(x1, y, z1) || !worldObj.isAirBlock(x1, y+1, z1) ||
     !worldObj.isAirBlock(x2, y, z2) || !worldObj.isAirBlock(x2, y+1, z2))
    {
    return false;
    }
  Block bl, bm, br;
  bl = worldObj.getBlock(x2, y-1, z2);
  bm = worldObj.getBlock(x, y-1, z);
  br = worldObj.getBlock(x1, y-1, z1);
  if(bl.getMaterial()!=Material.water || bm.getMaterial()!=Material.water || br.getMaterial()!=Material.water)
    {
    return false;
    }
  int metaLeft = worldObj.getBlockMetadata(x2, y-1, z2);
  int metaRight = worldObj.getBlockMetadata(x1, y-1, z1);
  rotationDirection = (byte) (metaLeft<metaRight?-1 : metaRight<metaLeft ? 1 : 0);
  return true;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

}
