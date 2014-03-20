package net.shadowmage.ancientwarfare.automation.tile;

import java.lang.ref.WeakReference;
import java.util.EnumSet;

import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class TileWorkerTest extends TileEntity implements IWorker
{

WeakReference<IWorkSite> workSite = new WeakReference<IWorkSite>(null);

int searchDelay = 0;
int workDelay = 0;

public TileWorkerTest()
  {

  }

private void setWorkSite(IWorkSite site)
  {
  workSite = new WeakReference<IWorkSite>(site);
  if(workSite==null)
    {
    searchDelay = 40;
    workDelay = -1;
    }
  }

private IWorkSite getWorkSite()
  {
  return workSite==null ? null : workSite.get();
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
  if(searchDelay>0)
    {
    searchDelay--;
    }  
  if(searchDelay==0 && getWorkSite()==null)
    {
    if(findWorkSite())
      {
      workDelay = 40;
      searchDelay = -1;
      }
    else
      {
      searchDelay = 40;
      }
    }
  
  if(workDelay>0)
    {
    workDelay--;
    }  
  if(workDelay==0 && getWorkSite()!=null)
    {
    getWorkSite().doWork(this);
    workDelay = 40;
    }
  }

private boolean findWorkSite()
  {
  TileEntity te;
  IWorkSite site;
  for(int x = -1; x<=1; x++)
    {
    for(int z = -1; z<=1; z++)
      {
      if(z!=0 || x!=0 && (z==0 || x==0))
        {
        te = worldObj.getTileEntity(xCoord+x, yCoord, zCoord+z);
        if(te instanceof IWorkSite)
          {
          site = (IWorkSite)te;
          if(site.canHaveWorker(this) && site.addWorker(this))
            {
            this.setWorkSite(site);
            return true;
            }
          }
        }
      }
    }  
  return false;
  }

@Override
public float getWorkEffectiveness()
  {
  return 1.f;
  }

@Override
public Team getTeam()
  {
  return null;
  }

@Override
public EnumSet<WorkType> getWorkTypes()
  {
  return EnumSet.allOf(WorkType.class);
  }

}
