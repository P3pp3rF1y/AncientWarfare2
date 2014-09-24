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
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;

public abstract class TileTorqueBase extends TileEntity implements ITorqueTile, IInteractableTile
{

/**
 * per-tile (type) settings.  Should be set in tile constructor.
 */
protected double maxEnergy = 1000;
protected double maxInput = 100;
protected double maxOutput = 100;
protected double maxRpm = 10;
protected double energyDrainFactor = 1;

/**
 * Currently stored energy.  Should never exceed maxEnergy.  Should never be <0.
 */
protected double storedEnergy = 0;

/**
 * cached neighbor array for faster lookup during power transfers, only avail server side
 */
protected TileEntity[] neighborTileCache = null;
/**
 * cached connections list.  Built by buildConnection() during buildNeighborCache().<br>
 * Synched from server->client for those tiles with connectable sides (conduits, distributors)<br>
 * Used by client-side for rendering of connections between tiles.
 */
boolean[] connections;

/**
 * The primary facing direction for this tile.
 */
protected ForgeDirection orientation = ForgeDirection.NORTH;

/**
 * Cached vars used for checking/updating client-side energy state.
 */
protected double prevEnergy;
protected double energyInput;
protected double energyOutput;

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

/**
 * used by client-side for rendering animated tiles
 */
public double rotation;
public double prevRotation;

/************************************** UPDATE CODE ****************************************/

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote)
    {
    clientNetworkUpdate();
    return;
    }  
  else
    {
    serverNetworkUpdate();
    }
  this.energyInput = this.storedEnergy - this.prevEnergy;
  if(this.getMaxOutput()>0)
    {
    double s = this.storedEnergy;
    ITorque.transferPower(worldObj, xCoord, yCoord, zCoord, this);
    this.energyOutput = s - this.storedEnergy;    
    }
  ITorque.applyPowerDrain(this);
  this.prevEnergy = this.storedEnergy;  
  }

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
public String toString()
  {
  return "Torque Tile["+storedEnergy+"]::" +getClass().getSimpleName();
  }

/************************************** ENERGY MANAGEMENT CODE ****************************************/

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy>getMaxEnergy()-getEnergyStored()){energy=getMaxEnergy()-getEnergyStored();}
    if(energy>getMaxInput()){energy=getMaxInput();}
    setEnergy(getEnergyStored()+energy);
    return energy;
    }
  return 0;
  }

@Override
public double getMaxInput()
  {
  return maxInput;
  }

@Override
public double getMaxOutput()
  {
  return maxOutput;
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

/************************************** NEIGHBOR UPDATE AND CONNECTION CODE ****************************************/

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

public void onBlockUpdated()
  {
  buildNeighborCache();
  }

/**
 * Return the set of tile-entities that neighbor this tile.  indexed by forge-direction.ordinal()
 */
public TileEntity[] getNeighbors()
  {
  if(neighborTileCache==null){buildNeighborCache();}
  return neighborTileCache;
  }

/**
 * Build the cache of neighboring tile-entities.
 */
protected void buildNeighborCache()
  {
  if(worldObj.isRemote){return;}
  int conInt = connections==null ? 0 : getConnectionsInt();
  connections = new boolean[6];  
  neighborTileCache = new TileEntity[6];
  worldObj.theProfiler.startSection("AWPowerTileNeighborUpdate");
  ForgeDirection d;
  TileEntity te;
  for(int i = 0; i < 6; i++)
    {
    d = ForgeDirection.getOrientation(i);
    te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    neighborTileCache[i] = te;
    connections[i] = buildConnection(d, te);
    }
  int conInt2 = getConnectionsInt();
  if(conInt2!=conInt)
    {
    worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 0, getConnectionsInt());    
    } 
  worldObj.theProfiler.endSection();    
  }

/**
 * Update the cached 'connection' status for a given side.<br>
 * Should be overriden by those tiles that need neighbor connection cache
 * @param d
 * @param te
 */
protected boolean buildConnection(ForgeDirection d, TileEntity te)
  {
  return false;
  }

public boolean[] getConnections()
  {
  if(connections==null)
    {
    connections = new boolean[6];
    }
  return connections;
  }

protected int getConnectionsInt()
  {
  if(connections==null)
    {
    buildNeighborCache();
    }  
  int con = 0;
  int c;
  for(int i = 0; i < 6; i++)
    {
    c = connections[i]==true? 1: 0;
    con = con + (c<<i);
    }  
  return con;
  }

protected void readConnectionsInt(int con)
  {
  int c;
  if(connections==null){connections = new boolean[6];}
  for(int i = 0; i < 6; i++)
    {
    c = (con>>i) & 0x1;
    connections[i] = c==1;
    }
  }

/************************************** NETWORK CODE ****************************************/
/**
 * 0==connections update, used by conduits
 * 1==client-energy update
 * 2==waterwheel wheel speed/direction 
 */
@Override
public boolean receiveClientEvent(int a, int b)
  {
  super.receiveClientEvent(a, b);
  if(worldObj.isRemote)
    {
    if(a==0){readConnectionsInt(b);}
    else if(a==1)
      {
      clientDestEnergy=b;
      networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
      }
    }
  return true;
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
public final Packet getDescriptionPacket()
  {
  NBTTagCompound tag = getDescriptionTag();
  if(tag==null){return null;}
  return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, tag);
  }

public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("connections", getConnectionsInt());
  tag.setInteger("orientation", orientation.ordinal());
  tag.setInteger("clientEnergy", clientEnergy);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  if(pkt.func_148857_g().hasKey("connections"))
    {
    readConnectionsInt(pkt.func_148857_g().getInteger("connections"));
    }
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));
  clientEnergy = pkt.func_148857_g().getInteger("clientEnergy");
  clientDestEnergy = clientEnergy;
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

}
