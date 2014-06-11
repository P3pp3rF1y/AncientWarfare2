package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public abstract class TileTorqueBase extends TileEntity implements ITorqueTile, IInteractableTile
{

protected TileEntity[] neighborTileCache = null;
protected double maxEnergy = 1000;
protected double storedEnergy = 0;
protected double energyDrainFactor = 1;
ForgeDirection orientation = ForgeDirection.NORTH;

public void setOrientation(ForgeDirection d)
  {
  this.orientation = d;
  }

@Override
public ForgeDirection getOrientation()
  {
  return orientation;
  }

@Override
public double getEnergyDrainFactor()
  {
  return energyDrainFactor;
  }

public void onBlockUpdated()
  {
  buildNeighborCache();
  }

@Override
public String toString()
  {
  return "Torque Tile["+storedEnergy+"]::" +getClass().getSimpleName();
  }

@Override
public void setEnergy(double energy)
  {
  this.storedEnergy = energy;
  }

@Override
public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergy;
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

public TileEntity[] getNeighbors()
  {
  if(neighborTileCache==null){buildNeighborCache();}
  return neighborTileCache;
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
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    String key = "guistrings.automation.current_energy";
    String value = String.format("%.2f", storedEnergy);
    ChatComponentTranslation chat = new ChatComponentTranslation(key, new Object[]{value});
    player.addChatComponentMessage(chat);    
    }
  return false;
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("orientation", orientation.ordinal());
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  super.onDataPacket(net, pkt);
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  }

}
