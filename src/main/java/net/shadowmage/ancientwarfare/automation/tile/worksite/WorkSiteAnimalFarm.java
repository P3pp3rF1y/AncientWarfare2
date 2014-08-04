package net.shadowmage.ancientwarfare.automation.tile.worksite;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteAnimalFarm extends TileWorksiteBounded
{

private int workerRescanDelay;
private boolean shouldCountResources;

public int maxPigCount = 6;
public int maxCowCount = 6;
public int maxChickenCount = 6;
public int maxSheepCount = 6;

private int wheatCount;
private int bucketCount;
private int carrotCount;
private int seedCount;
private ItemStack shears = null;

private List<EntityPair> pigsToBreed = new ArrayList<EntityPair>();
private List<EntityPair> chickensToBreed = new ArrayList<EntityPair>();
private List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
private List<Integer> cowsToMilk = new ArrayList<Integer>();
private List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();
private List<Integer> sheepToShear = new ArrayList<Integer>();
private List<Integer> entitiesToCull = new ArrayList<Integer>();

public WorkSiteAnimalFarm()
  {
  this.shouldCountResources = true;  
  
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33)
    {
    @Override
    public void markDirty()
      {
      super.markDirty();
      shouldCountResources = true;
      }
    };
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
  int[] bottomIndices = InventoryTools.getIndiceArrayForSpread(30, 3);  
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//feed
  this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//buckets/shears
  ItemSlotFilter filter = new ItemSlotFilter()
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
    @Override
    public String toString()
      {
      return "Anon filter -- wheat / seeds / carrot";
      }
    };
  inventory.setFilterForSlots(filter, frontIndices);
    
  filter = new ItemSlotFilter()
    {
    @Override
    public boolean isItemValid(ItemStack stack)
      {
      if(stack==null){return true;}
      Item item = stack.getItem();
      if(item==Items.bucket || item==Items.shears)
        {       
        return true;
        }      
      return false;
      }
    @Override
    public String toString()
      {
      return "Anon filter -- bucker / shears";
      }
    };
  inventory.setFilterForSlots(filter, bottomIndices);
  }

@Override
protected boolean hasWorksiteWork()
  {
  return hasAnimalWork();
  }

@Override
protected void updateWorksite()
  {
  worldObj.theProfiler.startSection("Count Resources");  
  if(shouldCountResources){countResources();}
  worldObj.theProfiler.endStartSection("Animal Rescan");
  workerRescanDelay--;
  if(workerRescanDelay<=0){rescan();}
  worldObj.theProfiler.endStartSection("EggPickup");
  if(worldObj.getWorldTime()%20==0)
    {
    pickupEggs();
    }
  worldObj.theProfiler.endSection();
  }

private void countResources()
  {
  
  this.shouldCountResources = false;
  carrotCount = 0;
  seedCount = 0;
  wheatCount = 0;
  bucketCount = 0;
  shears =null;
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
  for(int i = 30; i < 33; i++)
    {
    stack = inventory.getStackInSlot(i);
    if(stack==null){continue;}
    if(stack.getItem()==Items.bucket)
      {
      bucketCount+=stack.stackSize;
      }
    else if(stack.getItem()==Items.shears)
      {
      shears = stack;
      }
    }
//  AWLog.logDebug("counting animal farm resources.."+wheatCount+","+seedCount+","+carrotCount+","+bucketCount+","+shears);
  }

@SuppressWarnings("unchecked")
private void rescan()
  {
//  AWLog.logDebug("rescanning animal farm");
  worldObj.theProfiler.startSection("Animal Rescan");
  pigsToBreed.clear();
  cowsToBreed.clear();
  cowsToMilk.clear();
  sheepToBreed.clear();
  chickensToBreed.clear();
  entitiesToCull.clear();
  
  BlockPosition min = getWorkBoundsMin();
  BlockPosition max = getWorkBoundsMax();
  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
  
  List<EntityAnimal> entityList = worldObj.getEntitiesWithinAABB(EntityAnimal.class, bb);
  
  List<EntityAnimal> cows = new ArrayList<EntityAnimal>();
  List<EntityAnimal> pigs = new ArrayList<EntityAnimal>();
  List<EntityAnimal> sheep = new ArrayList<EntityAnimal>();
  List<EntityAnimal> chickens = new ArrayList<EntityAnimal>();
  
  for(EntityAnimal animal : entityList)
    {
    if(animal instanceof EntityCow)
      {
      cows.add(animal);
      }
    else if(animal instanceof EntityChicken)
      {
      chickens.add(animal);
      }
    else if(animal instanceof EntitySheep)
      {
      sheep.add(animal);
      }
    else if(animal instanceof EntityPig)
      {
      pigs.add(animal);
      }
    }  
  
  scanForCows(cows);
  scanForSheep(sheep);
  scanForAnimals(chickens, chickensToBreed, maxChickenCount);
  scanForAnimals(pigs, pigsToBreed, maxPigCount);
  workerRescanDelay = 200;
  worldObj.theProfiler.endSection();
  }

private void scanForAnimals(List<EntityAnimal> animals, List<EntityPair> targets, int maxCount)
  {
  EntityAnimal animal1;
  EntityAnimal animal2;
  EntityPair breedingPair;
  
  int age;
  
  for(int i = 0; i<animals.size(); i++)
    {
    animal1 = animals.get(i);
    age = animal1.getGrowingAge();
    if(age!=0 || animal1.isInLove()){continue;}//unbreedable first-target, skip
    while(i+1<animals.size())//loop through remaining animals to find a breeding partner
      {
      i++;
      animal2 = animals.get(i);
      age = animal2.getGrowingAge();
      if(age==0 && !animal2.isInLove())//found a second breedable animal, add breeding pair, exit to outer loop
        {
        breedingPair = new EntityPair(animal1, animal2);
        targets.add(breedingPair);
        break;
        }
      }
    }
  
  int grownCount = 0;
  for(EntityAnimal animal : animals)
    {
    if(animal.getGrowingAge()>=0)
      {
      grownCount++;
      }
    }
  
  if(grownCount>maxCount)
    {
    for(int i = 0, cullCount = grownCount-maxCount; i<animals.size() && cullCount>0; i++)
      {
      if(animals.get(i).getGrowingAge()>=0)
        {
        entitiesToCull.add(animals.get(i).getEntityId());
        cullCount--;
        }
      }
    }
  }

private void scanForSheep(List<EntityAnimal> sheep)
  {
  scanForAnimals(sheep, sheepToBreed, maxSheepCount);
  for(EntityAnimal animal : sheep)
    {
    if(animal.getGrowingAge()>=0)
      {
      EntitySheep sheep1 = (EntitySheep)animal;
      if(!sheep1.getSheared())
        {
        sheepToShear.add(sheep1.getEntityId());
        }      
      }
    }
  }

private void scanForCows(List<EntityAnimal> animals)
  {  
  scanForAnimals(animals, cowsToBreed, maxCowCount);  
  for(EntityAnimal animal : animals)
    {
    if(animal.getGrowingAge()>=0)
      {
      cowsToMilk.add(animal.getEntityId());
      }
    }
  }

@Override
protected boolean processWork()
  {
//  AWLog.logDebug("processing animal farm work!");

  boolean didWork = false;
  if(!cowsToBreed.isEmpty() && wheatCount>=2)
    {
    didWork = tryBreeding(cowsToBreed);    
    if(didWork)
      {
      wheatCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
      return true;
      }
    }
  if(!sheepToBreed.isEmpty() && wheatCount>=2)
    {
    didWork = tryBreeding(sheepToBreed);
    if(didWork)
      {
      wheatCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
      return true;
      }
    }
  if(!chickensToBreed.isEmpty() && seedCount>=2)
    {
    didWork = tryBreeding(chickensToBreed);
    if(didWork)
      {
      seedCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat_seeds), 2);
      return true;
      }
    }
  if(!pigsToBreed.isEmpty() && carrotCount>=2)
    {
    didWork = tryBreeding(pigsToBreed);
    if(didWork)
      {
      carrotCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.carrot), 2);
      return true;
      }
    }
  if(shears!=null && !sheepToShear.isEmpty())
    {
    didWork = tryShearing(sheepToShear);
    if(didWork)      
      {
      return true;
      }
    }
  if(bucketCount>0 && !cowsToMilk.isEmpty())
    {
    didWork = tryMilking(cowsToMilk);
    if(didWork)
      {
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.BOTTOM), new ItemStack(Items.bucket), 1);
      this.addStackToInventory(new ItemStack(Items.milk_bucket), RelativeSide.TOP);
      return true;
      }
    }
  if(!entitiesToCull.isEmpty())
    {
    if(tryCulling(entitiesToCull))
      {
      return true;
      }
    }
  return false;
  }

private boolean tryBreeding(List<EntityPair> targets)
  {
  Entity animalA;
  Entity animalB;
  EntityPair pair;
  if(!targets.isEmpty())
    {
    pair = targets.remove(0);
    animalA = pair.getEntityA(worldObj);
    animalB = pair.getEntityB(worldObj);
    if(!(animalA instanceof EntityAnimal) || !(animalB instanceof EntityAnimal))
      {
      return false;
      }
    ((EntityAnimal)animalA).func_146082_f(null);//setInLove(EntityPlayer breeder)
    ((EntityAnimal)animalB).func_146082_f(null);//setInLove(EntityPlayer breeder)
    return true;
    }  
  return false;
  }

private boolean tryMilking(List<Integer> targets)
  {
  return false;
  }

private boolean tryShearing(List<Integer> targets)
  {
  if(targets.isEmpty()){return false;}
  EntitySheep sheep = (EntitySheep) worldObj.getEntityByID(targets.remove(0));
  if(sheep==null){return false;}
  if(sheep.getSheared()){return false;}
  ArrayList<ItemStack> items = sheep.onSheared(shears, worldObj, xCoord, yCoord, zCoord, 0);
  for(ItemStack item : items)
    {
    addStackToInventory(item, RelativeSide.TOP);
    }
  return true;
  }

private boolean tryCulling(List<Integer> targets)
  {
  int entityId;
  Entity entity;
  EntityAnimal animal;
  while(!targets.isEmpty())
    {
    entityId = targets.remove(0);
    entity = worldObj.getEntityByID(entityId);
    if(entity instanceof EntityAnimal)
      {
      animal = (EntityAnimal)entity;
      if(animal.isInLove() || animal.getGrowingAge()<0){continue;}

      animal.captureDrops = true;
      animal.captureDrops = true;
      animal.arrowHitTimer =10;
      animal.attackEntityFrom(DamageSource.generic, animal.getHealth()+1);
      ItemStack stack;
      for(EntityItem item : animal.capturedDrops)
        {
        stack = item.getEntityItem();
        this.addStackToInventory(stack, RelativeSide.TOP);       
        item.setDead();
        }
      return true;
      }
    }
  return false;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, xCoord, yCoord, zCoord);
    return true;
    }
  return false;
  }

@SuppressWarnings("unchecked")
private void pickupEggs()
  {
  BlockPosition p1 = getWorkBoundsMin();
  BlockPosition p2 = getWorkBoundsMax().copy().offset(1, 1, 1);
  AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
  List<EntityItem> items = worldObj.getEntitiesWithinAABB(EntityItem.class, bb);
  ItemStack stack;
  for(EntityItem item : items)
    {
    stack = item.getEntityItem();
    if(stack==null){continue;}
    if(stack.getItem()==Items.egg)
      {
      item.setDead();
      addStackToInventory(stack, RelativeSide.TOP);
      }
    }
  }

private boolean hasAnimalWork()
  {
  return !entitiesToCull.isEmpty() 
      || (carrotCount>0 && !pigsToBreed.isEmpty()) 
      || (seedCount>0 && !chickensToBreed.isEmpty())
      || (wheatCount>0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty()))
      || (bucketCount>0 && !cowsToMilk.isEmpty())
      || (shears!=null && !sheepToShear.isEmpty());
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.FARMING;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, xCoord, yCoord, zCoord);
  }

private static class EntityPair
{

int idA;
int idB;

private EntityPair(Entity a, Entity b)
  {
  idA = a.getEntityId();
  idB = b.getEntityId();
  }

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
