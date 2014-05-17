package net.shadowmage.ancientwarfare.automation.tile;

import java.util.EnumSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;

public class TileTorqueDistributor extends TileEntity implements ITorqueTransport
{

double storedEnergy;

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}  
  ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
  }

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double getMaxEnergy()
  {
  return TileTorqueConduit.maxEnergy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxOutput()
  {
  return TileTorqueConduit.maxOutput;
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  EnumSet<ForgeDirection> dirs = EnumSet.allOf(ForgeDirection.class);
  dirs.remove(ForgeDirection.UNKNOWN);
  dirs.remove(ForgeDirection.getOrientation(getBlockMetadata()).getOpposite());
  return dirs;
  }

@Override
public double getMaxInput()
  {
  return TileTorqueConduit.maxInput;
  }

@Override
public EnumSet<ForgeDirection> getInputDirections()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()).getOpposite());
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  storedEnergy = tag.getDouble("storedEnergy");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  }

@Override
public String toString()
  {
  return "Torque Distributor["+storedEnergy+"]";
  }

}
