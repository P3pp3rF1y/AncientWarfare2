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
import net.shadowmage.ancientwarfare.automation.proxy.RFProxy;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketBlockEvent;
import cofh.api.energy.IEnergyHandler;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList(value=
  {
  @Optional.Interface(iface="cofh.api.energy.IEnergyHandler", modid="CoFHCore",striprefs=true),
  })
public abstract class TileTorqueBase extends TileEntity implements ITorqueTile, IInteractableTile, IRotatableTile, IEnergyHandler
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
  return canOutputTorque(from) || canInputTorque(from);
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate)
  {
  return 0;
  }

@Optional.Method(modid="CoFHCore")
@Override
public final int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate)
  {
  if(!canInputTorque(from)){return 0;}
  if(simulate){return Math.min(maxReceive, (int)(AWAutomationStatics.torqueToRf * getMaxTorqueInput(from)));}
  return (int)(AWAutomationStatics.torqueToRf * addTorque(from, (double)maxReceive * AWAutomationStatics.rfToTorque));
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

private void buildTorqueCache()
  {
  ITorqueTile[] torqueCache = new ITorqueTile[6];
  if(worldObj==null){throw new RuntimeException("Attempt to build neighbor cache on null world!!");}
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
      torqueCache[i]=itt;
      }
    }
  this.torqueCache = torqueCache;
  }

private void buildRFCache()
  {
  TileEntity[] rfCache = new TileEntity[6];
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
  this.rfCache = rfCache;
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
  int valueBits = (value & 0xff);
  sendDataToClient(side.ordinal(), valueBits); 
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
    }
  return transferred;
  }

protected final double applyPowerDrain(TorqueCell cell)
  {
  double e = cell.getEnergy();  
  double eff = 1.d - cell.getEfficiency();
  double drain = eff * e;
  e -= drain;
  cell.setEnergy(e);  
  return drain;
  }

protected final void sendDataToClient(int type, int data)
  {
  PacketBlockEvent pkt = new PacketBlockEvent();
  pkt.setParams(xCoord, yCoord, zCoord, getBlockType(), (short)type, (short)data);
  NetworkHandler.sendToAllTrackingChunk(worldObj, xCoord >> 4 , zCoord >> 4, pkt);
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
    if(a < 6)
      {
      int side = a;
      int val = b;      
      networkUpdateTicks = AWAutomationStatics.energyMinNetworkUpdateFrequency;
      handleClientRotationData(ForgeDirection.values()[side], val);
      }    
    }
  return true;
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
