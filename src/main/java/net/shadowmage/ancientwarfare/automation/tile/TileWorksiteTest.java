package net.shadowmage.ancientwarfare.automation.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.config.AWLog;

public class TileWorksiteTest extends TileWorksiteBase
{

int workCount = 100;

public TileWorksiteTest()
  {
  
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
public WorkType getWorkType()
  {
  return WorkType.FORESTRY;
  }

@Override
public void writeClientData(NBTTagCompound tag)
  {
  
  }

@Override
public void readClientData(NBTTagCompound tag)
  {
  
  }

}
