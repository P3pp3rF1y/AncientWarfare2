package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nonnull;
import java.util.*;

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
     * removes all key/value pairs that have corresponding keys contained in toRemove
     */
    public void removeAll(ItemQuantityMap toRemove) {
        for (ItemHashEntry entry : toRemove.map.keySet()) {
            remove(entry);
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
        }else
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
            }else{
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

    @Override
    public String toString() {
        String out = "Item Quantity Map:";
        for (ItemHashEntry wrap : map.keySet()) {
            out = out + "\n   " + wrap.item.getUnlocalizedName();
            out = out + " : " + map.get(wrap);
        }
        return out;
    }

    /*
     * Return the most compact set of item-stacks that can represent the contents of this map.<br>
     * May return multiple stacks of the same item if the quantity contained is > maxStackSize.<br>
     */
    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
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
    public void getCompactItems(List<ItemStack> items) {
        @Nonnull ItemStack outStack;
        for (ItemHashEntry wrap1 : map.keySet()) {
            outStack = wrap1.getItemStack().copy();
            outStack.setCount(map.get(wrap1));
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
            if(entry!=null) {
                qty = entryTag.getInteger("quantity");
                map.put(entry, qty);
            }
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
        private final Item item;
        private final int damage;
        private final NBTTagCompound itemTag;
        @Nonnull
        private ItemStack cacheStack = ItemStack.EMPTY;

        /*
         * @param item MUST NOT BE NULL
         */
        public ItemHashEntry(ItemStack item) {
            if (item.isEmpty()) {
                throw new IllegalArgumentException("Stack may not be null");
            }
            this.item = item.getItem();
            if (this.item == Items.AIR) {
                throw new IllegalArgumentException("Item may not be null");
            }
            this.damage = item.getItemDamage();
            if (item.hasTagCompound()) {
                //noinspection ConstantConditions
                this.itemTag = item.getTagCompound().copy();
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
            return itemTag == null ? null : itemTag.copy();
        }

        /*
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
            long hash = 31 + item.hashCode();
            hash = 31 * hash + damage;
            if (itemTag != null) {
                hash = 31 * hash + itemTag.hashCode();
            }
            return ((Long)hash).hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ItemHashEntry)) {
                return false;
            }
            ItemHashEntry wrap = (ItemHashEntry) obj;
            if(item == wrap.item && damage == wrap.damage){
                if (itemTag == null)
                    return wrap.itemTag == null;
                else
                    return wrap.itemTag != null && itemTag.equals(wrap.itemTag);
            }
            return false;
        }

        @Override
        public String toString() {
            return "StackHashWrap: " + item.getUnlocalizedName() + "@" + damage;
        }

        public ItemHashEntry copy() {
            return new ItemHashEntry(item, damage, itemTag);
        }

        public ItemStack getItemStack() {
            if (!cacheStack.isEmpty()) {
                return cacheStack;
            } else {
                @Nonnull ItemStack stack = new ItemStack(item, 1, damage);
                if (itemTag != null) {
                    stack.setTagCompound((NBTTagCompound) itemTag.copy());
                }
                cacheStack = stack;
                return cacheStack;
            }
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            return writeToNBT(this, tag);
        }

        public static NBTTagCompound writeToNBT(ItemHashEntry entry, NBTTagCompound tag) {
            tag.setString("itemName", entry.item.getRegistryName().toString());
            tag.setInteger("damage", entry.damage);
            if (entry.itemTag != null) {
                tag.setTag("itemTag", entry.itemTag);
            }
            return tag;
        }

        public static ItemHashEntry readFromNBT(NBTTagCompound tag) {
            Item item = Item.REGISTRY.getObject(new ResourceLocation(tag.getString("itemName")));
            if (item != null){
                int dmg = tag.getInteger("damage");
                NBTTagCompound itemTag = tag.hasKey("itemTag") ? tag.getCompoundTag("itemTag") : null;
                return new ItemHashEntry(item, dmg, itemTag);
            }
            AncientWarfareCore.log.error("Missing item: " + tag.getString("itemName"));
            return null;
        }
    }

}
