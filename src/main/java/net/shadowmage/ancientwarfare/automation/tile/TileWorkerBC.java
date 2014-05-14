package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import buildcraft.api.mj.MjBattery;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.common.Optional;

@Optional.Interface(iface="buildcraft.api.transport.IPipeConnection",modid="BuildCraft|Core",striprefs=true)
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
  if(getWorkSite().hasWork())
    {
    AWLog.logDebug("worksite had work");
    boolean work = true;
    if(ModuleStatus.buildCraftLoaded)
      {
      AWLog.logDebug("BuildCraft detected, worker mj...:"+mj);
      if(mj>=20)
        {
        work = true;
        mj-=20;
        }
      }
    if(work)
      {
      AWLog.logDebug("processing work command");
      getWorkSite().doWork(this);
      }          
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

@Optional.Method(modid="BuildCraft|Core")
@Override
public ConnectOverride overridePipeConnection(PipeType arg0, ForgeDirection arg1)
  {
  return arg0==PipeType.POWER ? ConnectOverride.CONNECT : ConnectOverride.DISCONNECT;
  }

}
