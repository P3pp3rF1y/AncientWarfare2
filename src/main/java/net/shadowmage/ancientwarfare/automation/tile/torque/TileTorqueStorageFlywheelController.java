package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;


public class TileTorqueStorageFlywheelController extends TileTorqueStorageBase
{

private boolean powered;

double clientRotation;
double prevClientRotation;

public TileTorqueStorageFlywheelController()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_storage_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  maxRpm = 100;
  }

public TileFlywheelStorage getControlledFlywheel()
  {
  int x = xCoord;
  int y = yCoord-1;
  int z = zCoord;
  TileEntity te = worldObj.getTileEntity(x, y, z);
  if(te instanceof TileFlywheelStorage)
    {
    TileFlywheelStorage fs = (TileFlywheelStorage)te;
    if(fs.controllerPos!=null)
      {
      x = fs.controllerPos.x;
      y = fs.controllerPos.y;
      z = fs.controllerPos.z;
      te = worldObj.getTileEntity(x, y, z);
      if(te instanceof TileFlywheelStorage)
        {
        return (TileFlywheelStorage) te;
        }
      }
    }
  return null;
  }

public double getFlywheelRotation()
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0: storage.rotation;
  }

public double getFlywheelPrevRotation()
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0: storage.prevRotation;
  }

//TODO clean these up, along with all other energy accessors?
//@Override
//public double getEnergyStored()
//  {
//  TileFlywheelStorage storage = getControlledFlywheel();
//  return storage==null ? 0 : storage.storedEnergy;
//  }
//
//@Override
//public double getMaxEnergy()
//  {
//  TileFlywheelStorage storage = getControlledFlywheel();
//  return storage==null ? 0 : storage.maxEnergyStored;
//  }

@Override
protected void updateRotation()
  {  
  super.updateRotation();
  prevClientRotation = clientRotation;
  if(!powered){clientRotation += rotation -prevRotation;}
  }

@Override
public void onBlockUpdated()
  {
  super.onBlockUpdated();
  if(!worldObj.isRemote)
    {
    boolean p = powered;
    powered = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord)>0;
    if(p!=powered)
      {
      int a = 3;
      int b = powered ? 1: 0;
      worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), a, b);
      }    
    }
  }

@Override
public boolean useClientRotation(){return true;}

@Override
public double getClientOutputRotation(){return clientRotation;}

@Override
public double getPrevClientOutputRotation(){return prevClientRotation;}

@Override
public boolean receiveClientEvent(int a, int b)
  {  
  if(worldObj.isRemote)
    {
    if(a==3)
      {
      powered = b==1;
      }    
    }
  return super.receiveClientEvent(a, b);
  }

@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setBoolean("powered", powered);  
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  powered = tag.getBoolean("powered");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setBoolean("powered", powered);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  powered = tag.getBoolean("powered");  
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  if(powered){return 0;}
  return super.getMaxTorqueOutput(from);
  }

@Override
public boolean canInputTorque(ForgeDirection from){return from==orientation.getOpposite();}

@Override
public boolean canOutputTorque(ForgeDirection towards){return towards==orientation;}

}
