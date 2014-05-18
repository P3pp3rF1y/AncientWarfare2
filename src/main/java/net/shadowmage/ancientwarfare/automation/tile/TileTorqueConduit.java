package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;

public class TileTorqueConduit extends TileEntity implements ITorqueTransport
{

public static final double maxInput = 100;
public static final double maxOutput = 100;
public static final double maxEnergy = 1000;

double storedEnergy;

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
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
public double getMaxEnergy()
  {
  return maxEnergy;
  }

@Override
public double getMaxInput()
  {  
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==ForgeDirection.getOrientation(getBlockMetadata());
  }

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public String toString()
  {
  return "Torque Conduit["+storedEnergy+"]";
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
