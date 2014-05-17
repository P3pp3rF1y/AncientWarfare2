package net.shadowmage.ancientwarfare.automation.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import buildcraft.api.mj.IOMode;
import buildcraft.api.mj.MjBattery;


public class TileFlywheel extends TileEntity implements IWorkSite, IOwnable
{

private int maxWorkers = 2;
protected String owningPlayer;
private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());
@MjBattery(maxCapacity=100000,mode=IOMode.Both)
public double storedEnergy;

//@Method(modid = "")
@Override
public void updateEntity()
  {
  if(!worldObj.isRemote)
    {
    ForgeDirection d = ForgeDirection.getOrientation(getBlockMetadata());
    TileEntity te = worldObj.getTileEntity(xCoord+d.offsetX, yCoord+d.offsetY, zCoord+d.offsetZ);
    if(te instanceof TileMechanicalWorker && !workers.contains(te))
      {
      TileMechanicalWorker tem = (TileMechanicalWorker)te;
      if(tem.storedEnergy < TileMechanicalWorker.maxEnergyStored)
        {
        double d1 = TileMechanicalWorker.maxEnergyStored-tem.storedEnergy;
        if(d1 >= (AWAutomationStatics.energyPerWorkUnit/20) )
          {
          d1 = (AWAutomationStatics.energyPerWorkUnit/20);
          }
        if(d1>storedEnergy){d1=storedEnergy;}
        if(d1>0)
          {
          storedEnergy-=d1;
          tem.storedEnergy+=d1;
          AWLog.logDebug("found mechanical worker...injecting power..."+storedEnergy);
          }
        }
      }
    }
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit;
  AWLog.logDebug("doing work...new energy: "+storedEnergy);
  }

@Override
public boolean hasWork()
  {
  return storedEnergy<100000.d;
  }

@Override
public void doWork(IWorker worker)
  {
  storedEnergy += AWAutomationStatics.energyPerWorkUnit;
  AWLog.logDebug("doing work...new energy: "+storedEnergy);
  }

@Override
public boolean addWorker(IWorker worker)
  {
  if((workers.contains(worker) || workers.size()<maxWorkers) && worker.getWorkTypes().contains(getWorkType()))
    {
    workers.add(worker);
    return true;   
    }
  return false;
  }

@Override
public void removeWorker(IWorker worker)
  {
  workers.remove(worker);
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CONSTRUCTION;//TODO make a 'generic' work type
  }

@Override
public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(owningPlayer);
  }

@Override
public BlockPosition getWorkBoundsMin()
  {
  return null;
  }

@Override
public BlockPosition getWorkBoundsMax()
  {
  return null;
  }

@Override
public List<BlockPosition> getWorkTargets()
  {
  return Collections.emptyList();
  }

@Override
public boolean hasWorkBounds()
  {
  return false;
  }

@Override
public void setOwnerName(String name)
  {
  this.owningPlayer = name;
  }

@Override
public String getOwnerName()
  {
  return owningPlayer;
  }

}
