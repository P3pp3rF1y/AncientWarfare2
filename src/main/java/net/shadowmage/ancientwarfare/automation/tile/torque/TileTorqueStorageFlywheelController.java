package net.shadowmage.ancientwarfare.automation.tile.torque;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.block.AWAutomationBlockLoader;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.BlockFinder;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;


public class TileTorqueStorageFlywheelController extends TileTorqueStorageBase
{

private boolean powered;

double clientRotation;
double prevClientRotation;

double flywheelEnergy;
public int controlHeight, controlSize, controlType;
public Set<BlockPosition> controlSet = new HashSet<BlockPosition>();//set of blocks under this controller, only non-empty when set is valid

public TileTorqueStorageFlywheelController()
  {
  energyDrainFactor = AWAutomationStatics.low_drain_factor;
  maxEnergy = AWAutomationStatics.low_storage_energy_max;
  maxOutput = AWAutomationStatics.low_transfer_max;
  maxInput = AWAutomationStatics.low_transfer_max;
  maxRpm = 100;
  }

protected void initializeController(int w, int l, int h, Set<BlockPosition> controlledSet, int type)
  {
  AWLog.logDebug("initializing controller: "+controlledSet);
  this.controlHeight = h;
  this.controlSize = w;
  this.controlType = type;
  if(!controlSet.isEmpty())
    {
    invalidateSetup();
    }
  controlSet.addAll(controlledSet);  
  for(BlockPosition pos : controlSet)
    {
    TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileFlywheelStorage){((TileFlywheelStorage) te).setController(this);}
    }
  }

public void initializeFromStoragePlaced()
  {
  invalidateSetup();
  validateSetup();
  }

public void invalidateSetup()
  {
  AWLog.logDebug("invalidating setup in controller..");
  if(controlSet!=null)
    {
    TileEntity te;
    for(BlockPosition pos : controlSet)
      {
      te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
      if(te instanceof TileFlywheelStorage)
        {
        ((TileFlywheelStorage) te).setController(null);
        }
      }
    }
  controlHeight = 0;
  controlSize = 0;
  controlType = -1;
  controlSet.clear();
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

public void blockBroken()
  {
  AWLog.logDebug("controller broken...");
  invalidateSetup();  
  }

public void blockPlaced()
  {
  AWLog.logDebug("controller placed...");
  validateSetup();
  }

protected void validateSetup()
  {
  worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
  Set<BlockPosition> connectedPosSet = new HashSet<BlockPosition>(); 
  Block block = worldObj.getBlock(xCoord, yCoord-1, zCoord);
  int meta = worldObj.getBlockMetadata(xCoord, yCoord-1, zCoord);
  if(block!=AWAutomationBlockLoader.flywheelStorage)
    {
    return;
    }
  BlockFinder.findConnectedSixWay(worldObj, xCoord, yCoord-1, zCoord, block, meta, connectedPosSet);
  AWLog.logDebug("found connected blocks: "+connectedPosSet);
  int minX=Integer.MAX_VALUE, minY=Integer.MAX_VALUE, minZ=Integer.MAX_VALUE, maxX=Integer.MIN_VALUE, maxY=Integer.MIN_VALUE, maxZ=Integer.MIN_VALUE;
  boolean valid = false;
  for(BlockPosition pos : connectedPosSet)
    {
    if(pos.x<minX){minX=pos.x;}
    if(pos.x>maxX){maxX=pos.x;}
    if(pos.y<minY){minY=pos.y;}
    if(pos.y>maxY){maxY=pos.y;}
    if(pos.z<minZ){minZ=pos.z;}
    if(pos.z>maxZ){maxZ=pos.z;}
    }
  int w = maxX-minX + 1;
  int l = maxZ-minZ + 1;
  int h = maxY-minY + 1;
  int cube = w*l*h;
  if(cube==connectedPosSet.size() && ((w==1 && l==1) || (w==3 && l==3)) ){valid = true;}
  AWLog.logDebug("wlh: "+w+","+l+","+h+" c: "+cube+" a: "+connectedPosSet.size()+" valid: "+valid);
  if(valid)    
    {    
    int cx = w==1? minX : minX + 1;
    int cz = l==1? minZ : minZ + 1;
    int cy = maxY + 1;    
    if(cx==xCoord && cy==yCoord && cz==zCoord)
      {
      AWLog.logDebug("valid setup...");
      AWLog.logDebug("control in proper position, initializing controller!");
      initializeController(w, l, h, connectedPosSet, meta);
      }
    }  
  }


@Override
public boolean useClientRotation()
  {
  return true;
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
  if(!controlSet.isEmpty())
    {
    tag.setInteger("controlHeight", controlHeight);
    tag.setInteger("controlSize", controlSize);
    tag.setInteger("controlType", controlType);
    NBTTagList list = new NBTTagList();
    for(BlockPosition pos : controlSet)
      {
      list.appendTag(pos.writeToNBT(new NBTTagCompound()));
      }
    tag.setTag("controlSet", list);
    }
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  powered = tag.getBoolean("powered");
  controlSet.clear();
  if(tag.hasKey("controlSet"))
    {
    controlHeight = tag.getInteger("controlHeight");
    controlSize = tag.getInteger("controlSize");
    controlType = tag.getInteger("controlType");
    NBTTagList list = tag.getTagList("controlSet", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i< list.tagCount(); i++)
      {
      controlSet.add(new BlockPosition(list.getCompoundTagAt(i)));
      }
    }
  else
    {
    controlHeight = 0;
    controlSize = 0;
    controlType = -1;
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setBoolean("powered", powered);
  if(!controlSet.isEmpty())
    {
    tag.setInteger("controlHeight", controlHeight);
    tag.setInteger("controlSize", controlSize);
    tag.setInteger("controlType", controlType);
    NBTTagList list = new NBTTagList();
    for(BlockPosition pos : controlSet)
      {
      list.appendTag(pos.writeToNBT(new NBTTagCompound()));
      }
    tag.setTag("controlSet", list);
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  powered = tag.getBoolean("powered");
  controlSet.clear();
  if(tag.hasKey("controlSet"))
    {
    controlHeight = tag.getInteger("controlHeight");
    controlSize = tag.getInteger("controlSize");
    controlType = tag.getInteger("controlType");
    NBTTagList list = tag.getTagList("controlSet", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i< list.tagCount(); i++)
      {
      controlSet.add(new BlockPosition(list.getCompoundTagAt(i)));
      }
    }
  else
    {
    controlHeight = 0;
    controlSize = 0;
    controlType = -1;
    }
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
