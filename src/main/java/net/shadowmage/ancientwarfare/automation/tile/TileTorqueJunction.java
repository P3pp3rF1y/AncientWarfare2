package net.shadowmage.ancientwarfare.automation.tile;

import java.util.EnumSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;

public class TileTorqueJunction extends TileEntity implements ITorqueTransport
{

private static double maxEnergy = 1000;
private static double maxInput = 100;
private static double maxOutput = 100;

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
  return maxEnergy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxOutput()
  {
  return maxOutput;
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()));
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public EnumSet<ForgeDirection> getInputDirections()
  {
  EnumSet<ForgeDirection> set = EnumSet.allOf(ForgeDirection.class);
  set.remove(ForgeDirection.getOrientation(getBlockMetadata()));
  set.remove(ForgeDirection.UNKNOWN);
  return set;
  }

@Override
public String toString()
  {
  return "Torque Junction["+storedEnergy+"]";
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

}
