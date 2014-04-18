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
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
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
boolean hasShears;
List<EntityPair> cowsToBreed = new ArrayList<EntityPair>();
List<Integer> cowsToMilk = new ArrayList<Integer>();
List<EntityPair> sheepToBreed = new ArrayList<EntityPair>();
List<Integer> sheepToShear = new ArrayList<Integer>();

int workerRescanDelay;
boolean shouldCountResources;

int maxPigCount = 6;
int maxCowCount = 6;
int maxChickenCount = 6;
int maxSheepCount = 6;

List<Integer> entitiesToCull = new ArrayList<Integer>();

public WorkSiteAnimalFarm()
  {
  this.canUpdate = true;
  this.maxWorkers = 1;
  this.shouldCountResources = true;  
  this.inventory = new InventorySided(27 + 3 + 3, this);
  
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
    this.inventory.addSidedMapping(InventorySide.FRONT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.LEFT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.RIGHT, i, true, true);
    this.inventory.addSidedMapping(InventorySide.REAR, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.FRONT, i, (k%9)*18, (k/9)*18);
    this.inventory.addSlotFilter(i, filter);
    }
  
  filter = new SlotItemFilter()
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
    };
  this.inventory.addSlotViewMap(InventorySide.BOTTOM, 8, (4*18)+12+8+12+8, "guistrings.inventory.side.bottom");
  for(int i = 30, k = 0; i <33; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.BOTTOM, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.BOTTOM, i, (k%9)*18, (k/9)*18);
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
  bucketCount = 0;
  hasShears = false;
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
      hasShears = true;
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
  EntityAnimal cow1;
  EntityAnimal cow2;
  EntityPair breedingPair;
  
  int age;
  
  for(int i = 0; i<animals.size(); i++)
    {
    cow1 = animals.get(i);
    age = cow1.getGrowingAge();
    if(age!=0){continue;}//unbreedable first-target, skip
    while(i+1<animals.size())//loop through remaining animals to find a breeding partner
      {
      i++;
      cow2 = animals.get(i);
      age = cow2.getGrowingAge();
      if(age==0)//found a second breedable animal, add breeding pair, exit to outer loop
        {
        breedingPair = new EntityPair(cow1, cow2);
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
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(InventorySide.FRONT), new ItemStack(Items.wheat), 2);
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
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(InventorySide.FRONT), new ItemStack(Items.wheat), 2);
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
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(InventorySide.FRONT), new ItemStack(Items.wheat_seeds), 2);
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
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(InventorySide.FRONT), new ItemStack(Items.carrot), 2);
      return;
      }
    }
  if(hasShears && !sheepToShear.isEmpty())
    {
    AWLog.logDebug("attempting shearing of sheep..");
    didWork = tryShearing(sheepToShear);
    if(didWork)
      {
      /**
       * TODO handle wool drops from sheep
       */
      return;
      }
    }
  if(bucketCount>0 && !cowsToMilk.isEmpty())
    {
    AWLog.logDebug("attempting milking of cows..");
    didWork = tryMilking(cowsToMilk);
    if(didWork)
      {
      InventoryTools.removeItems(inventory, inventory.getAccessDirectionFor(InventorySide.BOTTOM), new ItemStack(Items.bucket), 1);
      this.addStackToInventory(new ItemStack(Items.milk_bucket), InventorySide.TOP);
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
  return false;
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
    this.addStackToInventory(stack, InventorySide.TOP);       
    item.setDead();
    }
  return true;
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
public String hasAltSetupGui()
  {
  return "guistrings.automation.animal_control";
  }

@Override
public void doWork(IWorker worker)
  {
  if(workerRescanDelay<=0 || !hasAnimalWork())
    {
    AWLog.logDebug("rescanning animal farm..");
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
      || (wheatCount>0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty()))
      || (bucketCount>0 && !cowsToMilk.isEmpty())
      || (hasShears && !sheepToShear.isEmpty());
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
