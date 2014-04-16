package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class WorkSiteAnimalFarm extends TileWorksiteBase
{

int carrotCount;
List<EntityPair> pigsToBreed = new ArrayList<EntityPair>();

int seedCount;
List<EntityPair> chickensToBreed = new ArrayList<EntityPair>();

int wheatCount;
List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();

int workerRescanDelay;
boolean shouldCountResources;

int maxPigCount = 6;
int maxCowCount = 6;

List<Integer> entitiesToCull = new ArrayList<Integer>();

public WorkSiteAnimalFarm()
  {
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldCountResources = true;  
  this.inventory = new InventorySided(27 + 3, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 8, "guistrings.inventory.side.top");
  for(int i =0; i <27; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
    
  SlotItemFilter filter = new SlotItemFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      Item item = stack.getItem();
      if(item==Items.wheat_seeds || item==Items.wheat || item==Items.carrot)
        {       
        return true;
        }      
      return false;
      }
    };
  this.inventory.addSlotViewMap(InventorySide.FRONT, 8, (3*18)+12+8, "guistrings.inventory.side.front");
  for(int i = 27, k = 0; i<30; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    this.inventory.addSlotFilter(i, filter);
    }
  }

@Override
public void updateEntity()
  {
  super.updateEntity();
  if(worldObj.isRemote){return;}
  if(workerRescanDelay>0){workerRescanDelay--;}
  if(shouldCountResources){countResources();}
  }

private void countResources()
  {
  this.shouldCountResources = false;
  carrotCount = 0;
  seedCount = 0;
  wheatCount = 0;
  ItemStack stack;
  for(int i = 27; i < 30; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem()==Items.carrot)
      {
      carrotCount+=stack.stackSize;
      }
    else if(stack.getItem()==Items.wheat_seeds)
      {
      seedCount+=stack.stackSize;
      }
    else if(stack.getItem()==Items.wheat)
      {
      wheatCount+=stack.stackSize;
      }
    }
  }

private void rescan()
  {
  pigsToBreed.clear();
  cowsToBreed.clear();
  sheepToBreed.clear();
  chickensToBreed.clear();
  entitiesToCull.clear();
  
  Set<Integer> usedIDs = new HashSet<Integer>();
  BlockPosition min = getWorkBoundsMin();
  BlockPosition max = getWorkBoundsMax();
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
  
  List<EntityAnimal> entityList = worldObj.getEntitiesWithinAABB(EntityPig.class, bb);
  Iterator<EntityAnimal> it = entityList.iterator();
  
  List<EntityAnimal> breedableList = new ArrayList<EntityAnimal>();
  List<EntityAnimal> cullableList = new ArrayList<EntityAnimal>();
  
  int grownCount = 0;
  
  EntityAnimal animal; 
  /**
   * need to loop through entities
   */
  }

private void processWork()
  {
  //TODO
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@Override
public void doWork(IWorker worker)
  {
  if(workerRescanDelay<=0 || !hasAnimalWork())
    {
    rescan();
    }  
  if(hasAnimalWork())
    {
    processWork();
    }
  }

@Override
public boolean hasWork()
  {
  return canWork() && (workerRescanDelay<=0 || hasAnimalWork());
  }

private boolean hasAnimalWork()
  {
  return !entitiesToCull.isEmpty() 
      || (carrotCount>0 && !pigsToBreed.isEmpty()) 
      || (seedCount>0 && !chickensToBreed.isEmpty())
      || (wheatCount>0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty()));
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.ANIMAL_HUSBANDRY;
  }

@Override
public void onInventoryChanged()
  {
  this.shouldCountResources = true;
  }

@Override
public void doPlayerWork(EntityPlayer player)
  {
  //noop
  }

@Override
public void initWorkSite()
  {
  //noop
  }

@Override
protected void addWorkTargets(List<BlockPosition> targets)
  {
  //noop
  }

@Override
public void writeClientData(NBTTagCompound tag)
  {
  //noop
  }

@Override
public void readClientData(NBTTagCompound tag)
  {
  //noop
  }

private static class EntityPair
{
int idA;
int idB;
private EntityPair(int a, int b)
  {
  idA = a;
  idB = b;
  }

public Entity getEntityA(World world)
  {
  return world.getEntityByID(idA);
  }

public Entity getEntityB(World world)
  {
  return world.getEntityByID(idB);
  }
}

}
