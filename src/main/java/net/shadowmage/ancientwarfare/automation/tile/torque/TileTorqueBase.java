package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.proxy.BCProxy;
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import buildcraft.api.mj.IBatteryObject;
import buildcraft.api.mj.ISidedBatteryProvider;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value=
  {
  @Optional.Interface(iface="cofh.api.energy.IEnergyHandler", modid="CoFHCore",striprefs=true),
  @Optional.Interface(iface="buildcraft.api.mj.ISidedBatteryProvider",modid="BuildCraft|Core",striprefs=true),
  })
public abstract class TileTorqueBase extends TileEntity implements ITorqueTile, IInteractableTile, IRotatableTile, IEnergyHandler, ISidedBatteryProvider
{

/**
 * The primary facing direction for this tile.
 */
protected ForgeDirection orientation = ForgeDirection.NORTH;

/**
 * used by server to limit packet sending<br>
 * used by client for lerp-ticks for lerping to new power state
 */
protected int networkUpdateTicks;

private TileEntity[]bcCache;//cannot reference interface directly, but can cast directly...
private TileEntity[]rfCache;//cannot reference interface directly, but can cast directly...
private ITorqueTile[]torqueCache;


//*************************************** COFH RF METHODS ***************************************//
@Optional.Method(modid="CoFHCore")
@Override
public final int getEnergyStored(ForgeDirection from)
  {
  return (int) (getTorqueStored(from) * AWAutomationStatics.torqueToRf);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int getMaxEnergyStored(ForgeDirection from)
  {
  return (int) (getMaxTorque(from) * AWAutomationStatics.torqueToRf);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final boolean canConnectEnergy(ForgeDirection from)
  {
  return canOutputTorque(from) || canInputTorque(from);//TODO verify what this expects
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
  {
  if(!canOutputTorque(from)){return 0;}  
  if(simulate){return Math.min(maxExtract, (int) (AWAutomationStatics.torqueToRf * getMaxTorqueOutput(from)));}
  return (int) (AWAutomationStatics.torqueToRf * drainTorque(from, (double)maxExtract * AWAutomationStatics.rfToTorque));
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
  {
  if(!canInputTorque(from)){return 0;}
  if(simulate){return Math.min(maxReceive, (int)(AWAutomationStatics.torqueToRf * getMaxTorqueInput(from)));}
  return (int)(AWAutomationStatics.torqueToRf * addTorque(from, (double)maxReceive * AWAutomationStatics.rfToTorque));
  }

//*************************************** BC MJ METHODS ***************************************//

@Optional.Method(modid="BuildCraft|Core")
@Override
public IBatteryObject getMjBattery(String kind, ForgeDirection direction)
  {  
  return (IBatteryObject) BCProxy.instance.getBatteryObject(kind, this, direction);
  }

//*************************************** NEIGHBOR CACHE UPDATING ***************************************//

protected final ITorqueTile[] getTorqueCache()
  {
  if(torqueCache==null){buildTorqueCache();}
  return torqueCache;
  }

protected final TileEntity[] getRFCache()
  {
  if(rfCache==null){buildRFCache();}
  return rfCache;
  }

protected final TileEntity[] getBCCache()
  {
  if(bcCache==null){buildBCCache();}
  return bcCache;
  }

private void buildTorqueCache()
  {
  torqueCache = new ITorqueTile[6];
  ForgeDirection dir;
  TileEntity te;
  ITorqueTile itt;
  int x, y, z;
  for(int i = 0; i < 6; i++)
    {
    dir = ForgeDirection.values()[i];
    if(!canOutputTorque(dir) && !canInputTorque(dir)){continue;}
    x = xCoord+dir.offsetX;
    y = yCoord+dir.offsetY;
    z = zCoord+dir.offsetZ;
    if(!worldObj.blockExists(x, y, z)){continue;}
    te = worldObj.getTileEntity(x, y, z);
    if(te instanceof ITorqueTile)
      {
      itt = (ITorqueTile)te;
      if((canOutputTorque(dir) && itt.canInputTorque(dir.getOpposite())) || canInputTorque(dir) && itt.canOutputTorque(dir.getOpposite()))
        {
        torqueCache[dir.ordinal()]=itt;
        }
      }
    }
  }

private void buildRFCache()
  {
  rfCache = new TileEntity[6];
  ForgeDirection dir;
  TileEntity te;
  int x, y, z;
  for(int i = 0; i < 6; i++)
    {
    dir = ForgeDirection.values()[i];
    if(!canOutputTorque(dir) && !canInputTorque(dir)){continue;}
    x = xCoord+dir.offsetX;
    y = yCoord+dir.offsetY;
    z = zCoord+dir.offsetZ;
    if(!worldObj.blockExists(x, y, z)){continue;}
    te = worldObj.getTileEntity(x, y, z);
    if(RFProxy.instance.isRFTile(te))
      {
      rfCache[dir.ordinal()]=te;
      }
    }
  }

private void buildBCCache()
  {
  bcCache = new TileEntity[6];
  ForgeDirection dir;
  TileEntity te;
  int x, y, z;
  for(int i = 0; i < 6; i++)
    {
    dir = ForgeDirection.values()[i];
    if(!canOutputTorque(dir) && !canInputTorque(dir)){continue;}
    x = xCoord+dir.offsetX;
    y = yCoord+dir.offsetY;
    z = zCoord+dir.offsetZ;
    if(!worldObj.blockExists(x, y, z)){continue;}
    te = worldObj.getTileEntity(x, y, z);
    if(BCProxy.instance.isPowerPipe(te))
      {
      bcCache[dir.ordinal()]=te;
      }
    }
  }

@Override
public void validate()
  {  
  super.validate();
  invalidateNeighborCache();
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  invalidateNeighborCache();
  }

public final void onNeighborTileChanged()
  {
  invalidateNeighborCache();
  }

protected final void invalidateNeighborCache()
  {
  torqueCache=null;
  rfCache=null;
  bcCache=null;
  }

//*************************************** generic stuff ***************************************//

@Override
public final void setPrimaryFacing(ForgeDirection d)
  {
  this.orientation = d;
  this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
  this.invalidateNeighborCache();
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  }

@Override
public final ForgeDirection getPrimaryFacing()
  {
  return orientation;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!worldObj.isRemote)
    {
    double d = 0;
    for(int i = 0; i < 6; i++)
      {
      d += getTorqueStored(ForgeDirection.values()[i]);
      }
    String e = String.format(".2f", d);
    player.addChatMessage(new ChatComponentText("Energy Stored: "+e));    
    }
  return false;
  }

//*************************************** Utility Methods ***************************************//

protected final void transferPower(ForgeDirection from)
  {
  ITorqueTile[] tc = getTorqueCache();
  if(tc[from.ordinal()]!=null && tc[from.ordinal()].canInputTorque(from.getOpposite()))
    {
    drainTorque(from, tc[from.ordinal()].addTorque(from.getOpposite(), getMaxTorqueOutput(from)));
    }
  }

protected final int getConnectionsInt(boolean [] connections)
  {  
  int con = 0;
  int c;
  for(int i = 0; i < 6; i++)
    {
    c = connections[i]==true? 1: 0;
    con = con + (c<<i);
    }  
  return con;
  }

protected final boolean[] readConnectionsInt(int con, boolean[] connections)
  {
  int c;
  if(connections==null){connections = new boolean[6];}
  for(int i = 0; i < 6; i++)
    {
    c = (con>>i) & 0x1;
    connections[i] = c==1;
    }
  return connections;
  }

protected final int packClientEnergyStates(int[] halfByteDatas)
  {
  int field = 0;
  int len = halfByteDatas.length;
  for(int i =0; i<len && i<8 ; i++)
    {
    field = field | ((halfByteDatas[i] & 16) << i*4);
    }
  return field;
  }

protected final void unpackClientEnergyStates(int stateData, int[] halfByteStates)
  {
  int len = halfByteStates.length;
  for(int i =0; i<len && i<8 ; i++)
    {
    halfByteStates[i]=(stateData >> i*4) & 16;
    }
  }

//*************************************** NBT / DATA PACKET ***************************************//

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  orientation = ForgeDirection.getOrientation(tag.getInteger("orientation"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setInteger("orientation", orientation.ordinal());
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
  tag.setInteger("orientation", orientation.ordinal());
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  orientation = ForgeDirection.getOrientation(pkt.func_148857_g().getInteger("orientation"));  
  this.invalidateNeighborCache();
  this.worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
  this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);//uhh..why am i doing this on client?
  }

}
