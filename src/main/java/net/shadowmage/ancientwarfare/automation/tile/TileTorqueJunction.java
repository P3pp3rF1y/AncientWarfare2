package net.shadowmage.ancientwarfare.automation.tile;

import java.util.EnumSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;

public class TileTorqueJunction extends TileEntity implements ITorqueTransport
{

private static double maxEnergy = 1000;
private static double maxInput = 100;
private static double maxOutput = 100;

double energy;

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}    
  ForgeDirection d = ForgeDirection.getOrientation(getBlockMetadata());
  TileEntity te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
  if(te instanceof ITorqueReceiver)
    {      
    ITorque.transferPower(this, d, (ITorqueReceiver) te);     
    }
  }

@Override
public void setEnergy(double energy)
  {
  this.energy = energy;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergy;
  }

@Override
public double getEnergyStored()
  {
  return energy;
  }

@Override
public double getMaxOutput(ForgeDirection toSide)
  {
  return maxOutput;
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  return EnumSet.of(ForgeDirection.getOrientation(getBlockMetadata()));
  }

@Override
public double addEnergy(double energy)
  {
  double d = getMaxEnergy()-getEnergyStored();
  if(energy>d)
    {
    energy = d;
    }
  this.energy+=energy;
  return energy;
  }

@Override
public double getMaxInput(ForgeDirection fromSide)
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
  return "Torque Junction["+energy+"]";
  }

}
