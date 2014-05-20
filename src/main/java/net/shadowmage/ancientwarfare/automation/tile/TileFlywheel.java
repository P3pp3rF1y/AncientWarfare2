package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueStorage;


public class TileFlywheel extends TileEntity implements ITorqueStorage
{
public static final double maxEnergyStored = 10000;
public static final double maxInputPerTick = 100;
public static final double maxOutputPerTick = 100;

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

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

}
