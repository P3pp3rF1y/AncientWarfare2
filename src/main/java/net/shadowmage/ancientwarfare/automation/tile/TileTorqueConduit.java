package net.shadowmage.ancientwarfare.automation.tile;

import java.util.EnumSet;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;

public class TileTorqueConduit extends TileEntity implements ITorqueTransport
{

private static double maxInput = 100;
private static double maxOutput = 100;
private static double maxEnergy = 1000;

double energyStored;

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
public double getEnergyStored()
  {
  return energyStored;
  }

@Override
public double getMaxOutput(ForgeDirection toSide)
  {
  return maxOutput;
  }

@Override
public double addEnergy(double energy)
  {
  double d = getMaxEnergy()-getEnergyStored();
  if(d>energy){d=energy;}
  energyStored+=d;
  return d;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergy;
  }

@Override
public double getMaxInput(ForgeDirection fromSide)
  {  
  return maxInput;
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
public void setEnergy(double energy)
  {
  this.energyStored = energy;
  }

@Override
public String toString()
  {
  return "Torque Conduit["+energyStored+"]";
  }

}
