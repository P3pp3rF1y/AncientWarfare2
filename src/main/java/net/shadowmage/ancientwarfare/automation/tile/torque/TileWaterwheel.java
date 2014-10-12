package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileWaterwheel extends TileTorqueSingleCell implements IInteractableTile
{

public float wheelRotation;
public float prevWheelRotation;

private float maxWheelRpm;
private float rotTick;
private byte rotationDirection=1;

private int updateTick;
protected TileEntity[] neighborTileCache = null;

public boolean validSetup = false;

private byte[] validationGrid;

public TileWaterwheel()
  {  
  double max = AWAutomationStatics.low_transfer_max;
  torqueCell = new TorqueCell(0, 4, max, AWAutomationStatics.low_efficiency_factor);
  maxWheelRpm = 20;
  rotTick = (maxWheelRpm * 360)/60/20;
  validationGrid = new byte[9];
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
      torqueCell.setEnergy(torqueCell.getEnergy() + (AWAutomationStatics.waterwheel_generator_output));
      }
    }
  }

@Override
protected void updateRotation()
  {
  super.updateRotation();
  prevWheelRotation = wheelRotation;
  if(validSetup)
    {
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
  
  validationGrid[0] = getValidationType(x2, y+1, z2);
  validationGrid[1] = getValidationType(x, y+1, z);
  validationGrid[2] = getValidationType(x1, y+1, z1);
  validationGrid[3] = getValidationType(x2, y, z2);
  validationGrid[4] = getValidationType(x, y, z);
  validationGrid[5] = getValidationType(x1, y, z1);
  validationGrid[6] = getValidationType(x2, y-1, z2);
  validationGrid[7] = getValidationType(x, y-1, z);
  validationGrid[8] = getValidationType(x1, y-1, z1);
  for(int i = 0;i < 9; i++){if(validationGrid[i]==0){return false;}}//quick check for invalid setup
  if(validationGrid[1]!=1 || validationGrid[4]!=1){return false;}//must have air inside the inner two blocks
  
  if((validationGrid[0]==2 || validationGrid[0]==1) && validationGrid[3]==2 && validationGrid[6]==2)//left side water flowing down
    {  
    //check opposite side for air (underneath has already been checked by quick block check above)
    if(validationGrid[2]==1 && validationGrid[5]==1)
      {
      rotationDirection = -1;//TODO check if these are correct rotation directions
      return true;
      }
    return false;
    }
  else if((validationGrid[2]==2 || validationGrid[2]==1)&& validationGrid[5]==2 && validationGrid[8]==2)//right side water flowing down
    {
    //check opposite side for air (underneath has already been checked by quick block check above)
    if(validationGrid[0]==1 && validationGrid[3]==1)
      {
      rotationDirection = 1;
      return true;
      }
    return false;
    }
  else//not a direct flow downwards, check underneath for flow
    {    
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
  }

private byte getValidationType(int x, int y, int z)
  {
  if(worldObj.isAirBlock(x, y, z)){return 1;}
  Block block = worldObj.getBlock(x, y, z);
  if(block.getMaterial()==Material.water){return 2;}
  return 0;
  }

@Override
public AxisAlignedBB getRenderBoundingBox()
  {
  return AxisAlignedBB.getBoundingBox(xCoord-3, yCoord-3, zCoord-3, xCoord+4, yCoord+4, zCoord+4);
  }

}
