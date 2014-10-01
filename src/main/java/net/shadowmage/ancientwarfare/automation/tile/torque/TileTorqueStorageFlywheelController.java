package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;


public class TileTorqueStorageFlywheelController extends TileTorqueStorageBase
{

private boolean powered;
double clientRotation;
double prevClientRotation;
TorqueCell inputCell, outputCell;

public TileTorqueStorageFlywheelController()
  {
  inputCell = new TorqueCell(32, 32, 32, 1.f);//TODO set default values from config
  outputCell = new TorqueCell(32, 32, 32, 1.f);//TODO set default values from config
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

public double getInputRotation(){return 0;}//TODO

public double getInputPrevRotation(){return 0;}//TODO

public double getFlywheelRotation()
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0: storage.rotation;
  }

public double getFlywheelPrevRotation()
  {
  TileFlywheelStorage storage = getControlledFlywheel();
  return storage==null ? 0: storage.prevRotation;
  }

//TODO clean these up, along with all other energy accessors?
//@Override
//public double getEnergyStored()
//  {
//  TileFlywheelStorage storage = getControlledFlywheel();
//  return storage==null ? 0 : storage.storedEnergy;
//  }
//
//@Override
//public double getMaxEnergy()
//  {
//  TileFlywheelStorage storage = getControlledFlywheel();
//  return storage==null ? 0 : storage.maxEnergyStored;
//  }

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
public float getClientOutputRotation(ForgeDirection from, float delta){return getRotation(clientRotation, prevClientRotation, delta);}

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
  return outputCell.getMaxOutput();
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
  return cell==null ? 0 : cell.getMaxInput();
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
  return tag;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  super.onDataPacket(net, pkt);
  NBTTagCompound tag = pkt.func_148857_g();
  powered = tag.getBoolean("powered");
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
