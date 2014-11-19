package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;

/**
 * base template class that includes a single torque cell and rotation synching
 * @author Shadowmage
 *
 */
public abstract class TileTorqueSingleCell extends TileTorqueBase
{

TorqueCell torqueCell;

/**
 * client side this == 0.0 -> 100, as a whole number percent of max rotation value
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0 -> 100 (integer percent)
 */
int clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;

public TileTorqueSingleCell()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void updateEntity()
  {  
  super.updateEntity();
  if(!worldObj.isRemote)
    { 
    serverNetworkUpdate();    
    torqueIn = torqueCell.getEnergy() - prevEnergy;
    torqueLoss = applyPowerDrain(torqueCell);
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = torqueCell.getEnergy();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

protected double applyPowerLoss()
  {
  return applyPowerDrain(torqueCell);
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(torqueCell.getPercentFull() * 100.d);
  int percent2 = (int)((torqueOut / torqueCell.getMaxOutput()) * 100.d);
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
    rotation += AWAutomationStatics.low_rpt * clientEnergyState * 0.01d;
    }
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks > 0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks);
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
  AWLog.logDebug("receiving sided rotation data: "+side+" :: "+value);
  if(side==orientation)
    {
    clientDestEnergyState = value;
    networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
    }
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
  return canOutputTorque(from) ? torqueCell.getMaxTickOutput() : 0;
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return canInputTorque(from) ? torqueCell.getMaxTickInput() : 0;
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
protected double getTotalTorque()
  {
  return torqueCell.getEnergy();
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public float getClientOutputRotation(ForgeDirection from, float delta)
  {
  return from==orientation ? getRotation(rotation, prevRotation, delta): 0;
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
  clientDestEnergyState = tag.getInteger("clientEnergy");
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  torqueCell.setEnergy(tag.getDouble("torqueEnergy"));
  clientDestEnergyState=tag.getInteger("clientEnergy");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("torqueEnergy", torqueCell.getEnergy());
  tag.setInteger("clientEnergy", clientDestEnergyState);
  }

}
