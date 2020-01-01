package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class InventoryTools {
	public static boolean canInventoryHold(IItemHandler handler, ItemStack stack) {
		return insertItem(handler, stack, true).isEmpty();
	}

	public static boolean canInventoryHold(IItemHandler handler, List<ItemStack> stacks) {
		return insertItems(handler, stacks, true).isEmpty();
	}

	public static List<ItemStack> insertItems(IItemHandler handler, List<ItemStack> stacks, boolean simulate) {
		NonNullList<ItemStack> remainingItems = NonNullList.create();
		if (simulate) {
			handler = cloneItemHandler(handler);
		}
		for (ItemStack stack : stacks) {
			ItemStack remainingItem = insertItem(handler, stack, false);
			if (!remainingItem.isEmpty()) {
				remainingItems.add(remainingItem);
			}
		}
		return remainingItems;
	}

	public static IItemHandlerModifiable cloneItemHandler(IItemHandler handler) {
		ItemStackHandler copy = new ItemStackHandler(handler.getSlots()) {
			@Nonnull
			@Override
			public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
				return canInsert(slot, stack) ? super.insertItem(slot, stack.copy(), simulate) : stack;
			}

			private boolean canInsert(int slot, @Nonnull ItemStack stack) {
				ItemStack remainingStack = handler.insertItem(slot, stack, true);
				return remainingStack.isEmpty() || remainingStack.getCount() != stack.getCount();
			}
		};

		for (int slot = 0; slot < handler.getSlots(); slot++) {
			copy.setStackInSlot(slot, handler.getStackInSlot(slot).copy());
		}
		return copy;
	}

	public static ItemStack insertItem(IItemHandler handler, ItemStack stack) {
		return insertItem(handler, stack, false);
	}

	public static ItemStack insertItem(IItemHandler handler, ItemStack stack, boolean simulate) {
		ItemStack remaining = stack.copy();
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			remaining = handler.insertItem(slot, remaining, simulate);
			if (remaining.isEmpty()) {
				break;
			}
		}
		return remaining;
	}

	public static void updateCursorItem(EntityPlayerMP player, ItemStack stack, boolean shiftClick) {
		if (!stack.isEmpty()) {
			if (shiftClick) {
				stack = mergeItemStack(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), stack);
			}
			if (!stack.isEmpty()) {
				player.inventory.setItemStack(stack);
				player.updateHeldItem();
			}
		}
	}

	public static ItemStack mergeItemStack(IItemHandler handler, ItemStack stack) {
		ItemStack ret = stack;
		if (stack.isStackable()) {
			for (int i = 0; i < handler.getSlots(); i++) {
				if (stack.isEmpty()) {
					break;
				}

				ItemStack slotStack = handler.getStackInSlot(i);

				if (doItemStacksMatch(slotStack, stack)) {
					int maxSize = Math.min(handler.getSlotLimit(i), stack.getMaxStackSize());

					int change = Math.min(maxSize - slotStack.getCount(), stack.getCount());

					handler.insertItem(i, stack, false);
					stack.shrink(change);
				}
			}
		}

		if (!stack.isEmpty()) {
			for (int i = 0; i < handler.getSlots(); i++) {
				ItemStack itemstack1 = handler.getStackInSlot(i);

				if (itemstack1.isEmpty() && handler.insertItem(i, stack, true).isEmpty()) {
					int slotLimit = handler.getSlotLimit(i);
					if (stack.getCount() > slotLimit) {
						handler.insertItem(i, stack.splitStack(slotLimit), false);
					} else {
						handler.insertItem(i, stack.splitStack(stack.getCount()), false);
						ret = ItemStack.EMPTY;
						break;
					}
				}
			}
		}

		return ret;
	}

	/*
	 * Attempts to remove filter * quantity from inventory.  Returns removed item in return stack, or null if
	 * no items were removed.<br>
	 * Will only remove and return up to filter.getMaxStackSize() items, regardless of how many are requested.
	 *
	 * @return the removed item.
	 */
	public static ItemStack removeItems(IItemHandler handler, ItemStack filter, int quantity) {
		return removeItems(handler, filter, quantity, false);
	}

	public static ItemStack removeItems(IItemHandler handler, ItemStack filter, int quantity, boolean simulate) {
		if (quantity <= 0) {
			return ItemStack.EMPTY;
		}
		if (quantity > filter.getMaxStackSize()) {
			quantity = filter.getMaxStackSize();
		}
		int returnCount = 0;
		@Nonnull ItemStack slotStack;
		for (int index = 0; index < handler.getSlots(); index++) {
			slotStack = handler.getStackInSlot(index);
			if (slotStack.isEmpty() || !doItemStacksMatchRelaxed(filter, slotStack)) {
				continue;
			}

			int toMove = Math.min(quantity - returnCount, slotStack.getCount());

			ItemStack extractedStack = handler.extractItem(index, toMove, simulate);

			returnCount += extractedStack.getCount();
			if (quantity - returnCount <= 0) {
				break;
			}
		}
		@Nonnull ItemStack returnStack = ItemStack.EMPTY;
		if (returnCount > 0) {
			returnStack = filter.copy();
			returnStack.setCount(returnCount);
		}
		return returnStack;
	}

	/*
	 * Move up to the specified quantity of filter stack from 'from' into 'to', using the designated sides (or general all sides merge if side<0 or from/to are not sided inventories)
	 *
	 * @param from     the inventory to withdraw items from
	 * @param to       the inventory to deposit items into
	 * @param filter   the stack used as a filter, only items matching this will be moved
	 * @param quantity how many items to move
	 * @param fromSide the side of 'from' inventory to withdraw out of
	 * @param toSide   the side of 'to' inventory to deposit into
	 */
	public static int transferItems(IItemHandler from, IItemHandler to, ItemStack filter, int quantity) {
		return transferItems(from, to, filter, quantity, false, false);
	}

	/*
	 * Move up to the specified quantity of filter stack from 'from' into 'to', using the designated sides (or general all sides merge if side<0 or from/to are not sided inventories)
	 *
	 * @param from         the inventory to withdraw items from
	 * @param to           the inventory to deposit items into
	 * @param filter       the stack used as a filter, only items matching this will be moved
	 * @param quantity     how many items to move
	 * @param fromSide     the side of 'from' inventory to withdraw out of
	 * @param toSide       the side of 'to' inventory to deposit into
	 * @param ignoreDamage ignore item-damage when looking for items to move
	 * @param ignoreNBT    ignore item-tag when looking for items to move
	 */
	public static int transferItems(IItemHandler from, IItemHandler to, ItemStack filterStack, int quantity, boolean ignoreDamage, boolean ignoreNBT) {
		return transferItems(from, to, stack -> doItemStacksMatch(filterStack, stack, ignoreDamage, ignoreNBT), quantity);
	}

	public static int transferItems(IItemHandler from, IItemHandler to, Function<ItemStack, Boolean> filter, int quantity) {
		int moved = 0;
		int toMove = quantity;
		for (int slot = 0; slot < from.getSlots() && toMove > 0; slot++) {
			@Nonnull ItemStack stack = from.getStackInSlot(slot);
			if (stack.isEmpty() || !filter.apply(stack)) {
				continue;
			}

			int stackSizeToMove = Math.min(stack.getMaxStackSize(), Math.min(stack.getCount(), toMove));

			ItemStack stackToMove = stack.copy();
			stackToMove.setCount(stackSizeToMove);

			if (from.extractItem(slot, stackSizeToMove, true).getCount() != stackSizeToMove) {
				continue;
			}

			ItemStack remaining = insertItem(to, stackToMove, false);

			int stackSizeMoved = stackSizeToMove - remaining.getCount();

			if (stackSizeMoved > 0) {
				from.extractItem(slot, stackSizeMoved, false);
			}

			moved += stackSizeMoved;
			toMove = quantity - moved;
		}
		return moved;
	}

	public static int findItemSlot(IItemHandler handler, Predicate<ItemStack> filter) {
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if (filter.test(stack)) {
				return slot;
			}
		}
		return -1;
	}

	public static int getCountOf(IItemHandler handler, ItemStack filterStack) {
		return getCountOf(handler, stack -> !stack.isEmpty() && doItemStacksMatchRelaxed(filterStack, stack));
	}

	public static int getCountOf(IItemHandler handler, Predicate<ItemStack> filter) {
		if (handler.getSlots() <= 0) {
			return 0;
		}
		int count = 0;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if (filter.test(stack)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	public static boolean hasCountOrMore(IItemHandler handler, ItemStack filterStack) {
		return hasCountOrMore(handler, s -> doItemStacksMatch(s, filterStack), filterStack.getCount());
	}

	private static boolean hasCountOrMore(IItemHandler handler, Predicate<ItemStack> filter, int minimumCount) {
		if (handler.getSlots() <= 0) {
			return false;
		}
		int count = 0;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if (filter.test(stack)) {
				count += stack.getCount();
				if (count >= minimumCount) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * validates that stacks are the same item / damage / tag, ignores quantity
	 */
	public static boolean doItemStacksMatchRelaxed(ItemStack stack1, ItemStack stack2) {
		if (stack1 == stack2) {
			return true;
		}
		return OreDictionary.itemMatches(stack1, stack2, !stack1.isEmpty() && (stack1.isItemStackDamageable() || stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE))
				&& ItemStack.areItemsEqualIgnoreDurability(stack1, stack2)
				&& ItemStack.areItemStackTagsEqual(stack1, stack2)
				&& stack1.areCapsCompatible(stack2);
	}

	public static boolean doItemStacksMatch(ItemStack stackA, ItemStack stackB) {
		return doItemStacksMatch(stackA, stackB, false, false);
	}

	public static boolean doItemStacksMatch(ItemStack stackA, ItemStack stackB, boolean ignoreDamage, boolean ignoreNBT) {
		if (stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		} else if (stackA.getItem() != stackB.getItem()) {
			return false;
		} else if ((stackA.getHasSubtypes() || !ignoreDamage) && stackA.getItemDamage() != stackB.getItemDamage()) {
			return false;
		} else if (!ignoreNBT && stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
			return false;
		} else {
			return (ignoreNBT || stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
		}
	}

	/*
	 * drops the input itemstack into the world at the input position
	 */
	public static void dropItemInWorld(World world, ItemStack item, BlockPos pos) {
		dropItemInWorld(world, item, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void dropItemInWorld(World world, ItemStack item, double x, double y, double z) {
		if (world.isRemote) {
			return;
		}
		InventoryHelper.spawnItemStack(world, x, y, z, item);
	}

	/*
	 * Writes out the input inventory to the input nbt-tag.<br>
	 * The written out inventory is suitable for reading back using
	 * {@link InventoryTools#readInventoryFromNBT(IInventory, NBTTagCompound)}
	 */
	public static NBTTagCompound writeInventoryToNBT(IInventory inventory, NBTTagCompound tag) {
		NBTTagList itemList = new NBTTagList();
		NBTTagCompound itemTag;
		@Nonnull ItemStack item;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			item = inventory.getStackInSlot(i);
			if (item.isEmpty()) {
				continue;
			}
			itemTag = item.writeToNBT(new NBTTagCompound());
			itemTag.setShort("slot", (short) i);
			itemList.appendTag(itemTag);
		}
		tag.setTag("itemList", itemList);
		return tag;//TODO clean up all references to this to use single-line semantics
	}

	/*
	 * Reads an inventory contents into the input inventory from the given nbt-tag.<br>
	 * Should only be passed nbt-tags / inventories that have been saved using
	 * {@link InventoryTools#writeInventoryToNBT(IInventory, NBTTagCompound)}
	 */
	public static void readInventoryFromNBT(IInventory inventory, NBTTagCompound tag) {
		NBTTagList itemList = tag.getTagList("itemList", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound itemTag;
		@Nonnull ItemStack item;
		int slot;
		for (int i = 0; i < itemList.tagCount(); i++) {
			itemTag = itemList.getCompoundTagAt(i);
			slot = itemTag.getShort("slot");
			item = new ItemStack(itemTag);
			inventory.setInventorySlotContents(slot, item);
		}
	}

	/*
	 * Compacts in input item-stack list.<br>
	 * This particular method wraps an ItemQuantityMap, and has much better speed than the other two methods,
	 * but does use more memory in the process.  On average 2x faster than compactStackList and 4x+ faster than
	 * compacctStackList2
	 */
	public static NonNullList<ItemStack> compactStackList(NonNullList<ItemStack> in) {
		ItemQuantityMap map = new ItemQuantityMap();
		for (ItemStack stack : in) {
			map.addCount(stack, stack.getCount());
		}
		return map.getItems();
	}

	public static void mergeItemStacks(NonNullList<ItemStack> stacks, NonNullList<ItemStack> stacksToMerge) {
		for (ItemStack stackToMerge : stacksToMerge) {
			if (stackToMerge.isEmpty()) {
				continue;
			}

			for (ItemStack stack : stacks) {
				if (stack.getCount() < stack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(stackToMerge, stack)) {
					int count = Math.min(stack.getMaxStackSize() - stack.getCount(), stackToMerge.getCount());
					stack.grow(count);
					stackToMerge.shrink(count);
					if (stackToMerge.isEmpty()) {
						break;
					}
				}
			}
			if (!stackToMerge.isEmpty()) {
				stacks.add(stackToMerge);
			}
		}
	}

	public static void dropItemsInWorld(World world, IInventory inventory, BlockPos pos) {
		for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
			dropItemInWorld(world, inventory.getStackInSlot(slot), pos);
		}
	}

	public static void dropItemsInWorld(World world, IItemHandler handler, BlockPos pos) {
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			dropItemInWorld(world, handler.getStackInSlot(slot), pos);
		}
	}

	public static void dropItemsInWorld(World world, IItemHandler handler, double x, double y, double z) {
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			dropItemInWorld(world, handler.getStackInSlot(slot), x, y, z);
		}
	}

	public static void dropItemsInWorld(World world, NonNullList<ItemStack> stacks, BlockPos pos) {
		for (ItemStack stack : stacks) {
			dropItemInWorld(world, stack, pos);
		}
	}

	public static NonNullList<ItemStack> getItems(IItemHandler handler) {
		NonNullList<ItemStack> ret = NonNullList.create();

		for (int slot = 0; slot < handler.getSlots(); slot++) {
			ItemStack slotStack = handler.getStackInSlot(slot);
			if (!slotStack.isEmpty()) {
				ret.add(slotStack);
			}
		}
		return ret;
	}

	public static NonNullList<ItemStack> removeItems(NonNullList<ItemStack> stacks, NonNullList<ItemStack> stacksToRemove) {
		return stacks.stream().filter(s -> stacksToRemove.stream().noneMatch(r -> doItemStacksMatch(s, r)))
				.collect(Collectors.toCollection(NonNullList::create));
	}

	public static NonNullList<ItemStack> removeItems(IItemHandler handler, NonNullList<ItemStack> stacks) {
		return removeItems(handler, stacks, false);
	}

	public static NonNullList<ItemStack> removeItems(IItemHandler handler, NonNullList<ItemStack> stacks, boolean simulate) {
		NonNullList<ItemStack> extractedItems = NonNullList.create();
		if (simulate) {
			handler = cloneItemHandler(handler);
		}
		for (ItemStack stack : stacks) {
			ItemStack extracted = removeItem(handler, stack, false);
			if (!extracted.isEmpty()) {
				extractedItems.add(extracted);
			}
		}
		return extractedItems;
	}

	private static ItemStack removeItem(IItemHandler handler, ItemStack stack, boolean simulate) {
		ItemStack extracted = ItemStack.EMPTY;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			ItemStack slotStack = handler.getStackInSlot(slot);
			if (doItemStacksMatchRelaxed(stack, slotStack)) {
				extracted = handler.extractItem(slot, stack.getCount(), simulate);
				if (extracted.getCount() == stack.getCount()) {
					break;
				}
			}
		}
		return extracted;
	}

	public static ItemStack removeItem(NonNullList<ItemStack> stacks, Predicate<ItemStack> filter, int quantity) {
		return removeItem(stacks, filter, quantity, false);
	}

	public static ItemStack removeItem(NonNullList<ItemStack> stacks, Predicate<ItemStack> filter, int quantity, boolean simulate) {
		Iterator<ItemStack> it = simulate ? copyStacks(stacks).iterator() : stacks.iterator();

		ItemStack stackToReturn = ItemStack.EMPTY;
		int removed = 0;
		while (it.hasNext()) {
			ItemStack stack = it.next();

			if (filter.test(stack)) {
				if (stack.getMaxStackSize() < quantity) {
					throw new UnsupportedOperationException("Not supported for quantity greater than max stack size");
				}

				int toRemove = Math.min(quantity - removed, stack.getCount());
				removed += toRemove;

				if (stackToReturn.isEmpty()) {
					stackToReturn = stack.copy();
				}
				stack.shrink(toRemove);
				stackToReturn.setCount(removed);

				if (stack.isEmpty()) {
					it.remove();
				}

				if (quantity - removed <= 0) {
					return stackToReturn;
				}
			}
		}
		return stackToReturn;
	}

	public static List<ItemStack> copyStacks(List<ItemStack> stacks) {
		return stacks.stream().map(ItemStack::copy).collect(Collectors.toCollection(NonNullList::create));
	}

	public static boolean isInventory(TileEntity tileEntity) {
		return tileEntity.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
	}

	public static void generateLootFor(World world, IItemHandler inventory, Random rng, int rolls) {
		generateLootFor(world, null, inventory, rng, LootTableList.CHESTS_SIMPLE_DUNGEON, rolls);
	}

	public static void generateLootFor(World world,
			@Nullable EntityPlayer player, IItemHandler inventory, Random rng, ResourceLocation lootTableName, int rolls) {
		NonNullList<ItemStack> loot = NonNullList.create();
		for (int i = 0; i < rolls; i++) {
			mergeItemStacks(loot, getLootStacks(world, player, rng, lootTableName));
		}

		List<Integer> randomSlots = getEmptySlotsRandomized(inventory, rng);
		shuffleItems(loot, randomSlots.size(), rng);

		for (ItemStack itemstack : loot) {
			if (randomSlots.isEmpty()) {
				AncientWarfareCore.LOG.warn("Tried to over-fill a container");
				return;
			}

			if (!itemstack.isEmpty()) {
				inventory.insertItem(randomSlots.remove(randomSlots.size() - 1), itemstack, false);
			}
		}
	}

	public static NonNullList<ItemStack> getLootStacks(World world, @Nullable EntityPlayer player, Random rng, ResourceLocation lootTableName) {
		LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
		LootTable lootTable = world.getLootTableManager().getLootTableFromLocation(lootTableName);
		if (player != null) {
			builder.withLuck(player.getLuck()).withPlayer(player);
		}
		LootContext lootContext = builder.build();
		return toNonNullList(lootTable.generateLootForPools(rng, lootContext));
	}

	public static void emptyInventory(IItemHandler itemHandler) {
		for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
			itemHandler.extractItem(slot, itemHandler.getStackInSlot(slot).getCount(), false);
		}
	}

	public static Optional<IItemHandler> getItemHandlerFrom(ICapabilityProvider provider, @Nullable EnumFacing side) {
		return provider.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side) ?
				Optional.ofNullable(provider.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) : Optional.empty();
	}

	/*
	 * Item-stack comparator.  Configurable in constructor to sort by localized or unlocalized name, as well as
	 * sort-order (regular or reverse).
	 *
	 * @author Shadowmage
	 */

	public static final class ComparatorItemStack implements Comparator<ItemStack> {

		public enum SortType {
			QUANTITY("sort_type_quantity") {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					int r = o1.getCount() - o2.getCount();
					if (r == 0) {
						return super.compare(o1, o2);
					}
					return r;
				}
			}, NAME("sort_type_name") {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					int r = o1.getDisplayName().compareTo(o2.getDisplayName());
					if (r == 0) {//if they have the same name, compare damage/tags
						return super.compare(o1, o2);
					}
					return r;
				}
			}, DAMAGE("sort_type_damage");

			public final String unlocalizedName;

			SortType(String unlocalizedName) {
				this.unlocalizedName = unlocalizedName;
			}

			public SortType next() {
				if (this == QUANTITY)
					return NAME;
				else if (this == NAME)
					return DAMAGE;
				else
					return QUANTITY;
			}

			@Override
			public String toString() {
				return unlocalizedName;
			}

			public int compare(ItemStack o1, ItemStack o2) {
				//noinspection ConstantConditions
				int itemComparison = o1.getItem().getRegistryName().toString().compareTo(o2.getItem().getRegistryName().toString());
				if (itemComparison != 0) {
					return itemComparison;
				}

				if (o1.getItemDamage() != o2.getItemDamage()) {
					return Integer.compare(o1.getItemDamage(), o2.getItemDamage());
				}

				if (o1.hasTagCompound()) {
					if (o2.hasTagCompound())
						//noinspection ConstantConditions
						return Integer.compare(o1.getTagCompound().hashCode(), o2.getTagCompound().hashCode());
					else
						return 1;
				} else if (o2.hasTagCompound()) {
					return -1;
				}

				return 0;
			}
		}

		public static enum SortOrder {
			ASCENDING(-1), DESCENDING(1);

			SortOrder(int mult) {
				this.mult = mult;
			}

			int mult;
		}

		private SortOrder sortOrder;
		private SortType sortType;

		/*
		 * @param order 1 for normal, -1 for reverse
		 */
		public ComparatorItemStack(SortType type, SortOrder order) {
			this.sortOrder = order;
			this.sortType = type;
		}

		public void setSortOrder(SortOrder order) {
			this.sortOrder = order;
		}

		public void setSortType(SortType type) {
			this.sortType = type;
		}

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			int result = sortType.compare(o1, o2);
			if (result == 0) {
				return 0;
			}
			return (result > 0 ? 1 : -1) * sortOrder.mult;
		}
	}

	public static List<Integer> getEmptySlotsRandomized(IItemHandler inventory, Random rand) {
		List<Integer> list = Lists.newArrayList();

		for (int i = 0; i < inventory.getSlots(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				list.add(i);
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	private static void shuffleItems(NonNullList<ItemStack> stacks, int numberOfSlots, Random rand) {
		numberOfSlots = numberOfSlots - stacks.size();

		int MIN_SIZE_TO_SPLIT = 3;

		List<ItemStack> splittableStacks = stacks.stream().filter(s -> (s.getCount() >= MIN_SIZE_TO_SPLIT)).collect(Collectors.toList());

		while (numberOfSlots > 0 && !splittableStacks.isEmpty()) {
			int slot = rand.nextInt(splittableStacks.size());

			ItemStack stack = splittableStacks.get(slot);

			int splitCount = MathHelper.getInt(rand, 1, stack.getCount() / 2);
			ItemStack splitStack = stack.splitStack(splitCount);

			if (stack.getCount() < MIN_SIZE_TO_SPLIT) {
				splittableStacks.remove(slot);
			}

			if (splitStack.getCount() >= MIN_SIZE_TO_SPLIT) {
				splittableStacks.add(splitStack);
			}

			stacks.add(splitStack);

			numberOfSlots--;
		}

		Collections.shuffle(stacks, rand);
	}

	public static void insertOrDropItem(IItemHandler handler, ItemStack stack, World world, BlockPos pos) {
		ItemStack remaining = insertItem(handler, stack, false);
		if (!remaining.isEmpty()) {
			dropItemInWorld(world, stack, pos);
		}
	}

	public static void insertOrDropItems(IItemHandler handler, List<ItemStack> stacks, World world, BlockPos pos) {
		for (ItemStack stack : stacks) {
			insertOrDropItem(handler, stack, world, pos);
		}
	}

	public static NonNullList<ItemStack> toNonNullList(List<ItemStack> stacks) {
		NonNullList<ItemStack> ret = NonNullList.create();

		for (ItemStack stack : stacks) {
			Validate.notNull(stack);
			ret.add(stack);
		}

		return ret;
	}

	public static Stream<ItemStack> stream(IItemHandler handler) {
		return StreamSupport.stream(getIterator(handler).spliterator(), false);
	}

	public static Iterable<ItemStack> getIterator(IItemHandler handler) {
		return () -> new Iterator<ItemStack>() {
			private int currentSlot = 0;

			@Override
			public boolean hasNext() {
				return currentSlot < handler.getSlots();
			}

			@Override
			public ItemStack next() {
				if (currentSlot < 0 || currentSlot >= handler.getSlots()) {
					throw new NoSuchElementException();
				}

				return handler.getStackInSlot(currentSlot++);
			}
		};
	}
}
