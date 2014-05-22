package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueGenerator;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.common.Optional;

@Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true)
public abstract class TileTorqueGeneratorBase extends TileTorqueBase implements ITorqueGenerator, IPowerEmitter
{

protected double maxEnergy = 1000;
protected double maxOutput = 100;

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}  
  ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergy;
  }

@Override
public double getMaxOutput()
  {
  return maxOutput;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  setEnergy(tag.getDouble("storedEnergy"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", getEnergyStored());
  }

@Optional.Method(modid="BuildCraft|Core")
@Override
public boolean canEmitPowerFrom(ForgeDirection side)
  {
  return canOutput(side);
  }

}
