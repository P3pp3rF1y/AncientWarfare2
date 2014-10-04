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
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

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
TorqueCell torqueCell;

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueGeneratorWaterwheel()
  {  
  torqueCell = new TorqueCell(0, 4, 32, 1.f);
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
      torqueCell.setEnergy(torqueCell.getEnergy() + (1.d * AWAutomationStatics.waterwheel_generator_output_factor));
      }
    serverNetworkUpdate();    
    torqueIn = torqueCell.getEnergy() - prevEnergy;    
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = torqueCell.getEnergy();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();   
    }  
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(torqueCell.getPercentFull()*100.d);
  int percent2 = (int)((torqueOut / torqueCell.getMaxOutput())*100.d);
  percent = Math.max(percent, percent2);  
  if(percent != clientDestEnergyState)
    {
    clientDestEnergyState = percent;
    sendSideRotation(getPrimaryFacing(), percent);    
    }
  }

@Override
protected void updateRotation()
  {
  prevRotation = rotation;
  if(clientEnergyState > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientEnergyState;
    rotation += r;
    }
  prevWheelRotation = wheelRotation;
  if(validSetup)
    {
    wheelRotation += rotTick * (float)rotationDirection;
    } 
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
      networkUpdateTicks--;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  clientDestEnergyState = ((double)value) * 0.01d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  torqueCell.setEnergy(tag.getDouble("torqueEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("torqueEnergy", torqueCell.getEnergy());
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setBoolean("validSetup", validSetup);
  tag.setByte("rotationDirection", rotationDirection);
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  validSetup = tag.getBoolean("validSetup");
  rotationDirection = tag.getByte("rotationDirection");
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
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


@Override
public double getMaxTorque(ForgeDirection from)
  {
  return torqueCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return torqueCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return torqueCell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return torqueCell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return torqueCell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return torqueCell.getMaxTickInput();
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return getRotation(rotation, prevRotation, delta);
  }

@Override
protected double getTotalTorque()
  {
  return torqueCell.getEnergy();
  }
}
