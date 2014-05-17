package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueStorage;
import buildcraft.api.mj.MjBattery;


public class TileFlywheel extends TileEntity implements ITorqueStorage
{
public static final double maxEnergyStored = 10000;
public static final double maxInputPerTick = 100;
public static final double maxOutputPerTick = 100;


@MjBattery(maxCapacity=maxEnergyStored)
public double storedEnergy;

private List<TileFlywheel> wheelsToBalance = new ArrayList<TileFlywheel>();

@Override
public void updateEntity()
  {
  if(!worldObj.isRemote)
    {
    ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
    tryBalancingFlywheels();    
    }
  }

private void tryBalancingFlywheels()
  {
  TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
  if((te instanceof TileFlywheel))
    {
    return;
    }
  int y = yCoord-1;
  te = worldObj.getTileEntity(xCoord, y, zCoord);
  while(y<156 && y>=1 && te instanceof TileFlywheel)
    {
    wheelsToBalance.add((TileFlywheel) te);
    y--;
    te = worldObj.getTileEntity(xCoord, y, zCoord);
    }
  wheelsToBalance.add(this);
  double totalPower = 0.d;
  double average;
  for(TileFlywheel wheel : wheelsToBalance)
    {
    totalPower+=wheel.getEnergyStored();
    }
  average = totalPower / (double)wheelsToBalance.size();
  for(TileFlywheel wheel : wheelsToBalance)
    {
    wheel.setEnergy(average);
    }
  wheelsToBalance.clear();
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxOutput()
  {
  return maxOutputPerTick;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

@Override
public double getMaxInput()
  {  
  return maxInputPerTick;
  }

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public EnumSet<ForgeDirection> getInputDirections()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()).getOpposite());
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()));
  }

@Override
public String toString()
  {
  return "Flywheel Energy Storage["+storedEnergy+"]";
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
