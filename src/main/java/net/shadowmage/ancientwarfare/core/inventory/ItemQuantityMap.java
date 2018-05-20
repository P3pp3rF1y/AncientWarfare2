package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ItemQuantityMap {

	private final Map<ItemHashEntry, Integer> map = new HashMap<>();

	/*
	 * puts all key/value pairs from the incoming map.  Overwrites existing counts.
	 */
	public void putAll(ItemQuantityMap incoming) {
		for (ItemHashEntry entry : incoming.map.keySet()) {
			map.put(entry, incoming.map.get(entry));
		}
	}

	/*
	 * is not a PUT operation -- merges quantities (values) instead of overwriting
	 */
	public void addAll(ItemQuantityMap incoming) {
		for (ItemHashEntry entry : incoming.map.keySet()) {
			if (map.containsKey(entry)) {
				map.put(entry, map.get(entry) + incoming.getCount(entry));
			} else {
				map.put(entry, incoming.map.get(entry));
			}
		}
	}

	/*
	 * removes given counts of items from this map, if the resulting count is 0 removes that entry as well.
	 */
	public void removeAll(ItemQuantityMap toRemove) {
		for (ItemHashEntry entry : toRemove.map.keySet()) {
			int currentCount = map.get(entry);
			int countToRemove = toRemove.map.get(entry);

			if (countToRemove >= currentCount) {
				remove(entry);
			} else {
				map.put(entry, currentCount - countToRemove);
			}
		}
	}

	/*
	 * @param entries must not be the exact key-set or concurrentModificationException will be thrown
	 */
	public void removeAll(Collection<ItemHashEntry> entries) {
		for (ItemHashEntry entry : entries) {
			remove(entry);
		}
	}

	/*
	 * decreases quantities by those contained in toRemove<br>
	 * if toRemove contains a key that this. does not, it is silently ignored, and removal continues
	 * at the next key
	 */
	public void decreaseQuantitiesFor(ItemQuantityMap toRemove) {
		for (ItemHashEntry entry : toRemove.map.keySet()) {
			if (contains(entry)) {
				decreaseCount(entry, toRemove.getCount(entry));
			}
		}
	}

	public int getCount(ItemHashEntry entry) {
		if (map.containsKey(entry)) {
			return map.get(entry);
		}
		return 0;
	}

	public int getCount(ItemStack item) {
		return getCount(new ItemHashEntry(item));
	}

	public void addCount(ItemStack item, int count) {
		addCount(new ItemHashEntry(item), count);
	}

	public void addCount(ItemHashEntry entry, int count) {
		if (!map.containsKey(entry)) {
			map.put(entry, count);
		} else
			map.put(entry, map.get(entry) + count);
	}

	public void decreaseCount(ItemStack item, int count) {
		decreaseCount(new ItemHashEntry(item), count);
	}

	public void decreaseCount(ItemHashEntry entry, int count) {
		if (map.containsKey(entry)) {
			int itemCount = map.get(entry) - count;
			if (itemCount <= 0) {
				map.remove(entry);
			} else {
				map.put(entry, itemCount);
			}
		}
	}

	public void remove(ItemStack item) {
		remove(new ItemHashEntry(item));
	}

	public void remove(ItemHashEntry entry) {
		map.remove(entry);
	}

	public void put(ItemStack item, int count) {
		put(new ItemHashEntry(item), count);
	}

	public void put(ItemHashEntry wrap, int count) {
		map.put(wrap, count);
	}

	public void clear() {
		this.map.clear();
	}

	public Set<ItemHashEntry> keySet() {
		return map.keySet();
	}

	public boolean contains(ItemHashEntry entry) {
		return map.containsKey(entry);
	}

	public boolean contains(ItemStack item) {
		return contains(new ItemHashEntry(item));
	}

	/*
	 * Return the most compact set of item-stacks that can represent the contents of this map.<br>
	 * May return multiple stacks of the same item if the quantity contained is > maxStackSize.<br>
	 */
	public NonNullList<ItemStack> getItems() {
		NonNullList<ItemStack> items = NonNullList.create();
		@Nonnull ItemStack outStack;
		int qty;
		for (ItemHashEntry wrap1 : map.keySet()) {
			qty = map.get(wrap1);
			while (qty > 0) {
				outStack = wrap1.getItemStack().copy();
				outStack.setCount(qty > outStack.getMaxStackSize() ? outStack.getMaxStackSize() : qty);
				qty -= outStack.getCount();
				items.add(outStack);
			}
		}
		return items;
	}

	/*
	 * Return a list of the most compact item-stacks as possible.<br>
	 * Stack max size for a given item is ignored -- stack size may be >64 (and may actually be up to Integer.MAX_VALUE)
	 *
	 * @param items will be filled with the item-stacks from this map, must not be NULL
	 */
	public void getCompactItems(NonNullList<ItemStack> items) {
		@Nonnull ItemStack outStack;
		for (ItemHashEntry wrap1 : map.keySet()) {
			outStack = wrap1.getItemStack().copy();
			outStack.setCount(map.get(wrap1));
			items.add(outStack);
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < entryList.tagCount(); i++) {
			NBTTagCompound entryTag = entryList.getCompoundTagAt(i);
			putEntryFromNBT(entryTag);
		}
	}

	public void putEntryFromNBT(NBTTagCompound entryTag) {
		NBTTagCompound itemTag = entryTag.getCompoundTag("item");
		ItemHashEntry entry = ItemHashEntry.readFromNBT(itemTag);
		if (!entry.getItemStack().isEmpty()) {
			int qty = entryTag.getInteger("quantity");
			if (qty == 0) { // when deserializing from NBT just remove all entries with 0
				map.remove(entry);
			} else {
				map.put(entry, qty);
			}
		} else {
			AncientWarfareCore.log.warn("Unable to read item from NBT into quantity map, probably no longer exists. {}", itemTag.toString());
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList entryList = new NBTTagList();
		for (ItemHashEntry entry : this.keySet()) {
			entryList.appendTag(writeEntryToNBT(entry));
		}
		tag.setTag("entryList", entryList);
		return tag;
	}

	public NBTTagCompound writeEntryToNBT(ItemHashEntry entry) {
		NBTTagCompound entryTag = new NBTTagCompound();
		entryTag.setTag("item", entry.writeToNBT());
		entryTag.setInteger("quantity", getCount(entry));
		return entryTag;
	}

	/*
	 * return the total number of inventory slots this quantity map would use if placed into slotted inventory
	 */
	public int getSlotUseCount() {
		int count = 0;
		int c1, c2, c3, c4;
		for (ItemHashEntry entry : this.map.keySet()) {
			c1 = entry.getItemStack().getMaxStackSize();
			c2 = this.getCount(entry);
			c3 = c2 / c1;
			c4 = c2 % c1;
			count += c3 + (c4 > 0 ? 1 : 0);
		}
		return count;
	}

	public int getTotalItemCount() {
		int count = 0;
		for (ItemHashEntry entry : this.map.keySet()) {
			count += map.get(entry);
		}
		return count;
	}

	/*
	 * Lightweight wrapper for an item stack as a hashable object suitable for use as keys in maps.<br>
	 * Uses item, item damage, and nbt-tag for hash-code.<br>
	 * Ignores quantity.<br>
	 * Immutable.
	 *
	 * @author Shadowmage
	 */
	public static final class ItemHashEntry {
		private final NBTTagCompound itemTag;
		@Nonnull
		private ItemStack cacheStack = ItemStack.EMPTY;

		/*
		 * @param item MUST NOT BE NULL
		 */
		public ItemHashEntry(ItemStack item) {
			ItemStack copy = item.copy();
			copy.setCount(1);
			itemTag = copy.writeToNBT(new NBTTagCompound());
		}

		@Override
		public int hashCode() {
			return itemTag.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (!(obj instanceof ItemHashEntry)) {
				return false;
			}
			return itemTag.equals(((ItemHashEntry) obj).itemTag);
		}

		public ItemStack getItemStack() {
			if (cacheStack.isEmpty()) {
				cacheStack = new ItemStack(itemTag);
			}
			return cacheStack;
		}

		private NBTTagCompound writeToNBT() {
			return itemTag.copy();
		}

		private static ItemHashEntry readFromNBT(NBTTagCompound tag) {
			return new ItemHashEntry(new ItemStack(tag));
		}
	}
}
