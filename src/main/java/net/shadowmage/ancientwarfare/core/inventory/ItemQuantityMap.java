package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class ItemQuantityMap {

    private final Map<ItemHashEntry, ItemCount> map = new HashMap<ItemHashEntry, ItemCount>();

    /**
     * puts all key/value pairs from the incoming map.  Overwrites existing counts.
     */
    public void putAll(ItemQuantityMap incoming) {
        for (ItemHashEntry entry : incoming.map.keySet()) {
            if (map.containsKey(entry)) {
                map.get(entry).count = incoming.map.get(entry).count;
            } else {
                map.put(entry, incoming.map.get(entry));
            }
        }
    }

    /**
     * is not a PUT operation -- merges quantities (values) instead of overwriting
     */
    public void addAll(ItemQuantityMap incoming) {
        for (ItemHashEntry entry : incoming.map.keySet()) {
            if (map.containsKey(entry)) {
                map.get(entry).count += incoming.getCount(entry);
            } else {
                ItemCount count = new ItemCount();
                count.count = incoming.map.get(entry).count;
                map.put(entry, count);
            }
        }
    }

    /**
     * removes all key/value pairs that have corresponding keys contained in toRemove
     */
    public void removeAll(ItemQuantityMap toRemove) {
        for (ItemHashEntry entry : toRemove.map.keySet()) {
            remove(entry);

        }
    }

    /**
     * @param entries must not be the exact key-set or concurrentModificationException will be thrown
     */
    public void removeAll(Collection<ItemHashEntry> entries) {
        for (ItemHashEntry entry : entries) {
            remove(entry);
        }
    }

    /**
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
            return map.get(entry).count;
        }
        return 0;
    }

    public int getCount(ItemStack item) {
        ItemHashEntry wrap = new ItemHashEntry(item);
        if (!map.containsKey(wrap)) {
            return 0;
        }
        return map.get(wrap).count;
    }

    public void addCount(ItemStack item, int count) {
        addCount(new ItemHashEntry(item), count);
    }

    public void addCount(ItemHashEntry entry, int count) {
        if (!map.containsKey(entry)) {
            map.put(entry, new ItemCount());
        }
        map.get(entry).count += count;
    }

    public void decreaseCount(ItemStack item, int count) {
        decreaseCount(new ItemHashEntry(item), count);
    }

    public void decreaseCount(ItemHashEntry entry, int count) {
        if (map.containsKey(entry)) {
            ItemCount itemCount = map.get(entry);
            itemCount.count -= count;
            if (itemCount.count <= 0) {
                map.remove(entry);
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
        if (map.containsKey(wrap)) {
            map.get(wrap).count = count;
        } else {
            ItemCount c = new ItemCount();
            c.count = count;
            map.put(wrap, c);
        }
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

    @Override
    public String toString() {
        String out = "Item Quantity Map:";
        for (ItemHashEntry wrap : map.keySet()) {
            out = out + "\n   " + wrap.item.getUnlocalizedName();
            out = out + " : " + map.get(wrap).count;
        }
        return out;
    }

    /**
     * Return the most compact set of item-stacks that can represent the contents of this map.<br>
     * May return multiple stacks of the same item if the quantity contained is > maxStackSize.<br> *
     *
     * @param items will be filled with the item-stacks from this map, must not be NULL
     */
    public void getItems(List<ItemStack> items) {
        ItemStack outStack;
        int qty;
        for (ItemHashEntry wrap1 : map.keySet()) {
            qty = map.get(wrap1).count;
            while (qty > 0) {
                outStack = wrap1.getItemStack().copy();
                outStack.stackSize = qty > outStack.getMaxStackSize() ? outStack.getMaxStackSize() : qty;
                qty -= outStack.stackSize;
                items.add(outStack);
            }
        }
    }

    /**
     * Return a list of the most compact item-stacks as possible.<br>
     * Stack max size for a given item is ignored -- stack size may be >64 (and may actually be up to Integer.MAX_VALUE)
     *
     * @param items will be filled with the item-stacks from this map, must not be NULL
     */
    public void getCompactItems(List<ItemStack> items) {
        ItemStack outStack;
        for (ItemHashEntry wrap1 : map.keySet()) {
            outStack = wrap1.getItemStack().copy();
            outStack.stackSize = map.get(wrap1).count;
            items.add(outStack);
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        NBTTagCompound entryTag;
        ItemHashEntry entry;
        int qty;
        for (int i = 0; i < entryList.tagCount(); i++) {
            entryTag = entryList.getCompoundTagAt(i);
            entry = ItemHashEntry.readFromNBT(entryTag);
            qty = entryTag.getInteger("quantity");
            map.put(entry, new ItemCount(qty));
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList entryList = new NBTTagList();
        NBTTagCompound entryTag;
        for (ItemHashEntry entry : this.keySet()) {
            entryTag = entry.writeToNBT(new NBTTagCompound());
            entryTag.setInteger("quantity", getCount(entry));
            entryList.appendTag(entryTag);
        }
        tag.setTag("entryList", entryList);
        return tag;
    }

    /**
     * return the total number of inventory slots this quantity map would use if placed into slotted inventory
     */
    public int getSlotUseCount() {
        int count = 0;
        int c1, c2, c3, c4;
        for (ItemHashEntry entry : this.map.keySet()) {
            c1 = entry.item.getItemStackLimit(entry.getItemStack());
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
            count += map.get(entry).count;
        }
        return count;
    }

    /**
     * used by ItemQuantityMap for tracking item quantities for a given ItemStackHashWrap
     *
     * @author Shadowmage
     */
    private static final class ItemCount {
        private int count;

        private ItemCount() {
        }

        private ItemCount(int qty) {
            count = qty;
        }
    }

    /**
     * Lightweight wrapper for an item stack as a hashable object suitable for use as keys in maps.<br>
     * Uses item, item damage, and nbt-tag for hash-code.<br>
     * Ignores quantity.<br>
     * Immutable.
     *
     * @author Shadowmage
     */
    public static final class ItemHashEntry {
        private final Item item;
        private final int damage;
        private final NBTTagCompound itemTag;
        private ItemStack cacheStack;

        /**
         * @param item MUST NOT BE NULL
         */
        public ItemHashEntry(ItemStack item) {
            if (item == null) {
                throw new IllegalArgumentException("Stack may not be null");
            }
            this.item = item.getItem();
            if (this.item == null) {
                throw new IllegalArgumentException("Item may not be null");
            }
            this.damage = item.getItemDamage();
            if (item.hasTagCompound()) {
                this.itemTag = (NBTTagCompound) item.getTagCompound().copy();
            } else {
                this.itemTag = null;
            }
        }

        public Item getItem() {
            return item;
        }

        public int getDamage() {
            return damage;
        }

        public NBTTagCompound getTag() {
            return (NBTTagCompound) (itemTag == null ? null : itemTag.copy());
        }

        /**
         * internal constructor used for copying/cloning
         */
        private ItemHashEntry(Item item, int damage, NBTTagCompound tag) {
            if (item == null) {
                throw new IllegalArgumentException("Item may not be null");
            }
            this.item = item;
            this.damage = damage;
            this.itemTag = (NBTTagCompound) (tag == null ? null : tag.copy());
        }

        @Override
        public int hashCode() {
            int hash = 1;
            if (item != null) {
                hash = 31 * hash + item.hashCode();
            }
            hash = 31 * hash + damage;
//  hash = 31*hash + stack.stackSize;//noop for implementation, only care about identity, not quantity
            if (itemTag != null) {
                hash = 31 * hash + itemTag.hashCode();
            }
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj.getClass() != this.getClass()) {
                return false;
            }
            ItemHashEntry wrap = (ItemHashEntry) obj;
            boolean tagsMatch;
            if (itemTag == null) {
                tagsMatch = wrap.itemTag == null;
            } else
                tagsMatch = wrap.itemTag != null && itemTag.equals(wrap.itemTag);
            return item == wrap.item && damage == wrap.damage && tagsMatch;
        }

        @Override
        public String toString() {
            return "StackHashWrap: " + item.getUnlocalizedName() + "@" + damage;
        }

        public ItemHashEntry copy() {
            return new ItemHashEntry(item, damage, itemTag);
        }

        public ItemStack getItemStack() {
            if (cacheStack != null) {
                return cacheStack;
            } else {
                ItemStack stack = new ItemStack(item, 1, damage);
                if (itemTag != null) {
                    stack.stackTagCompound = (NBTTagCompound) itemTag.copy();
                }
                cacheStack = stack;
                return cacheStack;
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            return writeToNBT(this, tag);
        }

        public static NBTTagCompound writeToNBT(ItemHashEntry entry, NBTTagCompound tag) {
            tag.setString("itemName", Item.itemRegistry.getNameForObject(entry.item));
            tag.setInteger("damage", entry.damage);
            if (entry.itemTag != null) {
                tag.setTag("itemTag", entry.itemTag);
            }
            return tag;
        }

        public static ItemHashEntry readFromNBT(NBTTagCompound tag) {
            Item item = (Item) Item.itemRegistry.getObject(tag.getString("itemName"));
            int dmg = tag.getInteger("damage");
            NBTTagCompound itemTag = tag.hasKey("itemTag") ? tag.getCompoundTag("itemTag") : null;
            return new ItemHashEntry(item, dmg, itemTag);
        }
    }

}
