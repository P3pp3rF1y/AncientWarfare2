package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;

public class TileHandCrankedEngine extends TileTorqueTransportBase implements ITorqueGenerator
{

double storedEnergy = 0;

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
  }

@Override
public void setEnergy(double energy)
  {
  storedEnergy = energy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==ForgeDirection.getOrientation(getBlockMetadata());
  }

@Override
public String toString()
  {
  return "Hand Cranked Engine["+storedEnergy+"]";
  }

}
