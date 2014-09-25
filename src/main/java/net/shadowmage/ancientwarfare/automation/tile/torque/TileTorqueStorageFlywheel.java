package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;


public class TileTorqueStorageFlywheel extends TileTorqueStorageBase
{

private boolean powered;
private List<TileTorqueStorageFlywheel> wheelsToBalance = new ArrayList<TileTorqueStorageFlywheel>();
double clientRotation;
double prevClientRotation;

public TileTorqueStorageFlywheel()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_storage_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  }

@Override
public boolean useClientRotation()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote){tryBalancingFlywheels();}  
  else{trySynchFlywheelRotation();}
  }

@Override
public void onBlockUpdated()
  {
  super.onBlockUpdated();
  boolean p = powered;
  powered = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord)>0;
  if(p!=powered)
    {
    int a = 3;
    int b = powered ? 1: 0;
    worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), a, b);
    }
  }

@Override
public double getClientOutputRotation()
  {
  return clientRotation;
  }

@Override
public double getPrevClientOutputRotation()
  {
  return prevClientRotation;
  }

@Override
protected void updateRotation()
  {
  super.updateRotation();
  prevClientRotation=clientRotation;
  if(!powered)
    {
    double d = rotation - prevRotation;
    clientRotation+=d;  
    }
  }

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
  powered = pkt.func_148857_g().getBoolean("powered");
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

private void trySynchFlywheelRotation()
  {
  TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
  if((te instanceof TileTorqueStorageFlywheel))
    {
    return;
    }
  int y = yCoord-1;
  te = worldObj.getTileEntity(xCoord, y, zCoord);
  while(y<256 && y>=1 && te instanceof TileTorqueStorageFlywheel)
    {
    wheelsToBalance.add((TileTorqueStorageFlywheel) te);
    y--;
    te = worldObj.getTileEntity(xCoord, y, zCoord);
    }
  for(TileTorqueStorageFlywheel wheel : wheelsToBalance)
    {
    wheel.rotation = rotation;
    wheel.prevRotation = prevRotation;
    }
  wheelsToBalance.clear();
  }

private void tryBalancingFlywheels()
  {
  TileEntity te = worldObj.getTileEntity(xCoord, yCoord+1, zCoord);
  if((te instanceof TileTorqueStorageFlywheel))
    {
    return;
    }
  int y = yCoord-1;
  te = worldObj.getTileEntity(xCoord, y, zCoord);
  while(y<256 && y>=1 && te instanceof TileTorqueStorageFlywheel)
    {
    wheelsToBalance.add((TileTorqueStorageFlywheel) te);
    y--;
    te = worldObj.getTileEntity(xCoord, y, zCoord);
    }
  wheelsToBalance.add(this);
  double totalPower = 0.d;
  double average;
  for(TileTorqueStorageFlywheel wheel : wheelsToBalance)
    {
    totalPower+=wheel.getEnergyStored();
    }
  average = totalPower / (double)wheelsToBalance.size();
  for(TileTorqueStorageFlywheel wheel : wheelsToBalance)
    {
    wheel.setEnergy(average);
    }
  wheelsToBalance.clear();
  }

@Override
public double getMaxOutput()
  {
  if(powered){return 0;}
  return super.getMaxOutput();
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==orientation.getOpposite();
  }

@Override
public boolean canOutput(ForgeDirection towards)
  {
  return towards==orientation;
  }

}
