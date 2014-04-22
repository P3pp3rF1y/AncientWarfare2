package net.shadowmage.ancientwarfare.structure.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBuilderTicked;

public class TileStructureBuilder extends TileEntity implements IWorkSite
{

private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());
protected String owningPlayer;
int maxWorkers = 2;

StructureBuilderTicked builder;
private boolean shouldRemove = false;
public boolean isStarted = false;
int workDelay = 20;

public TileStructureBuilder()
  {
  
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(builder==null || builder.invalid || builder.isFinished())
    {
    shouldRemove = true;
    }
  if(builder.getWorld()==null){builder.setWorld(worldObj);}
  if(shouldRemove)
    {
    worldObj.setBlock(xCoord, yCoord, zCoord, Blocks.air);
    return;
    }
  if(ModuleStatus.automationLoaded){return;}
  if(workDelay>0)
    {
    workDelay--;
    }
  if(workDelay<=0)
    {
    AWLog.logDebug("processing work for ticked builder...");
    processWork();
    workDelay=20;
    }
  }

public void processWork()
  {
  isStarted = true;
  builder.tick();
  }

/**
 * should be called immediately after the tile-entity is set into the world
 * from the ItemBlockStructureBuilder item onBlockPlaced code
 * @param builder
 */
public void setOwnerName(String name)
  {
  this.owningPlayer = name;
  }

/**
 * should be called immediately after the tile-entity is set into the world
 * from the ItemBlockStructureBuilder item onBlockPlaced code<br>
 * the passed in builder must be valid (have a valid structure), and must not
 * be null
 * @param builder
 */
public void setBuilder(StructureBuilderTicked builder)
  {
  this.builder = builder;
  }

@Override
public void readFromNBT(NBTTagCompound p_145839_1_)
  {  
  super.readFromNBT(p_145839_1_);  
  if(p_145839_1_.hasKey("builder"))
    {
    builder = new StructureBuilderTicked();    
    builder.readFromNBT(p_145839_1_.getCompoundTag("builder"));    
    }
  else
    {
    this.shouldRemove = true;
    }
  }

@Override
public void writeToNBT(NBTTagCompound p_145841_1_)
  {
  super.writeToNBT(p_145841_1_);
  if(builder!=null)
    {
    NBTTagCompound builderTag = new NBTTagCompound();
    builder.writeToNBT(builderTag);  
    p_145841_1_.setTag("builder", builderTag);    
    }
  }

//*******************************************WORKSITE************************************************//
@Override
public boolean hasWork()
  {
  //TODO
  return false;
  }

@Override
public void doWork(IWorker worker)
  {
  //TODO  
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  //noop
  }

@Override
public final boolean canHaveWorker(IWorker worker)
  {
  if(!worker.getWorkTypes().contains(getWorkType()) || worker.getTeam() != this.getTeam())
    {
    return false;
    }
  if(workers.contains(worker))
    {
    return true;
    }
  return workers.size()<maxWorkers;
  }

@Override
public final boolean addWorker(IWorker worker)
  {
  if(workers.size()<maxWorkers || workers.contains(worker))
    {
    workers.add(worker);
    return true;
    }
  return false;
  }

@Override
public final void removeWorker(IWorker worker)
  {
  workers.remove(worker);
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.CONSTRUCTION;
  }

@Override
public final Team getTeam()
  {  
  if(owningPlayer!=null)
    {
    worldObj.getScoreboard().getPlayersTeam(owningPlayer);
    }
  return null;
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

}
