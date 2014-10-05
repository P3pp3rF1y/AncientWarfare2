package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
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
 * The primary facing direction for this tile.  Default to north for uninitialized tiles (null is not a valid value)
 */
protected ForgeDirection orientation = ForgeDirection.NORTH;

/**
 * used by server to limit packet sending<br>
 * used by client for lerp-ticks for lerping between power states
 */
protected int networkUpdateTicks;

private TileEntity[]bcCache;//cannot reference interface directly, but can cast directly...//only used when bc installed
private TileEntity[]rfCache;//cannot reference interface directly, but can cast directly...//only used when cofh installed
private ITorqueTile[]torqueCache;

/**
 * helper vars to be used by tiles during updating, to cache in/out/loss values<br>
 * IMPORTANT: should NOT be relied upon for calculations, only for use for display purposes<br>
 * E.G. A tile may choose to -not- update these vars.<br>
 * However, best effort should be made to update these vars accurately.<br><br>
 * Generated energy should be counted as 'in'<br>
 * Any directly output energy should be counted as 'out'<br>
 * Only direct power loss from transmission efficiency or per-tick loss should be counted for 'loss'
 */
protected double torqueIn, torqueOut, torqueLoss, prevEnergy;

//*************************************** COFH RF METHODS ***************************************//
@Optional.Method(modid="CoFHCore")
@Override
public final int getEnergyStored(ForgeDirection from)
  {
//  AWLog.logDebug("cofh get stored: "+from);
  return (int) (getTorqueStored(from) * AWAutomationStatics.torqueToRf);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int getMaxEnergyStored(ForgeDirection from)
  {
//  AWLog.logDebug("cofh get max stored: "+from);
  return (int) (getMaxTorque(from) * AWAutomationStatics.torqueToRf);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final boolean canConnectEnergy(ForgeDirection from)
  {
//  AWLog.logDebug("cofh get can connect: "+from);
  return canOutputTorque(from) || canInputTorque(from);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
  {
//  AWLog.logDebug("cofh extract: "+from+" :: "+maxExtract);
  return 0;
//  if(!canOutputTorque(from)){return 0;}  
//  if(simulate){return Math.min(maxExtract, (int) (AWAutomationStatics.torqueToRf * getMaxTorqueOutput(from)));}
//  return (int) (AWAutomationStatics.torqueToRf * drainTorque(from, (double)maxExtract * AWAutomationStatics.rfToTorque));
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
  {
//  AWLog.logDebug("cofh receive: "+from+" :: "+maxReceive);
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

public final ITorqueTile[] getTorqueCache()
  {
  if(torqueCache==null){buildTorqueCache();}
  return torqueCache;
  }

public final TileEntity[] getRFCache()
  {
  if(rfCache==null){buildRFCache();}
  return rfCache;
  }

public final TileEntity[] getBCCache()
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
      torqueCache[dir.ordinal()]=itt;
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
    if(BCProxy.instance.isPowerTile(te))
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

public void onNeighborTileChanged()
  {
  invalidateNeighborCache();
  }

protected final void invalidateNeighborCache()
  {
  torqueCache=null;
  rfCache=null;
  bcCache=null;
  onNeighborCacheInvalidated();
  }

protected void onNeighborCacheInvalidated()
  {
  
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
  ItemStack stack = player.getCurrentEquippedItem();
  if(stack==null)
    {
    if(!worldObj.isRemote)
      {
      String e = String.format("Stored | In | Out | Loss :: %.2f | %.2f | %.2f | %.2f", getTotalTorque(), getTorqueIn(),  getTorqueOut(), getTorqueLoss());
      player.addChatMessage(new ChatComponentText(e));    
      }
    return true;
    }
  return false;
  }

//*************************************** Utility Methods ***************************************//

protected void updateRotation(){throw new RuntimeException("Call on undeclared method!!");}

protected void clientNetworkUpdate(){throw new RuntimeException("Call on undeclared method!!");}

protected void serverNetworkSynch(){throw new RuntimeException("Call on undeclared method!!");}

protected abstract void handleClientRotationData(ForgeDirection side, int value);

/**
 * @return the TOTAL amount stored in the entire tile (not just one side), used by on-right-click functionality
 */
protected abstract double getTotalTorque();

/**
 * @return the total output of torque for the tick
 */
protected double getTorqueOut(){return torqueOut;}

/**
 * @return the total input of torque for the tick
 */
protected double getTorqueIn(){return torqueIn;}

/**
 * @return the total torque lost (destroyed, gone completely) for the tick
 */
protected double getTorqueLoss(){return torqueLoss;}

protected final void serverNetworkUpdate()
  {
  networkUpdateTicks--;
  if(networkUpdateTicks<=0)
    {
    networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
    serverNetworkSynch();
    }
  }

protected void sendSideRotation(ForgeDirection side, int value)
  {
  int sideBits = side.ordinal();
  int valueBits = (value & 0x00ffffff) | ((sideBits & 0xff) << 24);
  sendDataToClient(0, valueBits);
  }

protected final double transferPowerTo(ForgeDirection from)
  {
  double transferred = 0;
  ITorqueTile[] tc = getTorqueCache();
  if(tc[from.ordinal()]!=null)
    {
    if(tc[from.ordinal()].canInputTorque(from.getOpposite()))
      {
      return drainTorque(from, tc[from.ordinal()].addTorque(from.getOpposite(), getMaxTorqueOutput(from)));      
      }
    }
  else
    {
    if(ModuleStatus.redstoneFluxEnabled)
      {
      transferred = RFProxy.instance.transferPower(this, from, getRFCache()[from.ordinal()]);
      if(transferred>0){return transferred;}
      }
    if(ModuleStatus.buildCraftLoaded)
      {
      transferred = BCProxy.instance.transferPower(this, from, getBCCache()[from.ordinal()]);
      if(transferred>0){return transferred;}
      }
    }
  return transferred;
  }

protected final void sendDataToClient(int type, int data)
  {
  worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), type, data);
  }

protected final float getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (float)(prevRotation + rd*delta);
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  if(worldObj.isRemote)
    {
    if(a==0)
      {
      int side = (b & 0xff000000) >> 24;
      int val = b & 0x00ffffff;
      networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
      handleClientRotationData(ForgeDirection.values()[side], val);
      }    
    }
  return true;//
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
