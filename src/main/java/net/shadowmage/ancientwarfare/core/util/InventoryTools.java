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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
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
import java.util.stream.Collectors;

public class InventoryTools {

    public static boolean canInventoryHold(IInventory inventory, EnumFacing side, ItemStack stack) {
        return canInventoryHold(inventory, getSlotsForSide(inventory, side), stack);
    }

    public static boolean canInventoryHold(IInventory inventory, int[] slots, ItemStack stack) {
        int toMerge = stack.getCount();
        @Nonnull ItemStack existing;
        for (int index : slots) {
            existing = inventory.getStackInSlot(index);
            if (existing.isEmpty()) {
                return true;
            } else if (doItemStacksMatch(stack, existing)) {
                toMerge -= existing.getMaxStackSize() - existing.getCount();
            }
            if (toMerge <= 0) {
                break;
            }
        }
        return toMerge <= 0;
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

        for (ItemStack stack : stacks) {
            itemQuantities.addCount(stack, stack.getCount());
        }

        for (int slot : slots) {
            @Nonnull ItemStack stack = inventory.getStackInSlot(slot);
            if (stack.isEmpty()) {
                emptySlots++;
            } else if (itemQuantities.contains(stack)) {
                itemQuantities.decreaseCount(stack, stack.getMaxStackSize() - stack.getCount());
            }
        }

        return emptySlots >= itemQuantities.keySet().size();
    }

    public static void updateCursorItem(EntityPlayerMP player, ItemStack stack, boolean shiftClick){
        if (!stack.isEmpty()) {
            if (shiftClick) {
                stack = mergeItemStack(player.inventory, stack, (EnumFacing) null);
            }
            if (!stack.isEmpty()) {
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

    public static ItemStack mergeItemStack(IInventory inventory, ItemStack stack, int[] slotIndices) {
        if (slotIndices == null || slotIndices.length == 0) {
            return stack;
        }
        int toMove;
        @Nonnull ItemStack slotStack;
        for (int index : slotIndices) {
            toMove = stack.getCount();
            slotStack = inventory.getStackInSlot(index);
            if (doItemStacksMatch(stack, slotStack)) {
                if (toMove > slotStack.getMaxStackSize() - slotStack.getCount()) {
                    toMove = slotStack.getMaxStackSize() - slotStack.getCount();
                }
                stack.shrink(toMove);
                slotStack.grow(toMove);
                inventory.setInventorySlotContents(index, slotStack);
                inventory.markDirty();
            }
            if (stack.getCount() <= 0)//merged stack fully
            {
                return ItemStack.EMPTY;
            }
        }
        if (!stack.isEmpty()) {
            for (int index : slotIndices) {
                slotStack = inventory.getStackInSlot(index);
                if (slotStack.isEmpty() && inventory.isItemValidForSlot(index, stack)) {
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
    public static ItemStack removeItems(IInventory inventory, @Nullable EnumFacing side, ItemStack filter, int quantity) {
        int[] slotIndices = getSlotsForSide(inventory, side);
        if (slotIndices == null) {
            return ItemStack.EMPTY;
        }
        if (quantity > filter.getMaxStackSize()) {
            quantity = filter.getMaxStackSize();
        }
        @Nonnull ItemStack returnStack = ItemStack.EMPTY;
        int returnCount = 0;
        @Nonnull ItemStack slotStack;
        for (int index : slotIndices) {
            slotStack = inventory.getStackInSlot(index);
            if (slotStack.isEmpty() || !doItemStacksMatch(filter, slotStack)) {
                continue;
            }
            if (returnStack.isEmpty()) {
                returnStack = filter.copy();
            }
            int toMove = slotStack.getCount();
            if (toMove > quantity) {
                toMove = quantity;
            }
            if (toMove + returnStack.getCount() > returnStack.getMaxStackSize()) {
                toMove = returnStack.getMaxStackSize() - returnStack.getCount();
            }
            slotStack.shrink(toMove);
            quantity -= toMove;
            returnCount += toMove;
            if (slotStack.getCount() <= 0) {
                inventory.setInventorySlotContents(index, ItemStack.EMPTY);
            }
            inventory.markDirty();
            if (quantity <= 0) {
                break;
            }
        }
        if (!returnStack.isEmpty()) {
            returnStack.grow(returnCount);
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
    public static ItemStack getConsumedItem(IInventory craft, IInventory storage, int index, ItemStack itemStack){
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
    public static int transferItems(IInventory from, IInventory to, ItemStack filter, int quantity, EnumFacing fromSide, EnumFacing toSide) {
        return transferItems(from, to, filter, quantity, fromSide, toSide, false, false);
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
    public static int transferItems(IInventory from, IInventory to, ItemStack filter, int quantity, EnumFacing fromSide, EnumFacing toSide, boolean ignoreDamage, boolean ignoreNBT) {
        int moved = 0;
        int[] fromIndices = getSlotsForSide(from, fromSide);
        @Nonnull ItemStack s1, s2;
        int toMove = quantity;
        int stackSize;
        for (int fromIndex : fromIndices) {
            s1 = from.getStackInSlot(fromIndex);
            if (s1.isEmpty() || !doItemStacksMatch(filter, s1, ignoreDamage, ignoreNBT)) {
                continue;
            }
            stackSize = s1.getCount();
            if (s1.getCount() > toMove)//move partial stack
            {
                s2 = s1.copy();
                s2.setCount(toMove);
                s1.shrink(toMove);
                stackSize = s2.getCount();
                s2 = mergeItemStack(to, s2, toSide);
                if (!s2.isEmpty())//partial merge, destination full, break out
                {
                    moved += stackSize - s2.getCount();
                    mergeItemStack(from, s2, fromSide);//put back the remainder of the partial stack that was copied out
                    from.markDirty();
                    break;
                } else {
                    moved += stackSize;
                    toMove -= stackSize;
                    from.markDirty();
                }
            } else {
                s1 = mergeItemStack(to, s1, toSide);
                if (!s1.isEmpty())//destination inventory was full, break out
                {
                    moved += stackSize - s1.getCount();
                    from.markDirty();
                    break;
                } else {
                    moved += stackSize;
                    toMove -= stackSize;
                    from.setInventorySlotContents(fromIndex, ItemStack.EMPTY);
                    from.markDirty();
                }
            }
            if (toMove <= 0) {
                break;
            }
        }
        return moved;
    }

    /*
     * return a count of how many slots in an inventory contain a certain item stack (any size)
     */
    public static int getNumOfSlotsContaining(IInventory inv, EnumFacing side, ItemStack filter) {
        if (inv.getSizeInventory() <= 0) {
            return 0;
        }
        int[] slotIndices = getSlotsForSide(inv, side);
        if (slotIndices == null || slotIndices.length == 0) {
            return 0;
        }
        int count = 0;
        @Nonnull ItemStack stack;
        for (int slotIndice : slotIndices) {
            stack = inv.getStackInSlot(slotIndice);
            if (!stack.isEmpty() && doItemStacksMatch(filter, stack)) {
                count++;
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
        if (inv.getSizeInventory() <= 0) {
            return 0;
        }
        int[] slotIndices = getSlotsForSide(inv, side);
        if (slotIndices == null || slotIndices.length == 0) {
            return 0;
        }
        int count = 0;
        @Nonnull ItemStack stack;
        for (int slotIndice : slotIndices) {
            stack = inv.getStackInSlot(slotIndice);
            if (!stack.isEmpty() && doItemStacksMatch(filter, stack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    /*
     * validates that stacks are the same item / damage / tag, ignores quantity
     */
    public static boolean doItemStacksMatch(ItemStack stack1, ItemStack stack2) {
        if (stack1 == stack2) {
            return true;
        }
        return OreDictionary.itemMatches(stack1, stack2, !stack1.isEmpty() && (stack1.isItemStackDamageable() || stack1.getItemDamage() != OreDictionary.WILDCARD_VALUE)) && ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    public static boolean doItemStacksMatch(ItemStack stack1, ItemStack stack2, boolean ignoreDamage, boolean ignoreNBT) {
        if (!ignoreDamage && !ignoreNBT) {
            return doItemStacksMatch(stack1, stack2);
        }
        if (stack1.isEmpty()) {
            return stack2.isEmpty();
        }
        if (stack2.isEmpty()) {
            return false;
        }
        if (stack1.getItem() != stack2.getItem()) {
            return false;
        }
        if (!ignoreDamage && stack1.getItemDamage() != stack2.getItemDamage()) {
            return false;
        }
        return ignoreNBT || ItemStack.areItemStackTagsEqual(stack1, stack2);
    }

    /*
     * @param useOreDictionary -- NOTE: this setting overrides damage/nbt match if set to true, and uses oredict id comparison
     */
    public static boolean doItemStacksMatch(ItemStack stack1, ItemStack stack2, boolean matchDamage, boolean matchNBT, boolean useOreDictionary) {
        if (!useOreDictionary) {
            return doItemStacksMatch(stack1, stack2, !matchDamage, !matchNBT);
        } else {
            if (stack1.isEmpty()) {
                return stack2.isEmpty();
            }
            if (stack2.isEmpty()) {
                return false;
            }
            if (stack1.getItem() == stack2.getItem()) {
                int id[] = OreDictionary.getOreIDs(stack1);
                if (id == null || id.length == 0) {
                    return false;
                }
                int id2[] = OreDictionary.getOreIDs(stack2);
                if (id2 == null || id2.length == 0) {
                    return false;
                }
                for (int anId : id) {
                    for (int anId2 : id2) {
                        if (anId == anId2) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /*
     * drops the input itemstack into the world at the input position;
     */
    public static void dropItemInWorld(World world, ItemStack item, BlockPos pos) {
        dropItemInWorld(world, item, pos.getX(), pos.getY(), pos.getZ());
    }
    public static void dropItemInWorld(World world, ItemStack item, double x, double y, double z) {
        if (item.isEmpty() || world == null || world.isRemote) {
            return;
        }
        EntityItem entityToSpawn;
        x += world.rand.nextFloat() * 0.6f - 0.3f;
        y += world.rand.nextFloat() * 0.6f + 1 - 0.3f;
        z += world.rand.nextFloat() * 0.6f - 0.3f;
        entityToSpawn = new EntityItem(world, x, y, z, item);
        world.spawnEntity(entityToSpawn);
    }

    public static void dropInventoryInWorld(World world, IInventory localInventory, BlockPos pos) {
        dropInventoryInWorld(world, localInventory, pos.getX(), pos.getY(), pos.getZ());
    }

    public static void dropInventoryInWorld(World world, IInventory localInventory, double x, double y, double z) {
        if (world.isRemote) {
            return;
        }
        if (localInventory != null) {
            @Nonnull ItemStack stack;
            for (int i = 0; i < localInventory.getSizeInventory(); i++) {
                stack = localInventory.removeStackFromSlot(i);
                if (stack.isEmpty()) {
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
     * Compacts an input item-stack list.<br>
     * Any partial stacks in the input list will be merged into max-stack-size stacks.<br>
     * The output list will be filled with the results of the merge, and will contain as few stacks as possible.<br>
     * This particular method is on average 2x faster than compactStackList2, and also uses less memory.
     */
    public static void compactStackList(List<ItemStack> in, List<ItemStack> out) {
        Map<ItemHashEntry, Integer> map = new HashMap<>();
        ItemHashEntry wrap;
        int count;
        for (ItemStack stack : in) {
            count = 0;
            wrap = new ItemHashEntry(stack);
            if (map.containsKey(wrap)) {
                count = map.get(wrap);
            }
            count += stack.getCount();
            map.put(wrap, count);
        }
        int qty;
        @Nonnull ItemStack outStack;
        for (ItemHashEntry wrap1 : map.keySet()) {
            qty = map.get(wrap1);
            while (qty > 0) {
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
        for (ItemStack inStack : in) {
            tmax = inStack.getCount();
            for (ItemStack outStack : out) {
                if (!InventoryTools.doItemStacksMatch(inStack, outStack) || outStack.getCount() >= outStack.getMaxStackSize()) {
                    continue;
                }
                transfer = outStack.getMaxStackSize() - outStack.getCount();
                if (transfer > tmax) {
                    transfer = tmax;
                }
                outStack.grow(transfer);
                tmax -= transfer;
                if (tmax <= 0) {
                    break;
                }
            }
            if (tmax > 0) {
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
        for (ItemStack stack : in) {
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

    /*
     * Item-stack comparator.  Configurable in constructor to sort by localized or unlocalized name, as well as
     * sort-order (regular or reverse).
     *
     * @author Shadowmage
     */
    public static final class ComparatorItemStack implements Comparator<ItemStack> {

        public enum SortType {
            QUANTITY("sort_type_quantity"){
                @Override
                public int compare(ItemStack o1, ItemStack o2){
                    int r = o1.getCount() - o2.getCount();
                    if(r == 0){
                        return super.compare(o1, o2);
                    }
                    return r;
                }
            },
            NAME("sort_type_name"){
                @Override
                public int compare(ItemStack o1, ItemStack o2){
                    int r = o1.getDisplayName().compareTo(o2.getDisplayName());
                    if(r == 0){//if they have the same name, compare damage/tags
                        return super.compare(o1, o2);
                    }
                    return r;
                }
            },
            DAMAGE("sort_type_damage");

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

            public int compare(ItemStack o1, ItemStack o2){
                if (o1.getItemDamage() != o2.getItemDamage()) {
                    return o1.getItemDamage() - o2.getItemDamage();
                } else {
                    if (o1.hasTagCompound()){
                        if(o2.hasTagCompound())
                            return o1.getTagCompound().hashCode() - o2.getTagCompound().hashCode();
                        else
                            return 1;
                    } else if (o2.hasTagCompound()) {
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
            if (text == null) {
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
            }else{
                val = sortType.compare(o1, o2);
            }
            return val * sortOrder.mult;
        }

        private int compareViaTextInput(ItemStack o1, ItemStack o2) {
            String input = textInput.toLowerCase(Locale.ENGLISH);
            String n1 = o1.getDisplayName().toLowerCase(Locale.ENGLISH), n2 = o2.getDisplayName().toLowerCase(Locale.ENGLISH);
            if (n1.startsWith(input)){
                if(!n2.startsWith(input))
                    return 1;
            } else if (n2.startsWith(input)) {
                return -1;
            } else if (n1.contains(input)){
                if(!n2.contains(input))
                    return 1;
            } else if (n2.contains(input)) {
                return -1;
            }
            return sortType.compare(o1, o2);
        }
    }

    public static int[] getIndiceArrayForSpread(int len) {
        return new IndexHelper().getIndiceArrayForSpread(len);
    }

    public static int[] getSlotsForSide(IInventory inventory, @Nullable EnumFacing side) {
        if (side != null && inventory instanceof ISidedInventory) {
            return ((ISidedInventory) inventory).getSlotsForFace(side);
        }
        return new IndexHelper().getIndiceArrayForSpread(inventory.getSizeInventory());
    }

    public static class IndexHelper{
        private int previousLength;

        public int[] getIndiceArrayForSpread(int length) {
            int[] array = new int[length];
            for (int i = 0; i < length; i++) {
                array[i] = previousLength + i;
            }
            previousLength += length;
            return array;
        }
    }

    public static List<Integer> getEmptySlotsRandomized(IInventory inventory, Random rand)
    {
        List<Integer> list = Lists.newArrayList();

        for (int i = 0; i < inventory.getSizeInventory(); ++i)
        {
            if (inventory.getStackInSlot(i).isEmpty())
            {
                list.add(i);
            }
        }

        Collections.shuffle(list, rand);
        return list;
    }

    public static void shuffleItems(List<ItemStack> stacks, int numberOfSlots, Random rand)
    {
        numberOfSlots = numberOfSlots - stacks.size();

        int MIN_SIZE_TO_SPLIT = 3;

        List<ItemStack> splittableStacks = stacks.stream().filter(s -> (s.getCount() >= MIN_SIZE_TO_SPLIT)).collect(Collectors.toList());

        while (numberOfSlots > 0 && !splittableStacks.isEmpty())
        {
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
}
