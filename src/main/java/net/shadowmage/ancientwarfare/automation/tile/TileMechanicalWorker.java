package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueReceiver;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class TileMechanicalWorker extends TileEntity implements IWorker, IInteractableTile, IOwnable, ITorqueReceiver
{

public static final double maxEnergyStored = 1500.d;
public static final double maxReceivedPerTick = 100.d;
public static final double idleConsumption = 0.d;

public double storedEnergy;

private int searchDelay;

String ownerName;//TODO load/save

public TileMechanicalWorker()
  {
  
  }

@Override
public String toString()
  {
  return "Mechanical Worker ["+storedEnergy+"]";
  }

public double getEnergyStored()
  {
  return storedEnergy;
  }

@Override
public boolean canUpdate()
  {  
  return true;
  }

@Override
public void updateEntity()
  {  
  if(worldObj.isRemote)
    {
    return;
    }
  }

@Override
public float getWorkEffectiveness()
  {
  return 1.f;
  }

@Override
public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
  }

@Override
public BlockPosition getPosition()
  {
  return new BlockPosition(xCoord,yCoord,zCoord);
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
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_MECHANICAL_WORKER, xCoord, yCoord, zCoord);
    }
  return false;
  }

public final void setOwnerName(String name)
  {
  this.ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public double getMaxEnergy()
  {
  return maxEnergyStored;
  }

@Override
public void setEnergy(double energy)
  {
  storedEnergy = energy;
  }

@Override
public double getMaxInput()
  {
  return maxReceivedPerTick;
  }

@Override
public boolean canInput(ForgeDirection from)
  {
  return from==ForgeDirection.getOrientation(getBlockMetadata()).getOpposite();
  }

@Override
public double addEnergy(ForgeDirection from, double energy)
  {
  if(canInput(from))
    {
    if(energy+getEnergyStored()>getMaxEnergy())
      {
      energy = getMaxEnergy()-getEnergyStored();
      }
    if(energy>getMaxInput())
      {
      energy = getMaxInput();
      }
    storedEnergy+=energy;
    return energy;    
    }
  return 0;
  }

@Override
public boolean canWorkAt(WorkType type)
  {
  return true;
  }

}
