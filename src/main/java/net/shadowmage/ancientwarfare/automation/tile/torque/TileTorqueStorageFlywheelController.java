package net.shadowmage.ancientwarfare.automation.tile.torque;

import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;


public class TileTorqueStorageFlywheelController extends TileTorqueStorageBase
{

private boolean powered;

/**
 * client side this == 0.0 -> 1.0
 */
double clientEnergyState;

/**
 * server side this == 0 -> 100 (integer percent)
 * client side this == 0.0 -> 1.0 (actual percent)
 */
double clientDestEnergyState;

/**
 * used client side for rendering
 */
double rotation, prevRotation;
TorqueCell inputCell, outputCell;

public TileTorqueStorageFlywheelController()
  {
  inputCell = new TorqueCell(32, 32, 32, 1.f);//TODO set default values from config
  outputCell = new TorqueCell(32, 32, 32, 1.f);//TODO set default values from config
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(!worldObj.isRemote)
    {    
    serverNetworkUpdate();    
    torqueIn = getTotalTorque() - prevEnergy;
    balancePower();
    torqueOut = transferPowerTo(getPrimaryFacing());
    prevEnergy = getTotalTorque();
    }
  else
    {
    clientNetworkUpdate();
    updateRotation();
    }
  }

protected void balancePower()
  {
  /**
   * fill output from input
   * fill output from storage
   * fill storage from input
   */
  TileFlywheelStorage storage = getControlledFlywheel();
  double in = inputCell.getEnergy();
  double out = outputCell.getEnergy();
  double transfer = outputCell.getMaxEnergy()-out;  
  transfer = Math.min(in, transfer);
  in-=transfer;
  out+=transfer;
  if(storage!=null)
    {
    double store = storage.storedEnergy;    
    transfer = Math.min(store, outputCell.getMaxEnergy() - out);
    store-=transfer;
    out+=transfer;
    
    transfer = Math.min(in, storage.maxEnergyStored-store);
    in-=transfer;
    store+=transfer;
    storage.storedEnergy=store;
    }  
  outputCell.setEnergy(out);
  inputCell.setEnergy(in);  
  }

@Override
protected void serverNetworkSynch()
  {
  int percent = (int)(outputCell.getPercentFull()*100.d);
  int percent2 = (int)((torqueOut / outputCell.getMaxOutput())*100.d);
  percent = Math.max(percent, percent2);  
  if(percent != clientDestEnergyState)
    {
    clientDestEnergyState = percent;
    sendSideRotation(getPrimaryFacing(), percent);    
    }
  }

@Override
protected void updateRotation()
  {
  prevRotation = rotation;
  if(clientEnergyState > 0)
    {
    double r = AWAutomationStatics.low_rpt * clientEnergyState;
    rotation += r;
    }
  }

@Override
protected void clientNetworkUpdate()
  {
  if(clientEnergyState != clientDestEnergyState)
    {
    if(networkUpdateTicks>=0)
      {
      clientEnergyState += (clientDestEnergyState - clientEnergyState) / ((double)networkUpdateTicks+1.d);
      networkUpdateTicks--;
      }
    else
      {
      clientEnergyState = clientDestEnergyState;
      }
    }
  }

@Override
protected void handleClientRotationData(ForgeDirection side, int value)
  {
  clientDestEnergyState = ((double)value) * 0.01d;
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

public float getFlywheelRotation(float delta)
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0: getRotation(storage.rotation, storage.prevRotation, delta);
  }

protected double getFlywheelEnergy()
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0 : storage.storedEnergy;//TODO
  }

@Override
protected double getTotalTorque()
  {
  return inputCell.getEnergy()+outputCell.getEnergy()+getFlywheelEnergy();
  }

@Override
public void onNeighborTileChanged()
  {
  super.onNeighborTileChanged();
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
public boolean useOutputRotation(ForgeDirection from){return true;}

@Override
public float getClientOutputRotation(ForgeDirection from, float delta){return getRotation(rotation, prevRotation, delta);}

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
public boolean canInputTorque(ForgeDirection from){return from==orientation.getOpposite();}

@Override
public boolean canOutputTorque(ForgeDirection towards){return towards==orientation;}

@Override
public double getMaxTorqueOutput(ForgeDirection from)
  {
  if(powered){return 0;}
  return outputCell.getMaxTickOutput();
  }

@Override
public double getMaxTorque(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxEnergy();
  }

@Override
public double getTorqueStored(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getEnergy();
  }

@Override
public double addTorque(ForgeDirection from, double energy)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.addEnergy(energy);
  }

@Override
public double drainTorque(ForgeDirection from, double energy)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.drainEnergy(energy);
  }

@Override
public double getMaxTorqueInput(ForgeDirection from)
  {
  TorqueCell cell = getCell(from);
  return cell==null ? 0 : cell.getMaxTickInput();
  }

private TorqueCell getCell(ForgeDirection from)
  {
  if(from==orientation){return outputCell;}
  else if(from==orientation.getOpposite()){return inputCell;}
  return null;
  }

//*************************************** NBT / DATA PACKET ***************************************//
@Override
public NBTTagCompound getDescriptionTag()
  {
  NBTTagCompound tag = super.getDescriptionTag();
  tag.setBoolean("powered", powered);  
  tag.setInteger("clientEnergy", (int)clientDestEnergyState);
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  powered = tag.getBoolean("powered");
  clientDestEnergyState = ((double)tag.getInteger("clientEnergy")) / 100.d;
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);
  tag.setBoolean("powered", powered);
  tag.setDouble("torqueEnergyIn", inputCell.getEnergy());
  tag.setDouble("torqueEnergyOut", outputCell.getEnergy());
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  powered = tag.getBoolean("powered");  
  inputCell.setEnergy(tag.getDouble("torqueEnergyIn"));
  outputCell.setEnergy(tag.getDouble("torqueEnergyOut"));
  }

}
