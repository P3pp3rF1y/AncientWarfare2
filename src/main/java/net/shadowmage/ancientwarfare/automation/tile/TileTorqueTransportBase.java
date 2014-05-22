package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTransport;
import buildcraft.api.power.IPowerEmitter;
import cpw.mods.fml.common.Optional;


@Optional.Interface(iface="buildcraft.api.power.IPowerEmitter",modid="BuildCraft|Core",striprefs=true)
public abstract class TileTorqueTransportBase extends TileEntity implements ITorqueTransport, IPowerEmitter
{

protected TileEntity[] neighborTileCache = null;

protected double maxEnergy = 1000;
protected double maxInput = 100;
protected double maxOutput = 100;

@Override
public TileEntity[] getNeighbors()
  {
  if(neighborTileCache==null){buildNeighborCache();}
  return neighborTileCache;
  }

@Override
public void validate()
  {  
  super.validate();
  neighborTileCache = null;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  neighborTileCache = null;
  }

/**
 * should be called from the containing block when it receives a 'onNeighbotUpdate' callback 
 */
public void onBlockUpdated()
  {
  AWLog.logDebug("torque tile update...");
  buildNeighborCache();
  }

protected void buildNeighborCache()
  {
  this.neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    this.neighborTileCache[i] = te;
    }
  worldObj.theProfiler.endSection();    
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}  
  ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
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
    setEnergy(getEnergyStored()+energy);
    return energy;    
    }
  return 0;
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
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from!=ForgeDirection.getOrientation(getBlockMetadata());
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==ForgeDirection.getOrientation(getBlockMetadata());
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
