package net.shadowmage.ancientwarfare.core.tile;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileResearchStation extends TileEntity implements IWorkSite
{

private Set<IWorker> workers = Collections.newSetFromMap( new WeakHashMap<IWorker, Boolean>());

protected String owningPlayer;

public InventoryBasic bookInventory = new InventoryBasic(1);
public InventoryBasic resourceInventory = new InventoryBasic(9);

int maxWorkers = 2;

int startCheckDelay = 0;
int startCheckDelayMax = 40;

public boolean useAdjacentInventory;

public TileResearchStation()
  {
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

public String getCrafterName()
  {
  return ItemResearchBook.getResearcherName(bookInventory.getStackInSlot(0));
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
    
  if(startCheckDelay>0)
    {
    startCheckDelay--;
    }
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  InventoryTools.readInventoryFromNBT(bookInventory, tag.getCompoundTag("bookInventory"));
  InventoryTools.readInventoryFromNBT(resourceInventory, tag.getCompoundTag("resourceInventory"));
//  InventoryTools.readInventoryFromNBT(result, tag.getCompoundTag("resultInventory"));
//  InventoryTools.readInventoryFromNBT(layoutMatrix, tag.getCompoundTag("layoutMatrix"));
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  
  NBTTagCompound inventoryTag = new NBTTagCompound();
  InventoryTools.writeInventoryToNBT(bookInventory, inventoryTag);
  tag.setTag("bookInventory", inventoryTag);
  
  inventoryTag = new NBTTagCompound();
  InventoryTools.writeInventoryToNBT(resourceInventory, inventoryTag);
  tag.setTag("resourceInventory", inventoryTag);
  
//  inventoryTag = new NBTTagCompound();
//  InventoryTools.writeInventoryToNBT(result, inventoryTag);
//  tag.setTag("resultInventory", inventoryTag);
//
//  inventoryTag = new NBTTagCompound();
//  InventoryTools.writeInventoryToNBT(layoutMatrix, inventoryTag);
//  tag.setTag("layoutMatrix", inventoryTag);
  
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
public void doPlayerWork(EntityPlayer player)
  {
  workTick(1);
  }

@Override
public boolean hasWork()
  {
  String name = getCrafterName();
  if(name==null){return false;}
  int goal = ResearchTracker.instance().getCurrentGoal(worldObj, name);
  if(goal>=0){return true;}
  List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, name);  
  if(!queue.isEmpty() && startCheckDelay==0){return true;}
  return false;
  }

@Override
public void doWork(IWorker worker)
  {
  workTick((int)worker.getWorkEffectiveness());
  }

private void workTick(int tickCount)
  {
  String name = getCrafterName();
  if(name==null){return;}
  int goal = ResearchTracker.instance().getCurrentGoal(worldObj, name);
  if(goal>=0)
    {
    ResearchGoal g1 = ResearchGoal.getGoal(goal);
    int progress = ResearchTracker.instance().getProgress(worldObj, name);
    progress+=tickCount;
    if(progress>=g1.getTotalResearchTime())
      {
      ResearchTracker.instance().finishResearch(worldObj, getCrafterName(), goal);
      tryStartNextResearch(name);
      }
    else
      {
      ResearchTracker.instance().setProgress(worldObj, name, progress);
      }
    }
  else
    {
    tryStartNextResearch(name);
    }    
  }

private void tryStartNextResearch(String name)
  {
  List<Integer> queue = ResearchTracker.instance().getResearchQueueFor(worldObj, name);    
  if(!queue.isEmpty())
    {
    int g = queue.get(0);
    ResearchGoal g1 = ResearchGoal.getGoal(g);
    if(g1==null){return;}
    if(g1.tryStart(resourceInventory, -1))
      {
      ResearchTracker.instance().startResearch(worldObj, getCrafterName(), g);        
      }
    } 
  startCheckDelay = startCheckDelayMax;
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.RESEARCH;
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
