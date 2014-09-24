package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public abstract class TileTorqueBase extends TileEntity implements ITorqueTile, IInteractableTile
{

protected TileEntity[] neighborTileCache = null;
protected double maxEnergy = 1000;
protected double maxInput = 100;
protected double maxOutput = 100;
protected double storedEnergy = 0;
protected double energyDrainFactor = 1;
ForgeDirection orientation = ForgeDirection.NORTH;

/**
 * used by server to limit packet sending<br>
 * used by client for lerp-ticks for lerping to new power state
 */
protected int networkUpdateTicks;
/**
 * used by server to determine last sent client power state<br>
 * used by clients as their displayed power state
 */
protected int clientEnergy;
/**
 * used by clients to store what energy level they should be at.<br>
 * used in combination with networkUpdateTicks to lerp from clientEnergy to clientDestEnergy
 */
protected int clientDestEnergy;

protected double prevEnergy;
protected double energyInput;
protected double energyOutput;

protected double maxRpm = 10;

public double rotation;
public double prevRotation;

protected void serverNetworkUpdate()
  {
  if(!AWAutomationStatics.enable_energy_network_updates){return;}
  networkUpdateTicks--;
  if(networkUpdateTicks<=0)
    {
    double percentStored = storedEnergy/maxEnergy;
    double percentTransferred = energyOutput/maxOutput;
    int total = (int)((percentStored+percentTransferred)*100.d);
    if(total>100){total=100;}
    if(total!=clientEnergy)
      {
      clientEnergy = total;
      this.worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 1, clientEnergy);
      }
    networkUpdateTicks=AWAutomationStatics.energyMinNetworkUpdateFrequency;
    }
  }

protected void clientNetworkUpdate()
  {
  if(!AWAutomationStatics.enable_energy_client_updates){return;}
  updateRotation();
  if(networkUpdateTicks>0)
    {
    int diff = clientDestEnergy-clientEnergy;
    clientEnergy += diff/networkUpdateTicks;    
    networkUpdateTicks--;
    }
  }

protected void updateRotation()
  {
  double maxRpm = this.maxRpm;
  double rpm = (double)clientEnergy * 0.01d * maxRpm;
  prevRotation=rotation;
  rotation += rpm * 360.d / 20.d / 60.d;
  }

public void setOrientation(ForgeDirection d)
  {
  this.orientation = d;
  }

@Override
public ForgeDirection getPrimaryFacing()
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
  clientEnergy = tag.getInteger("clientEnergy");
  orientation = ForgeDirection.getOrientation(tag.getInteger("orientation"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setDouble("storedEnergy", storedEnergy);
  tag.setInteger("orientation", orientation.ordinal());
  tag.setInteger("clientEnergy", clientEnergy);
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
  tag.setInteger("clientEnergy", clientEnergy);
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  clientEnergy = pkt.func_148857_g().getInteger("clientEnergy");
  clientDestEnergy = clientEnergy;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

/**
 * 0==connections update, used by conduits
 * 1==client-energy update
 * 2==waterwheel wheel speed/direction 
 */
@Override
public boolean receiveClientEvent(int a, int b)
  {
  super.receiveClientEvent(a, b);
  if(worldObj.isRemote && a==1)
    {
    clientDestEnergy=b;
    networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
    }  
  return true;
  }

}
