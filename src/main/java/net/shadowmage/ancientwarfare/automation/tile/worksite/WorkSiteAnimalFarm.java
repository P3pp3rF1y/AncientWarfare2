package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ItemWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
	private int potatoCount;
	private int beetrootCount;
	private int seedCount;
	private int shearsSlot = -1;

	private List<EntityPair> pigsToBreed = new ArrayList<>();
	private List<EntityPair> chickensToBreed = new ArrayList<>();
	private List<EntityPair> cowsToBreed = new ArrayList<>();
	private int cowsToMilk;
	private List<EntityPair> sheepToBreed = new ArrayList<>();
	private List<Integer> sheepToShear = new ArrayList<>();
	private List<Integer> entitiesToCull = new ArrayList<>();

	private static final ArrayList<ItemWrapper> ANIMAL_DROPS = ItemWrapper.buildList("Animal Farm drops", AWAutomationStatics.animal_farm_pickups);

	public final ItemStackHandler foodInventory;
	public final ItemStackHandler toolInventory;

	private static final Set<Integer> entityCulledIds = new HashSet<>();

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
		setSideInventory(RelativeSide.BOTTOM, toolInventory, RelativeSide.TOP);
	}

	private boolean isFood(Item item) {
		return item == Items.WHEAT_SEEDS || item == Items.WHEAT || item == Items.CARROT || item == Items.POTATO || item == Items.BEETROOT;
	}

	private boolean isTool(Item item) {
		return item == Items.BUCKET || item instanceof ItemShears;
	}

	@Override
	public boolean userAdjustableBlocks() {
		return false;
	}

	private boolean canShearSheep() {
		return shearsSlot >= 0 && !sheepToShear.isEmpty();
	}

	private boolean canMilkCow() {
		return bucketCount > 0 && cowsToMilk > 0;
	}

	private boolean canBreedSheep() {
		return wheatCount > 1 && !sheepToBreed.isEmpty();
	}

	private boolean canBreedCows() {
		return wheatCount > 1 && !cowsToBreed.isEmpty();
	}

	private boolean canBreedChicken() {
		return seedCount > 1 && !chickensToBreed.isEmpty();
	}

	private boolean canBreedPigs() {
		return (carrotCount > 1 && !pigsToBreed.isEmpty()) || (potatoCount > 1 && !pigsToBreed.isEmpty()) || (beetrootCount > 1 && !pigsToBreed.isEmpty());
	}

	private boolean canCull() {
		return !entitiesToCull.isEmpty();
	}

	private static final IWorksiteAction SHEAR_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction MILK_COW_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction BREED_SHEEP_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction BREED_COWS_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction BREED_CHICKEN_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction BREED_PIGS_ACTION = WorksiteImplementation::getEnergyPerActivation;
	private static final IWorksiteAction CULL_ACTION = WorksiteImplementation::getEnergyPerActivation;

	@Override
	protected Optional<IWorksiteAction> getNextAction() {
		if (canShearSheep()) {
			return Optional.of(SHEAR_ACTION);
		} else if (canMilkCow()) {
			return Optional.of(MILK_COW_ACTION);
		} else if (canBreedSheep()) {
			return Optional.of(BREED_SHEEP_ACTION);
		} else if (canBreedCows()) {
			return Optional.of(BREED_COWS_ACTION);
		} else if (canBreedChicken()) {
			return Optional.of(BREED_CHICKEN_ACTION);
		} else if (canBreedPigs()) {
			return Optional.of(BREED_PIGS_ACTION);
		} else if (canCull()) {
			return Optional.of(CULL_ACTION);
		}
		return Optional.empty();
	}

	@Override
	protected boolean processAction(IWorksiteAction action) {
		if (action == SHEAR_ACTION) {
			return tryShearing();
		} else if (action == MILK_COW_ACTION) {
			if (tryMilking()) {
				InventoryTools.removeItems(toolInventory, new ItemStack(Items.BUCKET), 1);
				InventoryTools.insertOrDropItem(mainInventory, new ItemStack(Items.MILK_BUCKET), world, pos);
				return true;
			}
		} else if (action == BREED_SHEEP_ACTION) {
			if (tryBreeding(sheepToBreed)) {
				wheatCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT), 2);
				return true;
			}
		} else if (action == BREED_COWS_ACTION) {
			if (tryBreeding(cowsToBreed)) {
				wheatCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT), 2);
				return true;
			}
		} else if (action == BREED_CHICKEN_ACTION) {
			if (tryBreeding(chickensToBreed)) {
				seedCount -= 2;
				InventoryTools.removeItems(foodInventory, new ItemStack(Items.WHEAT_SEEDS), 2);
				return true;
			}
		} else if (action == BREED_PIGS_ACTION) {
			if (tryBreeding(pigsToBreed)){
				if (carrotCount > 1) {
					carrotCount -= 2;
					InventoryTools.removeItems(foodInventory, new ItemStack(Items.CARROT), 2);
					return true;
				}
				else if (potatoCount > 1) {
					potatoCount -= 2;
					InventoryTools.removeItems(foodInventory, new ItemStack(Items.POTATO), 2);
					return true;
				}
				else if (beetrootCount > 1) {
					beetrootCount -= 2;
					InventoryTools.removeItems(foodInventory, new ItemStack(Items.BEETROOT), 2);
					return true;
				}
			}
		} else if (action == CULL_ACTION) {
			return tryCulling();
		}
		return false;
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
	public void onBlockBroken(IBlockState state) {
		super.onBlockBroken(state);
		InventoryTools.dropItemsInWorld(world, foodInventory, pos);
		InventoryTools.dropItemsInWorld(world, toolInventory, pos);
	}

	private void countResources() {
		carrotCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.CARROT);
		potatoCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.POTATO);
		beetrootCount = InventoryTools.getCountOf(foodInventory, s -> s.getItem() == Items.BEETROOT);
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
				cowsToMilk++;
			}
		}
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
		return cowsToMilk > 0 && world.rand.nextInt(cowsToMilk * 4) > (cowsToMilk * 3);
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
		while (canCull()) {
			entity = world.getEntityByID(entitiesToCull.remove(0));
			if (entity instanceof EntityAnimal && entity.isEntityAlive()) {
				animal = (EntityAnimal) entity;
				if (animal.isInLove() || animal.getGrowingAge() < 0) {
					continue;
				}

				animal.captureDrops = true;
				animal.arrowHitTimer = 10;
				entityCulledIds.add(animal.getEntityId());
				animal.attackEntityFrom(DamageSource.GENERIC, animal.getHealth() + 1);
				for (EntityItem item : animal.capturedDrops) {
					ItemStack stack = item.getItem();
					if (!stack.isEmpty()) {
						if (fortune > 0) {
							stack.grow(world.rand.nextInt(fortune));
						}
						InventoryTools.insertOrDropItem(mainInventory, stack, world, pos);
					}
				}
				animal.capturedDrops.clear();
				animal.captureDrops = false;
				entityCulledIds.remove(animal.getEntityId());
				return true;
			}
		}
		return false;
	}

	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent evt) {
		if (entityCulledIds.contains(evt.getEntity().getEntityId())) {
			evt.setCanceled(true);
		}
	}

	@Override
	public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, pos);
		}
		return true;
	}

	private void pickupDrops() {
		List<EntityItem> items = EntityTools.getEntitiesWithinBounds(world, EntityItem.class, getWorkBoundsMin(), getWorkBoundsMax());
		for (EntityItem item : items) {
			ItemStack stack = item.getItem();
			if (item.isEntityAlive() && !stack.isEmpty() && stack.getItem() != Items.AIR) {
				Item droppedItem = stack.getItem();
				for (ItemWrapper animalDrop : ANIMAL_DROPS) {
					if (droppedItem.equals(animalDrop.item) && animalDrop.damage == -1 || animalDrop.damage == stack.getItemDamage()) {
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

		private final int idA;
		private final int idB;

		private EntityPair(Entity a, Entity b) {
			idA = a.getEntityId();
			idB = b.getEntityId();
		}

		private Entity getEntityA(World world) {
			return world.getEntityByID(idA);
		}

		private Entity getEntityB(World world) {
			return world.getEntityByID(idB);
		}
	}

}
