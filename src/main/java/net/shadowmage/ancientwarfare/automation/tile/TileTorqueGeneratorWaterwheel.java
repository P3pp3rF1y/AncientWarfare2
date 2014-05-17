package net.shadowmage.ancientwarfare.automation.tile;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;

public class TileTorqueGeneratorWaterwheel extends TileEntity implements ITorqueGenerator, IInteractableTile
{

private double storedEnergy;

public TileTorqueGeneratorWaterwheel()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void setEnergy(double energy)
  {
  // TODO Auto-generated method stub
  
  }

@Override
public double getMaxEnergy()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getEnergyStored()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getMaxOutput()
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public EnumSet<ForgeDirection> getOutputDirection()
  {
  // TODO Auto-generated method stub
  return null;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  // TODO Auto-generated method stub
  return false;
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
  return "Torque Generator Waterwheel["+storedEnergy+"]";
  }

}
