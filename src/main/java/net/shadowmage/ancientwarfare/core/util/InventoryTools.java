package net.shadowmage.ancientwarfare.core.util;

import com.google.common.collect.Lists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.oredict.OreDictionary;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InventoryTools {

	public static boolean canInventoryHold(IItemHandler handler, ItemStack stack) {
		int toMerge = stack.getCount();
		@Nonnull ItemStack existing;
		for(int index = 0; index < handler.getSlots(); index++) {
			existing = handler.getStackInSlot(index);
			if(existing.isEmpty()) {
				return true;
			} else if(doItemStacksMatch(stack, existing)) {
				toMerge -= existing.getMaxStackSize() - existing.getCount();
			}
			if(toMerge <= 0) {
				break;
			}
		}
		return toMerge <= 0;
	}

	public static boolean canInventoryHold(IItemHandler handler, List<ItemStack> stacks) {
		int emptySlots = 0;
		ItemQuantityMap itemQuantities = new ItemQuantityMap();

		for(ItemStack stack : stacks) {
			if(!stack.isEmpty()) {
				itemQuantities.addCount(stack, stack.getCount());
			}
		}

		for(int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if(stack.isEmpty()) {
				emptySlots++;
			} else if(itemQuantities.contains(stack)) {
				itemQuantities.decreaseCount(stack, stack.getMaxStackSize() - stack.getCount());
			}
		}

		return emptySlots >= itemQuantities.keySet().size();
	}

	public static NonNullList<ItemStack> insertItems(IItemHandler handler, List<ItemStack> stacks, boolean simulate) {
		NonNullList<ItemStack> remainingItems = NonNullList.create();
		if(simulate) {
			handler = cloneItemHandler(handler);
		}
		for(ItemStack stack : stacks) {
			ItemStack remainingItem = insertItem(handler, stack, false);
			if(!remainingItem.isEmpty()) {
				remainingItems.add(remainingItem);
			}
		}
		return remainingItems;
	}

	private static IItemHandler cloneItemHandler(IItemHandler handler) {
		ItemStackHandler copy = new ItemStackHandler(handler.getSlots());

		for(int slot = 0; slot < handler.getSlots(); slot++) {
			copy.setStackInSlot(slot, handler.getStackInSlot(slot).copy());
		}
		return copy;
	}

	public static ItemStack insertItem(IItemHandler handler, ItemStack stack, boolean simulate) {
		ItemStack remaining = stack.copy();
		for(int slot = 0; slot < handler.getSlots(); slot++) {
			remaining = handler.insertItem(slot, remaining, simulate);
			if(remaining.isEmpty()) {
				break;
			}
		}
		return remaining;
	}


	/*
	 * Checks if the input inventory can hold all of the items.<br>
	 * <br>
	 *
	 * @param inventory the inventory to check
	 * @param side      the side of the inventory to check
	 * @param stacks    a list of item stacks, need not be compacted/optimal, must not contain null entries
	 * @return true if input inventory can hold ALL of the input items
	 */
	public static boolean canInventoryHold(IInventory inventory, EnumFacing side, List<ItemStack> stacks) {
		return canInventoryHold(inventory, getSlotsForSide(inventory, side), stacks);
	}

	public static boolean canInventoryHold(IInventory inventory, int[] slots, List<ItemStack> stacks) {
		int emptySlots = 0;
		ItemQuantityMap itemQuantities = new ItemQuantityMap();

		for(ItemStack stack : stacks) {
			if(!stack.isEmpty()) {
				itemQuantities.addCount(stack, stack.getCount());
			}
		}

		for(int slot : slots) {
			@Nonnull ItemStack stack = inventory.getStackInSlot(slot);
			if(stack.isEmpty()) {
				emptySlots++;
			} else if(itemQuantities.contains(stack)) {
				itemQuantities.decreaseCount(stack, stack.getMaxStackSize() - stack.getCount());
			}
		}

		return emptySlots >= itemQuantities.keySet().size();
	}

	public static void updateCursorItem(EntityPlayerMP player, ItemStack stack, boolean shiftClick) {
		if(!stack.isEmpty()) {
			if(shiftClick) {
				stack = mergeItemStack(player.inventory, stack, (EnumFacing) null);
			}
			if(!stack.isEmpty()) {
				player.inventory.setItemStack(stack);
				player.updateHeldItem();
			}
		}
	}

	/*
	 * Attempt to merge stack into inventory via the given side, or general all-sides merge if side <0<br>
	 * Resorts to default general merge if inventory is not a sided inventory.<br>
	 * Double-pass merging.  First pass attempts to merge with partial stacks.  Second pass will place
	 * into empty slots if available.
	 *
	 * @param inventory the inventory to merge into, must not be null
	 * @param stack     the stack to merge, must not be null
	 * @param side      or <0 for none
	 * @return any remaining un-merged item, or null if completely merged
	 */
	public static ItemStack mergeItemStack(IInventory inventory, ItemStack stack, @Nullable EnumFacing side) {
		return mergeItemStack(inventory, stack, getSlotsForSide(inventory, side));
	}

	public static ItemStack mergeItemStack(IItemHandler handler, ItemStack stack) {
		ItemStack ret = stack;
		if(stack.isStackable()) {
			for(int i = 0; i < handler.getSlots(); i++) {
				if(stack.isEmpty()) {
					break;
				}

				ItemStack slotStack = handler.getStackInSlot(i);

				if(doItemStacksMatch(slotStack, stack)) {
					int j = slotStack.getCount() + stack.getCount();
					int maxSize = Math.min(handler.getSlotLimit(i), stack.getMaxStackSize());

					if(j <= maxSize) {
						stack.setCount(0);
						slotStack.setCount(j);
					} else if(slotStack.getCount() < maxSize) {
						stack.shrink(maxSize - slotStack.getCount());
						slotStack.setCount(maxSize);
					}
					ret = ItemStack.EMPTY;
				}
			}
		}

		if(!stack.isEmpty()) {
			for(int i = 0; i < handler.getSlots(); i++) {
				ItemStack itemstack1 = handler.getStackInSlot(i);

				if(itemstack1.isEmpty() && handler.insertItem(i, stack, true).isEmpty()) {
					int slotLimit = handler.getSlotLimit(i);
					if(stack.getCount() > slotLimit) {
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

	public static ItemStack mergeItemStack(IInventory inventory, ItemStack stack, int[] slotIndices) {
		if(slotIndices == null || slotIndices.length == 0) {
			return stack;
		}
		int toMove;
		@Nonnull ItemStack slotStack;
		for(int index : slotIndices) {
			toMove = stack.getCount();
			slotStack = inventory.getStackInSlot(index);
			if(doItemStacksMatchRelaxed(stack, slotStack)) {
				if(toMove > slotStack.getMaxStackSize() - slotStack.getCount()) {
					toMove = slotStack.getMaxStackSize() - slotStack.getCount();
				}
				stack.shrink(toMove);
				slotStack.grow(toMove);
				inventory.setInventorySlotContents(index, slotStack);
				inventory.markDirty();
			}
			if(stack.getCount() <= 0)//merged stack fully
			{
				return ItemStack.EMPTY;
			}
		}
		if(!stack.isEmpty()) {
			for(int index : slotIndices) {
				slotStack = inventory.getStackInSlot(index);
				if(slotStack.isEmpty() && inventory.isItemValidForSlot(index, stack)) {
					inventory.setInventorySlotContents(index, stack);
					inventory.markDirty();
					return ItemStack.EMPTY;//successful merge
				}
			}
		} else {
			return ItemStack.EMPTY;//successful merge
		}
		return stack;//partial or unsuccessful merge
	}

	/*
	 * Attempts to remove filter * quantity from inventory.  Returns removed item in return stack, or null if
	 * no items were removed.<br>
	 * Will only remove and return up to filter.getMaxStackSize() items, regardless of how many are requested.
	 *
	 * @return the removed item.
	 */
	public static ItemStack removeItems(IItemHandler handler, ItemStack filter, int quantity) {
		if(quantity <= 0) {
			return ItemStack.EMPTY;
		}
		if(quantity > filter.getMaxStackSize()) {
			quantity = filter.getMaxStackSize();
		}
		int returnCount = 0;
		@Nonnull ItemStack slotStack;
		for(int index = 0; index < handler.getSlots(); index++) {
			slotStack = handler.getStackInSlot(index);
			if(slotStack.isEmpty() || !doItemStacksMatchRelaxed(filter, slotStack)) {
				continue;
			}

			int toMove = Math.min(quantity - returnCount, slotStack.getCount());
			returnCount += toMove;

			handler.extractItem(index, toMove, false);

			if(quantity - returnCount <= 0) {
				break;
			}
		}
		@Nonnull ItemStack returnStack = ItemStack.EMPTY;
		if(returnCount > 0) {
			returnStack = filter.copy();
			returnStack.setCount(returnCount);
		}
		return returnStack;
	}

	public static ItemStack removeItems(IInventory inventory, @Nullable EnumFacing side, ItemStack filter, int quantity) {
		int[] slotIndices = getSlotsForSide(inventory, side);
		if(slotIndices == null || quantity <= 0) {
			return ItemStack.EMPTY;
		}
		if(quantity > filter.getMaxStackSize()) {
			quantity = filter.getMaxStackSize();
		}
		int returnCount = 0;
		@Nonnull ItemStack slotStack;
		for(int index : slotIndices) {
			slotStack = inventory.getStackInSlot(index);
			if(slotStack.isEmpty() || !doItemStacksMatchRelaxed(filter, slotStack)) {
				continue;
			}

			int toMove = Math.min(quantity - returnCount, slotStack.getCount());
			slotStack.getCount();
			if(toMove + returnCount > filter.getMaxStackSize()) {
				toMove = filter.getMaxStackSize() - returnCount;
			}
			returnCount += toMove;

			slotStack.shrink(toMove);
			if(slotStack.getCount() <= 0) {
				inventory.setInventorySlotContents(index, ItemStack.EMPTY);
			}
			inventory.markDirty();

			if(quantity - returnCount <= 0) {
				break;
			}
		}
		@Nonnull ItemStack returnStack = ItemStack.EMPTY;
		if(returnCount > 0) {
			returnStack = filter.copy();
			returnStack.setCount(returnCount);
		}
		return returnStack;
	}

	/*
	 * Apply the "container" system, for crafting purposes
	 *
	 * @param craft the inventory where craft occurred
	 * @param storage the inventory where the result, a "container item" may be dropped into
	 * @param index the craft slot where the item got consumed
	 * @param itemStack the item consumed
	 * @return ItemStack.EMPTY, or the item to drop/manage differently
	 */
	public static ItemStack getConsumedItem(IInventory craft, IInventory storage, int index, ItemStack itemStack) {
/* TODO likely remove once TileAutoCrafting uses getRemainingItems of the crafting recipe
		if (itemStack.getItem().hasContainerItem(itemStack))
        {
            @Nonnull ItemStack container = itemStack.getItem().getContainerItem(itemStack);
            if (container.isEmpty() || (container.isItemStackDamageable() && container.getItemDamage() > container.getMaxDamage()))//Container is invalid
            {
                return ItemStack.EMPTY;
            }
            if (itemStack.getItem().doesContainerItemLeaveCraftingGrid(itemStack) || !craft.getStackInSlot(index).isEmpty())//Need to use storage space
            {
                return InventoryTools.mergeItemStack(storage, container, (EnumFacing) null);//Merge into storage inventory, return any unmerged
            }
            else
            {
                craft.setInventorySlotContents(index, container);
            }
        }*/
		return ItemStack.EMPTY;
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
	public static int transferItems(IItemHandler from, IItemHandler to, ItemStack filter, int quantity, boolean ignoreDamage, boolean ignoreNBT) {
		int moved = 0;
		@Nonnull ItemStack s1, s2;
		int toMove = quantity;
		int stackSize;
		for(int fromIndex = 0; fromIndex < from.getSlots(); fromIndex++) {
			s1 = from.getStackInSlot(fromIndex);
			if(s1.isEmpty() || !doItemStacksMatch(filter, s1, ignoreDamage, ignoreNBT)) {
				continue;
			}
			stackSize = s1.getCount();
			if(s1.getCount() > toMove)//move partial stack
			{
				s2 = s1.copy();
				s2.setCount(toMove);
				s1.shrink(toMove);
				stackSize = s2.getCount();
				s2 = mergeItemStack(to, s2);
				if(!s2.isEmpty())//partial merge, destination full, break out
				{
					moved += stackSize - s2.getCount();
					mergeItemStack(from, s2);//put back the remainder of the partial stack that was copied out
					break;
				} else {
					moved += stackSize;
					toMove -= stackSize;
				}
			} else {
				s1 = mergeItemStack(to, s1);
				if(!s1.isEmpty())//destination inventory was full, break out
				{
					moved += stackSize - s1.getCount();
					break;
				} else {
					moved += stackSize;
					toMove -= stackSize;
				}
			}
			if(toMove <= 0) {
				break;
			}
		}
		return moved;
	}

	public static int findItemSlot(IItemHandler handler, Predicate<ItemStack> filter) {
		for(int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if(filter.test(stack)) {
				return slot;
			}
		}
		return -1;
	}

	public static int getCountOf(IItemHandler handler, ItemStack filterStack) {
		return getCountOf(handler, stack -> !stack.isEmpty() && doItemStacksMatchRelaxed(filterStack, stack));
	}

	public static int getCountOf(IItemHandler handler, Predicate<ItemStack> filter) {
		if(handler.getSlots() <= 0) {
			return 0;
		}
		int count = 0;
		for(int slot = 0; slot < handler.getSlots(); slot++) {
			@Nonnull ItemStack stack = handler.getStackInSlot(slot);
			if(filter.test(stack)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	/*
	 * return the found count of the input item stack (checks item/meta/tag)<br>
	 * if inv is not a sided inventory, or input side < 0, counts from entire inventory<br>
	 * otherwise only returns the item count from the input side
	 */
	public static int getCountOf(IInventory inv, @Nullable EnumFacing side, ItemStack filter) {
		if(inv.getSizeInventory() <= 0) {
			return 0;
		}
		int[] slotIndices = getSlotsForSide(inv, side);
		if(slotIndices == null || slotIndices.length == 0) {
			return 0;
		}
		int count = 0;
		@Nonnull ItemStack stack;
		for(int slotIndice : slotIndices) {
			stack = inv.getStackInSlot(slotIndice);
			if(!stack.isEmpty() && doItemStacksMatchRelaxed(filter, stack)) {
				count += stack.getCount();
			}
		}
		return count;
	}

	/*
	 * validates that stacks are the same item / damage / tag, ignores quantity
	 */
	public static boolean doItemStacksMatchRelaxed(ItemStack stack1, ItemStack stack2) {
		if(stack1 == stack2) {
			return true;
		}
		return OreDictionary.itemMatches(stack1, stack2, !stack1.isEmpty() && (stack1.isItemStackDamageable() || stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE)) && ItemStack.areItemsEqualIgnoreDurability(stack1, stack2) && stack1.areCapsCompatible(stack2);
	}

	public static boolean areItemStackTagsEqual(ItemStack stackA, ItemStack stackB) {
		if(stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		} else if(!stackA.isEmpty() && !stackB.isEmpty()) {
			if((stackA.getTagCompound() == null || stackA.getTagCompound().hasNoTags()) && (stackB.getTagCompound() != null && !stackB.getTagCompound().hasNoTags())) {
				return false;
			} else {
				return (stackA.getTagCompound() == null || stackA.getTagCompound().equals(stackB.getTagCompound())) && stackA.areCapsCompatible(stackB);
			}
		} else {
			return false;
		}
	}

	public static boolean doItemStacksMatch(ItemStack stackA, ItemStack stackB) {
		return doItemStacksMatch(stackA, stackB, false, false);
	}

	public static boolean doItemStacksMatch(ItemStack stackA, ItemStack stackB, boolean ignoreDamage, boolean ignoreNBT) {
		if(stackA.isEmpty() && stackB.isEmpty()) {
			return true;
		} else if(stackA.getItem() != stackB.getItem()) {
			return false;
		} else if((stackA.getHasSubtypes() || !ignoreDamage) && stackA.getItemDamage() != stackB.getItemDamage()) {
			return false;
		} else if(!ignoreNBT && stackA.getTagCompound() == null && stackB.getTagCompound() != null) {
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
		if(item.isEmpty() || world == null || world.isRemote) {
			return;
		}
		EntityItem entityToSpawn;
		x += world.rand.nextFloat() * 0.6f - 0.3f;
		y += world.rand.nextFloat() * 0.6f + 1 - 0.3f;
		z += world.rand.nextFloat() * 0.6f - 0.3f;
		entityToSpawn = new EntityItem(world, x, y, z, item);
		world.spawnEntity(entityToSpawn);
	}

	public static void dropInventoryInWorld(World world, IItemHandler inventory, BlockPos pos) {
		if(world.isRemote) {
			return;
		}
		if(inventory != null) {
			@Nonnull ItemStack stack;
			for(int i = 0; i < inventory.getSlots(); i++) {
				stack = inventory.getStackInSlot(i);
				if(stack.isEmpty()) {
					continue;
				}
				dropItemInWorld(world, inventory.extractItem(i, stack.getCount(), false), pos.getX(), pos.getY(), pos.getZ());
			}
		}
	}

	public static void dropInventoryInWorld(World world, IInventory localInventory, BlockPos pos) {
		dropInventoryInWorld(world, localInventory, pos.getX(), pos.getY(), pos.getZ());
	}

	public static void dropInventoryInWorld(World world, IInventory localInventory, double x, double y, double z) {
		if(world.isRemote) {
			return;
		}
		if(localInventory != null) {
			@Nonnull ItemStack stack;
			for(int i = 0; i < localInventory.getSizeInventory(); i++) {
				stack = localInventory.removeStackFromSlot(i);
				if(stack.isEmpty()) {
					continue;
				}
				dropItemInWorld(world, stack, x, y, z);
			}
		}
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
		for(int i = 0; i < inventory.getSizeInventory(); i++) {
			item = inventory.getStackInSlot(i);
			if(item.isEmpty()) {
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
		for(int i = 0; i < itemList.tagCount(); i++) {
			itemTag = itemList.getCompoundTagAt(i);
			slot = itemTag.getShort("slot");
			item = new ItemStack(itemTag);
			inventory.setInventorySlotContents(slot, item);
		}
	}

	/*
	 * Compacts an input item-stack list.<br>
	 * Any partial stacks in the input list will be merged into max-stack-size stacks.<br>
	 * The output list will be filled with the results of the merge, and will contain as few stacks as possible.<br>
	 * This particular method is on average 2x faster than compactStackList2, and also uses less memory.
	 */
	public static void compactStackList(List<ItemStack> in, List<ItemStack> out) {
		Map<ItemHashEntry, Integer> map = new HashMap<>();
		ItemHashEntry wrap;
		int count;
		for(ItemStack stack : in) {
			count = 0;
			wrap = new ItemHashEntry(stack);
			if(map.containsKey(wrap)) {
				count = map.get(wrap);
			}
			count += stack.getCount();
			map.put(wrap, count);
		}
		int qty;
		@Nonnull ItemStack outStack;
		for(ItemHashEntry wrap1 : map.keySet()) {
			qty = map.get(wrap1);
			while(qty > 0) {
				outStack = wrap1.getItemStack();
				outStack.setCount(qty > outStack.getMaxStackSize() ? outStack.getMaxStackSize() : qty);
				qty -= outStack.getCount();
				out.add(outStack);
			}
		}
	}

	/*
	 * Compacts an input item-stack list.<br>
	 * Any partial stacks in the input list will be merged into max-stack-size stacks.<br>
	 * The output list will be filled with the results of the merge, and will contain as few stacks as possible.<br>
	 * This particular method is on average 1/2 as fast as compactStackList, and also uses more memory.
	 */
	public static void compactStackList2(List<ItemStack> in, List<ItemStack> out) {
		int transfer = 0;
		int tmax;
		@Nonnull ItemStack copy;
		for(ItemStack inStack : in) {
			tmax = inStack.getCount();
			for(ItemStack outStack : out) {
				if(!InventoryTools.doItemStacksMatchRelaxed(inStack, outStack) || outStack.getCount() >= outStack.getMaxStackSize()) {
					continue;
				}
				transfer = outStack.getMaxStackSize() - outStack.getCount();
				if(transfer > tmax) {
					transfer = tmax;
				}
				outStack.grow(transfer);
				tmax -= transfer;
				if(tmax <= 0) {
					break;
				}
			}
			if(tmax > 0) {
				copy = inStack.copy();
				copy.setCount(tmax);
				out.add(copy);
			}
		}
	}

	/*
	 * Compacts in input item-stack list.<br>
	 * This particular method wraps an ItemQuantityMap, and has much better speed than the other two methods,
	 * but does use more memory in the process.  On average 2x faster than compactStackList and 4x+ faster than
	 * compacctStackList2
	 */
	public static List<ItemStack> compactStackList3(List<ItemStack> in) {
		ItemQuantityMap map = new ItemQuantityMap();
		for(ItemStack stack : in) {
			map.addCount(stack, stack.getCount());
		}
		return map.getItems();
	}

	public static void mergeItemStacks(List<ItemStack> stacks, List<ItemStack> stacksToMerge) {
		for(ItemStack stackToMerge : stacksToMerge) {
			if(stackToMerge.isEmpty()) {
				continue;
			}

			for(ItemStack stack : stacks) {
				if(stack.getCount() < stack.getMaxStackSize() && ItemHandlerHelper.canItemStacksStack(stackToMerge, stack)) {
					int count = Math.min(stack.getMaxStackSize() - stack.getCount(), stackToMerge.getCount());
					stack.grow(count);
					stackToMerge.shrink(count);
					if(stackToMerge.isEmpty()) {
						break;
					}
				}
			}
			if(!stackToMerge.isEmpty()) {
				stacks.add(stackToMerge);
			}
		}
	}

	public static void dropItemsInWorld(World world, NonNullList<ItemStack> stacks, BlockPos pos) {
		for(ItemStack stack : stacks) {
			dropItemInWorld(world, stack, pos);
		}
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
					if(r == 0) {
						return super.compare(o1, o2);
					}
					return r;
				}
			}, NAME("sort_type_name") {
				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					int r = o1.getDisplayName().compareTo(o2.getDisplayName());
					if(r == 0) {//if they have the same name, compare damage/tags
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
				if(this == QUANTITY)
					return NAME;
				else if(this == NAME)
					return DAMAGE;
				else
					return QUANTITY;
			}

			@Override
			public String toString() {
				return unlocalizedName;
			}

			public int compare(ItemStack o1, ItemStack o2) {
				if(o1.getItemDamage() != o2.getItemDamage()) {
					return o1.getItemDamage() - o2.getItemDamage();
				} else {
					if(o1.hasTagCompound()) {
						if(o2.hasTagCompound())
							return o1.getTagCompound().hashCode() - o2.getTagCompound().hashCode();
						else
							return 1;
					} else if(o2.hasTagCompound()) {
						return -1;
					}
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
		private String textInput = "";

		/*
		 * @param order 1 for normal, -1 for reverse
		 */
		public ComparatorItemStack(SortType type, SortOrder order) {
			this.sortOrder = order;
			this.sortType = type;
		}

		public void setTextInput(String text) {
			if(text == null) {
				text = "";
			}
			this.textInput = text;
		}

		public void setSortOrder(SortOrder order) {
			this.sortOrder = order;
		}

		public void setSortType(SortType type) {
			this.sortType = type;
		}

		@Override
		public int compare(ItemStack o1, ItemStack o2) {
			int val;
			if(!textInput.isEmpty()) {
				val = compareViaTextInput(o1, o2);
			} else {
				val = sortType.compare(o1, o2);
			}
			return val * sortOrder.mult;
		}

		private int compareViaTextInput(ItemStack o1, ItemStack o2) {
			String input = textInput.toLowerCase(Locale.ENGLISH);
			String n1 = o1.getDisplayName().toLowerCase(Locale.ENGLISH), n2 = o2.getDisplayName().toLowerCase(Locale.ENGLISH);
			if(n1.startsWith(input)) {
				if(!n2.startsWith(input))
					return 1;
			} else if(n2.startsWith(input)) {
				return -1;
			} else if(n1.contains(input)) {
				if(!n2.contains(input))
					return 1;
			} else if(n2.contains(input)) {
				return -1;
			}
			return sortType.compare(o1, o2);
		}
	}

	public static int[] getIndiceArrayForSpread(int len) {
		return new IndexHelper().getIndiceArrayForSpread(len);
	}

	public static int[] getSlotsForSide(IInventory inventory, @Nullable EnumFacing side) {
		if(side != null && inventory instanceof ISidedInventory) {
			return ((ISidedInventory) inventory).getSlotsForFace(side);
		}
		return new IndexHelper().getIndiceArrayForSpread(inventory.getSizeInventory());
	}

	public static class IndexHelper {
		private int previousLength;

		public int[] getIndiceArrayForSpread(int length) {
			int[] array = new int[length];
			for(int i = 0; i < length; i++) {
				array[i] = previousLength + i;
			}
			previousLength += length;
			return array;
		}
	}

	public static List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand) {
		List<Integer> list = Lists.newArrayList();

		for(int i = 0; i < inventory.getSizeInventory(); ++i) {
			if(inventory.getStackInSlot(i).isEmpty()) {
				list.add(i);
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	public static void shuffleItems(List<ItemStack> stacks, int numberOfSlots, Random rand) {
		numberOfSlots = numberOfSlots - stacks.size();

		int MIN_SIZE_TO_SPLIT = 3;

		List<ItemStack> splittableStacks = stacks.stream().filter(s -> (s.getCount() >= MIN_SIZE_TO_SPLIT)).collect(Collectors.toList());

		while(numberOfSlots > 0 && !splittableStacks.isEmpty()) {
			int slot = rand.nextInt(splittableStacks.size());

			ItemStack stack = splittableStacks.get(slot);

			int splitCount = MathHelper.getInt(rand, 1, stack.getCount() / 2);
			ItemStack splitStack = stack.splitStack(splitCount);

			if(stack.getCount() < MIN_SIZE_TO_SPLIT) {
				splittableStacks.remove(slot);
			}

			if(splitStack.getCount() >= MIN_SIZE_TO_SPLIT) {
				splittableStacks.add(splitStack);
			}

			stacks.add(splitStack);

			numberOfSlots--;
		}

		Collections.shuffle(stacks, rand);
	}

	public static void insertOrDropItem(IItemHandler handler, ItemStack stack, World world, BlockPos pos) {
		ItemStack remaining = insertItem(handler, stack, false);
		if(!remaining.isEmpty()) {
			dropItemInWorld(world, stack, pos);
		}
	}

	public static void insertOrDropItems(IItemHandler handler, List<ItemStack> stacks, World world, BlockPos pos) {
		for(ItemStack stack : stacks) {
			insertOrDropItem(handler, stack, world, pos);
		}
	}
}
