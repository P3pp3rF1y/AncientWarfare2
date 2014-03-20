package net.shadowmage.ancientwarfare.automation.tile;

import java.lang.ref.WeakReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class TileWorksiteTest extends TileEntity implements IWorkSite
{

WeakReference<IWorker> workerRef = new WeakReference<IWorker>(null);

int workCount = 100;

public TileWorksiteTest()
  {
  
  }

protected IWorker getWorker()
  {
  return workerRef==null? null : workerRef.get();
  }

protected void setWorker(IWorker worker)
  {
  workerRef = new WeakReference<IWorker>(worker);
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  if(workCount>0)
    {
    workCount--;    
    }
  }

@Override
public boolean hasWork()
  {
  return workCount>0;
  }

@Override
public void doWork(IWorker worker)
  {
  AWLog.logDebug("doing work!!");
  if(workCount>0)
    {
    workCount--;    
    }
  }

@Override
public boolean canHaveWorker(IWorker worker)
  {
  return worker!=null && worker.canWorkAt(this) && (getWorker()==null || getWorker()==worker);
  }

@Override
public boolean addWorker(IWorker worker)
  {
  AWLog.logDebug("adding worker: "+worker);
  if(canHaveWorker(worker))
    {
    setWorker(worker);
    return true;
    }
  return false;
  }

@Override
public void removeWorker(IWorker worker)
  {
  if(worker==getWorker())
    {
    setWorker(null);
    }
  }

}
