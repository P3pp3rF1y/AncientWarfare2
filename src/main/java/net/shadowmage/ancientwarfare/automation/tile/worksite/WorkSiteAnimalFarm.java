package net.shadowmage.ancientwarfare.automation.tile.worksite;

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
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ItemWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WorkSiteAnimalFarm extends TileWorksiteBoundedInventory {
	private static final int FOOD_INVENTORY_SIZE = 3;
	private static final int TOOL_INVENTORY_SIZE = 3;
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
	private int shearsSlot = -1;

	private List<EntityPair> pigsToBreed = new ArrayList<>();
	private List<EntityPair> chickensToBreed = new ArrayList<>();
	private List<EntityPair> cowsToBreed = new ArrayList<>();
	private int cowsToMilk;
	private List<EntityPair> sheepToBreed = new ArrayList<>();
	private List<Integer> sheepToShear = new ArrayList<>();
	private List<Integer> entitiesToCull = new ArrayList<>();

	private static ArrayList<ItemWrapper> ANIMAL_DROPS = new ArrayList<>();

	public final ItemStackHandler foodInventory;
	public final ItemStackHandler toolInventory;

	public WorkSiteAnimalFarm() {
		super();
		shouldCountResources = true;

		foodInventory = new ItemStackHandler(FOOD_INVENTORY_SIZE) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
				shouldCountResources = true;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return isFood(stack.getItem()) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};

		toolInventory = new ItemStackHandler(TOOL_INVENTORY_SIZE) {
			@Override
			protected void onContentsChanged(int slot) {
				markDirty();
				shouldCountResources = true;
			}

			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return isTool(stack.getItem()) ? super.insertItem(slot, stack, simulate) : stack;
			}
		};

		setSideInventory(RelativeSide.FRONT, foodInventory, RelativeSide.FRONT);
		setSideInventory(RelativeSide.BOTTOM, foodInventory, RelativeSide.TOP);
	}

	private boolean isFood(Item item) {
		return item == Items.WHEAT_SEEDS || item == Items.WHEAT || item == Items.CARROT;
	}

	private boolean isTool(Item item) {
		return item == Items.BUCKET || item instanceof ItemShears;
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}

	@Override
	protected boolean hasWorksiteWork() {
		return !entitiesToCull.isEmpty() || (carrotCount > 0 && !pigsToBreed.isEmpty()) || (seedCount > 0 && !chickensToBreed.isEmpty()) || (wheatCount > 0 && (!cowsToBreed.isEmpty() || !sheepToBreed.isEmpty())) || (bucketCount > 0 && cowsToMilk > 0) || (shearsSlot >= 0 && !sheepToShear.isEmpty());
	}

	@Override
	protected void updateWorksite() {
		world.profiler.startSection("Count Resources");
		if (shouldCountResources) {
			countResources();
			this.shouldCountResources = false;
		}
		world.profiler.endStartSection("Animal Rescan");
		if (workerRescanDelay-- <= 0) {
			rescan();
			workerRescanDelay = 200;
		}
		world.profiler.endStartSection("ItemPickup");
		if (world.getWorldTime() % 128 == 0) {
			pickupDrops();
		}
		world.profiler.endSection();
	}

	@Override
	public void onBlockBroken() {
		super.onBlockBroken();
		InventoryTools.dropItemsInWorld(world, foodInventory, pos);
		InventoryTools.dropItemsInWorld(world, toolInventory, pos);
	}

	private void countResources() {
		carrotCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.CARROT);
		seedCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.WHEAT_SEEDS);
		wheatCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.WHEAT);
		bucketCount = InventoryTools.getCountOf(toolInventory, s -> s.getItem() == Items.BUCKET);
		shearsSlot = InventoryTools.findItemSlot(toolInventory, s -> s.getItem() instanceof ItemShears);
	}

	private void rescan() {
		pigsToBreed.clear();
		cowsToBreed.clear();
		cowsToMilk = 0;
		sheepToBreed.clear();
		chickensToBreed.clear();
		entitiesToCull.clear();

		List<EntityAnimal> entityList = EntityTools.getEntitiesWithinBounds(world, EntityAnimal.class, getWorkBoundsMin(), getWorkBoundsMax());

		List<EntityAnimal> cows = new ArrayList<>();
		List<EntityAnimal> pigs = new ArrayList<>();
		List<EntityAnimal> sheep = new ArrayList<>();
		List<EntityAnimal> chickens = new ArrayList<>();

		for (EntityAnimal animal : entityList) {
			if (animal instanceof EntityCow) {
				cows.add(animal);
			} else if (animal instanceof EntityChicken) {
				chickens.add(animal);
			} else if (animal instanceof EntitySheep) {
				sheep.add(animal);
			} else if (animal instanceof EntityPig) {
				pigs.add(animal);
			}
		}

		scanForCows(cows);
		scanForSheep(sheep);
		scanForAnimals(chickens, chickensToBreed, maxChickenCount);
		scanForAnimals(pigs, pigsToBreed, maxPigCount);
	}

	private void scanForAnimals(List<EntityAnimal> animals, List<EntityPair> targets, int maxCount) {
		EntityAnimal animal1;
		EntityAnimal animal2;
		EntityPair breedingPair;

		int age;

		for (int i = 0; i < animals.size(); i++) {
			animal1 = animals.get(i);
			age = animal1.getGrowingAge();
			if (age != 0 || animal1.isInLove()) {
				continue;
			}//unbreedable first-target, skip
			while (i + 1 < animals.size())//loop through remaining animals to find a breeding partner
			{
				i++;
				animal2 = animals.get(i);
				age = animal2.getGrowingAge();
				if (age == 0 && !animal2.isInLove())//found a second breedable animal, add breeding pair, exit to outer loop
				{
					breedingPair = new EntityPair(animal1, animal2);
					targets.add(breedingPair);
					break;
				}
			}
		}

		int grownCount = 0;
		for (EntityAnimal animal : animals) {
			if (animal.getGrowingAge() >= 0) {
				grownCount++;
			}
		}

		if (grownCount > maxCount) {
			for (int i = 0, cullCount = grownCount - maxCount; i < animals.size() && cullCount > 0; i++) {
				if (animals.get(i).getGrowingAge() >= 0) {
					entitiesToCull.add(animals.get(i).getEntityId());
					cullCount--;
				}
			}
		}
	}

	private void scanForSheep(List<EntityAnimal> sheep) {
		scanForAnimals(sheep, sheepToBreed, maxSheepCount);
		for (EntityAnimal animal : sheep) {
			if (animal.getGrowingAge() >= 0) {
				EntitySheep sheep1 = (EntitySheep) animal;
				if (!sheep1.getSheared()) {
					sheepToShear.add(sheep1.getEntityId());
				}
			}
		}
	}

	private void scanForCows(List<EntityAnimal> animals) {
		scanForAnimals(animals, cowsToBreed, maxCowCount);
		for (EntityAnimal animal : animals) {
			if (animal.getGrowingAge() >= 0) {
				//TODO harder wild life integration
				// try to get HarderWildlife extended entity properties
				//                if (ModAccessors.HARDER_WILDLIFE.getMilkable(animal)) {
				//                    ModAccessors.HARDER_WILDLIFE.doMilking(animal);
				//                    cowsToMilk++;
				//                    if (cowsToMilk > maxCowCount) {
				//                        cowsToMilk = maxCowCount;
				//                        break;
				//                    }
				//                }
			}
		}
	}

	@Override
	protected boolean processWork() {
		if (!cowsToBreed.isEmpty() && wheatCount >= 2) {
			if (tryBreeding(cowsToBreed)) {
				wheatCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT), 2);
				return true;
			}
		}
		if (!sheepToBreed.isEmpty() && wheatCount >= 2) {
			if (tryBreeding(sheepToBreed)) {
				wheatCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT), 2);
				return true;
			}
		}
		if (!chickensToBreed.isEmpty() && seedCount >= 2) {
			if (tryBreeding(chickensToBreed)) {
				seedCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT_SEEDS), 2);
				return true;
			}
		}
		if (!pigsToBreed.isEmpty() && carrotCount >= 2) {
			if (tryBreeding(pigsToBreed)) {
				carrotCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.CARROT), 2);
				return true;
			}
		}
		if (tryShearing()) {
			return true;
		}
		if (bucketCount > 0 && tryMilking()) {
			InventoryTools.removeItems(toolInventory, new ItemStack(Items.BUCKET), 1);
			InventoryTools.insertOrDropItem(mainInventory, new ItemStack(Items.MILK_BUCKET), world, pos);
			return true;
		}
		return tryCulling();
	}

	private boolean tryBreeding(List<EntityPair> targets) {
		Entity animalA;
		Entity animalB;
		EntityPair pair;
		if (!targets.isEmpty()) {
			pair = targets.remove(0);
			animalA = pair.getEntityA(world);
			animalB = pair.getEntityB(world);
			if (!(animalA instanceof EntityAnimal) || !(animalB instanceof EntityAnimal)) {
				return false;
			}
			if (animalA.isEntityAlive() && animalB.isEntityAlive()) {
				EntityPlayer fakePlayer = AWFakePlayer.get(world);
				((EntityAnimal) animalA).setInLove(fakePlayer);
				((EntityAnimal) animalB).setInLove(fakePlayer);
				return true;
			}
		}
		return false;
	}

	private boolean tryMilking() {
		if (cowsToMilk > 0) {
			if (ModAccessors.HARDER_WILDLIFE_LOADED)
				return true;
			return world.rand.nextInt(cowsToMilk + getFortune()) > maxCowCount / 2;
		}
		return false;
	}

	private boolean tryShearing() {
		if (shearsSlot < 0 || sheepToShear.isEmpty()) {
			return false;
		}
		EntitySheep sheep = (EntitySheep) world.getEntityByID(sheepToShear.remove(0));
		ItemStack shears = toolInventory.getStackInSlot(shearsSlot);
		if (sheep == null || !sheep.isShearable(shears, world, pos)) {
			return false;
		}
		//shears do not get damaged, if they did this would need clone of the stack and additional setStackInSlot call
		NonNullList<ItemStack> items = InventoryTools.toNonNullList(sheep.onSheared(shears, world, pos, getFortune()));
		for (ItemStack item : items) {
			InventoryTools.insertOrDropItem(mainInventory, item, world, pos);
		}
		return true;
	}

	private boolean tryCulling() {
		Entity entity;
		EntityAnimal animal;
		int fortune = getFortune();
		while (!entitiesToCull.isEmpty()) {
			entity = world.getEntityByID(entitiesToCull.remove(0));
			if (entity instanceof EntityAnimal && entity.isEntityAlive()) {
				animal = (EntityAnimal) entity;
				if (animal.isInLove() || animal.getGrowingAge() < 0) {
					continue;
				}

				animal.captureDrops = true;
				animal.arrowHitTimer = 10;
				animal.attackEntityFrom(DamageSource.GENERIC, animal.getHealth() + 1);
				@Nonnull ItemStack stack;
				for (EntityItem item : animal.capturedDrops) {
					stack = item.getItem();
					if (!stack.isEmpty()) {
						if (fortune > 0) {
							stack.grow(world.rand.nextInt(fortune));
						}
						InventoryTools.insertOrDropItem(mainInventory, stack, world, pos);
					}
				}
				animal.capturedDrops.clear();
				animal.captureDrops = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, pos);
		}
		return true;
	}

	private void pickupDrops() {
		if (ANIMAL_DROPS.size() == 0) {
			ANIMAL_DROPS = ItemWrapper.buildList("Animal Farm drops", AWAutomationStatics.animal_farm_pickups);
		}

		List<EntityItem> items = EntityTools.getEntitiesWithinBounds(world, EntityItem.class, getWorkBoundsMin(), getWorkBoundsMax());
		@Nonnull ItemStack stack;
		for (EntityItem item : items) {
			stack = item.getItem();
			if (item.isEntityAlive() && !stack.isEmpty() && stack.getItem() != Items.AIR) {
				Item droppedItem = stack.getItem();
				for (ItemWrapper animalDrop : ANIMAL_DROPS) {
					if (droppedItem.equals(animalDrop.item)) {
						if (animalDrop.damage == -1 || animalDrop.damage == stack.getItemDamage()) {
							stack = InventoryTools.mergeItemStack(mainInventory, stack);
							if (!stack.isEmpty()) {
								item.setItem(stack);
							} else {
								item.setDead();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public WorkType getWorkType() {
		return WorkType.FARMING;
	}

	@Override
	public void openAltGui(EntityPlayer player) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, pos);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		maxChickenCount = tag.getInteger("maxChickens");
		maxCowCount = tag.getInteger("maxCows");
		maxPigCount = tag.getInteger("maxPigs");
		maxSheepCount = tag.getInteger("maxSheep");
		foodInventory.deserializeNBT(tag.getCompoundTag("foodInventory"));
		toolInventory.deserializeNBT(tag.getCompoundTag("toolInventory"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("maxChickens", maxChickenCount);
		tag.setInteger("maxCows", maxCowCount);
		tag.setInteger("maxPigs", maxPigCount);
		tag.setInteger("maxSheep", maxSheepCount);
		tag.setTag("foodInventory", foodInventory.serializeNBT());
		tag.setTag("toolInventory", toolInventory.serializeNBT());
		return tag;
	}

	private static class EntityPair {

		final int idA;
		final int idB;

		private EntityPair(Entity a, Entity b) {
			idA = a.getEntityId();
			idB = b.getEntityId();
		}

		public Entity getEntityA(World world) {
			return world.getEntityByID(idA);
		}

		public Entity getEntityB(World world) {
			return world.getEntityByID(idB);
		}
	}

}
