package net.shadowmage.ancientwarfare.structure.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.ModuleStatus;
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

public void onBlockBroken()
  {
  if(!worldObj.isRemote && !isStarted && builder!=null && builder.getTemplate()!=null)
    {
    isStarted = true;//to prevent further drops
    ItemStack item = new ItemStack(AWBlocks.builderBlock);
    item.setTagInfo("structureName", new NBTTagString(builder.getTemplate().name));
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  super.readFromNBT(tag);  
  if(tag.hasKey("builder"))
    {
    builder = new StructureBuilderTicked();    
    builder.readFromNBT(tag.getCompoundTag("builder"));    
    }
  else
    {
    this.shouldRemove = true;
    }
  this.isStarted = tag.getBoolean("started");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  if(builder!=null)
    {
    NBTTagCompound builderTag = new NBTTagCompound();
    builder.writeToNBT(builderTag);  
    tag.setTag("builder", builderTag);    
    }
  tag.setBoolean("started", isStarted);
  }

//*******************************************WORKSITE************************************************//
@Override
public boolean hasWork()
  {
  return true;
  }

@Override
public void doWork(IWorker worker)
  {
  processWork();
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
