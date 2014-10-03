package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

public class TileTorqueWindmillController extends TileTorqueBase
{

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
TorqueCell powerCell;

public TileTorqueWindmillController()
  {
  powerCell = new TorqueCell(32,32,32,1);
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(powerCell.getPercentFull()*100.d);
  int percent2 = (int)((torqueOut / powerCell.getMaxOutput())*100.d);
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
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  powerCell.setEnergy(tag.getDouble("torqueEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("torqueEnergy", powerCell.getEnergy());
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return powerCell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return powerCell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return powerCell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return powerCell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return powerCell.getMaxTickOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return powerCell.getMaxTickInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return getRotation(rotation, prevRotation, delta);
  }

@Override
protected double getTotalTorque()
  {
  return powerCell.getEnergy();
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return false;
  }

}
