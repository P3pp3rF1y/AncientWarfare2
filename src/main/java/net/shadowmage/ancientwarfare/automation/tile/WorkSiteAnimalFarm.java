package net.shadowmage.ancientwarfare.automation.tile;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class WorkSiteAnimalFarm extends TileWorksiteBase
{

int carrotCount;
List<EntityPair> pigsToBreed = new ArrayList<EntityPair>();

int seedCount;
List<EntityPair> chickensToBreed = new ArrayList<EntityPair>();

int wheatCount;
int bucketCount;
ItemStack shears = null;
List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
List<Integer> cowsToMilk = new ArrayList<Integer>();
List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();
List<Integer> sheepToShear = new ArrayList<Integer>();

int workerRescanDelay;
boolean shouldCountResources;

public int maxPigCount = 6;
public int maxCowCount = 6;
public int maxChickenCount = 6;
public int maxSheepCount = 6;

List<Integer> entitiesToCull = new ArrayList<Integer>();

public WorkSiteAnimalFarm()
  {
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldCountResources = true;  
  
  this.inventory = new InventorySided(this, RotationType.FOUR_WAY, 33);
  int[] topIndices = InventoryTools.getIndiceArrayForSpread(0, 27);
  int[] frontIndices = InventoryTools.getIndiceArrayForSpread(27, 3);
  int[] bottomIndices = InventoryTools.getIndiceArrayForSpread(30, 3);  
  this.inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  this.inventory.setAccessibleSideDefault(RelativeSide.FRONT, RelativeSide.FRONT, frontIndices);//saplings
  this.inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);//bonemeal
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
  }

private void rescan()
  {
  pigsToBreed.clear();
  cowsToBreed.clear();
  cowsToMilk.clear();
  sheepToBreed.clear();
  chickensToBreed.clear();
  entitiesToCull.clear();
  
  BlockPosition min = getWorkBoundsMin();
  BlockPosition max = getWorkBoundsMax();
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(min.x, min.y, min.z, max.x+1, max.y+1, max.z+1);
  
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
  scanForAnimals(sheep, sheepToBreed, maxSheepCount);  
  workerRescanDelay = 200;
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

private void processWork()
  {
  AWLog.logDebug("processing work from animal farm...");
  boolean didWork = false;
  if(!cowsToBreed.isEmpty() && wheatCount>=2)
    {
    AWLog.logDebug("attempting breeding of cows..");
    didWork = tryBreeding(cowsToBreed);    
    if(didWork)
      {
      wheatCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
      return;
      }
    }
  if(!sheepToBreed.isEmpty() && wheatCount>=2)
    {
    AWLog.logDebug("attempting breeding of sheep..");
    didWork = tryBreeding(sheepToBreed);
    if(didWork)
      {
      wheatCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat), 2);
      return;
      }
    }
  if(!chickensToBreed.isEmpty() && seedCount>=2)
    {
    AWLog.logDebug("attempting breeding of chickens..");
    didWork = tryBreeding(chickensToBreed);
    if(didWork)
      {
      seedCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.wheat_seeds), 2);
      return;
      }
    }
  if(!pigsToBreed.isEmpty() && carrotCount>=2)
    {
    AWLog.logDebug("attempting breeding of pigs..");
    didWork = tryBreeding(pigsToBreed);
    if(didWork)
      {
      carrotCount-=2;
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.FRONT), new ItemStack(Items.carrot), 2);
      return;
      }
    }
  if(shears!=null && !sheepToShear.isEmpty())
    {
    AWLog.logDebug("attempting shearing of sheep..");
    didWork = tryShearing(sheepToShear);
    if(didWork)      
      {
      return;
      }
    }
  if(bucketCount>0 && !cowsToMilk.isEmpty())
    {
    AWLog.logDebug("attempting milking of cows..");
    didWork = tryMilking(cowsToMilk);
    if(didWork)
      {
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(RelativeSide.BOTTOM), new ItemStack(Items.bucket), 1);
      this.addStackToInventory(new ItemStack(Items.milk_bucket), RelativeSide.TOP);
      return;
      }
    }
  if(!entitiesToCull.isEmpty())
    {
    AWLog.logDebug("attempting culling of animals..");
    tryCulling(entitiesToCull);
    }
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
  Integer i = targets.remove(0);
  Entity e = worldObj.getEntityByID(i);
  if(!(e instanceof EntityAnimal)){return false;}
  EntityAnimal animal = (EntityAnimal)e;
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

@Override
public boolean hasAltSetupGui()
  {
  return true;
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
  pickupEggs();
  }

private void pickupEggs()
  {
  BlockPosition p1 = getWorkBoundsMin();
  BlockPosition p2 = getWorkBoundsMax().copy().offset(1, 1, 1);
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
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
      || (wheatCount>0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty()))
      || (bucketCount>0 && !cowsToMilk.isEmpty())
      || (shears!=null && !sheepToShear.isEmpty());
  }

@Override
public WorkType getWorkType()
  {
  return WorkType.ANIMAL_HUSBANDRY;
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

@Override
public void doPlayerWork(EntityPlayer player)
  {
  if(workerRescanDelay<=0 || !hasAnimalWork())
    {
    rescan();
    }  
  if(hasAnimalWork())
    {
    processWork();
    }
  pickupEggs();
  }

}
