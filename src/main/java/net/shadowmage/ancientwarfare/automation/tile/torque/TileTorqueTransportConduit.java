package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.SidedTorqueCell;

public class TileTorqueTransportConduit extends TileTorqueTransportBase
{

boolean connections[] = null;
SidedTorqueCell[] storage = new SidedTorqueCell[6];

public TileTorqueTransportConduit()
  {
  for(int i = 0; i <6; i++)
    {
    storage[i] = new SidedTorqueCell(32, 32, 32, 1, ForgeDirection.values()[i], this);//TODO set from config
    }
  }

@Override
public boolean canInputTorque(ForgeDirection from)
  {
  return from!=orientation;
  }

@Override
public boolean canOutputTorque(ForgeDirection towards)
  {
  return towards==orientation;
  }

public boolean[] getConnections()
  {
  if(connections==null){buildConnections();}
  return connections;
  }

@Override
public void onNeighborTileChanged()
  {
  super.onNeighborTileChanged();
  connections=null;
  }

protected void buildConnections()
  {
  connections = new boolean[6];
  ITorqueTile[] cache = getTorqueCache();
  for(int i = 0; i < 6; i++)
    {
    if(cache[i]!=null){connections[i]=true;}
    }
  if(ModuleStatus.buildCraftLoaded)
    {
    TileEntity[] tes = getBCCache();
    for(int i = 0; i < 6; i++)
      {
      if(tes[i]!=null){connections[i]=true;}
      }
    }
  if(ModuleStatus.redstoneFluxEnabled)
    {
    TileEntity[] tes = getRFCache();
    for(int i = 0; i < 6; i++)
      {
      if(tes[i]!=null){connections[i]=true;}
      }
    }
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  return storage[from.ordinal()].getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  return storage[from.ordinal()].addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  return storage[from.ordinal()].drainEnergy(energy);
  }

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxOutput();
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  return storage[from.ordinal()].getMaxInput();
  }

@Override
public boolean useOutputRotation(ForgeDirection from)
  {
  return true;
  }

@Override
public double getClientOutputRotation(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public double getPrevClientOutputRotation(ForgeDirection from)
  {
  // TODO Auto-generated method stub
  return 0;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  NBTTagList list = tag.getTagList("energyList", Constants.NBT.TAG_COMPOUND);
  for(int i = 0; i < 6; i++)
    {
    storage[i].readFromNBT(list.getCompoundTagAt(i));
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagList list = new NBTTagList();
  for(int i = 0; i < 6; i++)
    {
    list.appendTag(storage[i].writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("energyList", list);
  }

}
