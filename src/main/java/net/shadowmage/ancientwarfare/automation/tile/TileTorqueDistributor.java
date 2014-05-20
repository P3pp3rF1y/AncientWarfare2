package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.common.Optional;


@Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true)
public class TileTorqueDistributor extends TileEntity implements ITorqueTransport, IPowerEmitter
{

@MjBattery(maxCapacity = TileTorqueConduit.maxEnergy)
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
public double getMaxInput()
  {
  return TileTorqueConduit.maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards!=ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
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

@Optional.Method(modid="BuildCraft|Core")
@Override
public boolean canEmitPowerFrom(ForgeDirection side)
  {
  return canOutput(side);
  }

}
