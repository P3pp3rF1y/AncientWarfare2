package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;

public class TileWorkerBC extends TileWorkerTest implements IPipeConnection
{

@MjBattery(maxCapacity=1500d,maxReceivedPerCycle=100d,minimumConsumption=0.01d)
double mj;

public TileWorkerBC()
  {
  }

@Override
public void updateEntity()
  {  
  super.updateEntity();
  }

@Override
protected void attemptWork()
  {  
  AWLog.logDebug("worker attempting work..");
  AWLog.logDebug("mj...:"+mj);
  if(getWorkSite().hasWork() && mj>20)
    {
    mj-=20;
    AWLog.logDebug("worksite had work, processing doWork");
    getWorkSite().doWork(this);      
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {  
  super.writeToNBT(tag);  
  }

@Override
public ConnectOverride overridePipeConnection(PipeType arg0, ForgeDirection arg1)
  {
  return arg0==PipeType.POWER ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
  }

}
