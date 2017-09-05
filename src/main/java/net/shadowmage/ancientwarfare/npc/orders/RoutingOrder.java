package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;

import java.util.List;

public class RoutingOrder extends OrderingList<RoutingOrder.RoutePoint> implements INBTSerializable<NBTTagCompound> {

    int routeDimension;

    public RoutingOrder() {
    }

    public void addRoutePoint(int side, int x, int y, int z) {
        add(new RoutePoint(side, x, y, z));
    }

    private boolean check(int index){
        return index >= 0 && index < size();
    }

    public void changeRouteType(int index, boolean isRmb) {
        if (check(index)) {
            get(index).changeRouteType(isRmb);
        }
    }

    public void changeBlockSide(int index) {
        if (check(index)) {
            get(index).changeBlockSide();
        }
    }

    public void toggleIgnoreDamage(int index) {
        if (check(index)) {
            get(index).toggleIgnoreDamage();
        }
    }

    public void toggleIgnoreTag(int index) {
        if (check(index)) {
            get(index).toggleIgnoreTag();
        }
    }

    public static RoutingOrder getRoutingOrder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemRoutingOrder) {
            RoutingOrder order = new RoutingOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemRoutingOrder) {
            stack.setTagInfo("orders", writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagList list = new NBTTagList();
        for (RoutePoint p : points) {
            list.appendTag(p.writeToNBT(new NBTTagCompound()));
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("entryList", list);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        clear();
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entryList.tagCount(); i++) {
            add(new RoutePoint(entryList.getCompoundTagAt(i)));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
    }

    public static class RoutePoint {
        boolean ignoreDamage, ignoreTag;
        RouteType routeType = RouteType.FILL_TARGET_TO;
        BlockPos target = new BlockPos();
        int blockSide = 0;
        ItemStack[] filters = new ItemStack[12];

        private RoutePoint(NBTTagCompound tag) {
            readFromNBT(tag);
        }

        public RoutePoint(int side, int x, int y, int z) {
            this.target = new BlockPos(x, y, z);
            this.blockSide = side;
        }

        private void changeBlockSide() {
            blockSide = blockSide == 5 ? 0 : blockSide + 1;
        }

        private void changeRouteType(boolean isRmb) {
            routeType = isRmb ? routeType.previous() : routeType.next();
        }

        public void setFilter(int index, ItemStack stack) {
            filters[index] = stack;
        }

        public int getBlockSide() {
            return blockSide;
        }

        public RouteType getRouteType() {
            return routeType;
        }

        public BlockPos getTarget() {
            return target;
        }

        public ItemStack getFilterInSlot(int slot) {
            return filters[slot];
        }

        public int getFilterSize(){
            return filters.length;
        }

        public boolean getIgnoreDamage() {
            return ignoreDamage;
        }

        public boolean getIgnoreTag() {
            return ignoreTag;
        }

        public void toggleIgnoreDamage() {
            ignoreDamage = !ignoreDamage;
        }

        public void toggleIgnoreTag() {
            ignoreTag = !ignoreTag;
        }

        private int depositAllItems(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int moved = 0;
            @Nonnull ItemStack stack;
            int stackSize = 0;
            int fromIndices[] = InventoryTools.getSlotsForSide(from, fromSide);
            boolean shouldMove;
            for (int index : fromIndices) {
                stack = from.getStackInSlot(index);
                if (stack.isEmpty()) {
                    continue;
                }
                shouldMove = false;
                stackSize = stack.getCount();
                for (ItemStack filter : filters) {
                    if (filter == null) {
                        continue;
                    }
                    if (InventoryTools.doItemStacksMatch(stack, filter, ignoreDamage, ignoreTag)) {
                        shouldMove = true;
                        break;
                    }
                }
                if (shouldMove) {
                    stack = InventoryTools.mergeItemStack(to, stack, toSide);
                    if (stack.isEmpty()) {
                        from.setInventorySlotContents(index, ItemStack.EMPTY);
                    }
                }
                if (stack.isEmpty() || stack.getCount() != stackSize) {
                    moved++;
                    from.markDirty();
                }
            }
            return moved;
        }

        private int depositAllItemsExcept(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int moved = 0;
            @Nonnull ItemStack stack;
            int stackSize = 0;
            int fromIndices[] = InventoryTools.getSlotsForSide(from, fromSide);
            boolean shouldMove;
            for (int index : fromIndices) {
                stack = from.getStackInSlot(index);
                if (stack.isEmpty()) {
                    continue;
                }
                shouldMove = true;
                stackSize = stack.getCount();
                for (ItemStack filter : filters) {
                    if (filter == null) {
                        continue;
                    }
                    if (InventoryTools.doItemStacksMatch(stack, filter, ignoreDamage, ignoreTag)) {
                        shouldMove = false;
                        break;
                    }
                }
                if (shouldMove) {
                    stack = InventoryTools.mergeItemStack(to, stack, toSide);
                    if (stack.isEmpty()) {
                        from.setInventorySlotContents(index, ItemStack.EMPTY);
                    }
                }
                if (stack.isEmpty() || stack.getCount() != stackSize) {
                    moved++;
                    from.markDirty();
                }
            }
            return moved;
        }

        private int fillTo(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int moved = 0;
            int toMove = 0;
            int foundCount = 0;
            int m1;
            for (ItemStack filter : filters) {
                if (filter == null) {
                    continue;
                }
                foundCount = InventoryTools.getCountOf(to, toSide, filter);
                toMove = filter.getCount();
                if (foundCount > toMove) {
                    continue;
                }
                toMove -= foundCount;
                m1 = InventoryTools.transferItems(from, to, filter, toMove, fromSide, toSide, ignoreDamage, ignoreTag);
                moved += m1 / filter.getMaxStackSize();
            }
            return moved;
        }
        
        private int depositRatio(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int movedTotal = 0;
            int toMove = 0;
            for (ItemStack filter : filters) {
                if (filter == null) {
                    continue;
                }
                int foundCount = InventoryTools.getCountOf(from, fromSide, filter);
                toMove = (int) (foundCount * (1f/(float)filter.getCount()));
                InventoryTools.transferItems(from, to, filter, toMove, fromSide, toSide, ignoreDamage, ignoreTag);
                movedTotal++;
            }
            
            return movedTotal;
        }
        
        private int depositExact(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int movedTotal = 0;
            int toMove = 0;
            int foundCount = 0;
            int moved;
            for (ItemStack filter : filters) {
                if (filter == null) {
                    continue;
                }
                foundCount = InventoryTools.getCountOf(from, fromSide, filter);
                toMove = filter.getCount();
                if (foundCount < toMove) {
                    continue;
                }
                if (!InventoryTools.canInventoryHold(to, toSide, filter))
                    continue;
                moved = InventoryTools.transferItems(from, to, filter, toMove, fromSide, toSide, ignoreDamage, ignoreTag);
                movedTotal += moved / filter.getMaxStackSize();
            }
            return movedTotal;
        }
        
        private int fillAtLeast(IInventory from, IInventory to, boolean reversed) {
            int fromSide = -1;
            int toSide = getBlockSide();
            if (reversed) {
                fromSide = getBlockSide();
                toSide = -1;
            }
            int movedTotal = 0;
            int toMove = 0;
            int foundCount = 0;
            int existingCount = 0;
            int moved;
            for (ItemStack filter : filters) {
                if (filter == null) {
                    continue;
                }
                foundCount = InventoryTools.getCountOf(from, fromSide, filter);
                existingCount = InventoryTools.getCountOf(to, toSide, filter);
                toMove = filter.getCount() - existingCount; // we only want to move items up to the specified filter size
                if (toMove < 1) {
                    // the target already has more than the filter specifies
                    continue;
                }
                
                if (foundCount < toMove) {
                    // the source doesn't have enough to fulfill the minimum requirement
                    continue;
                }
                @Nonnull ItemStack filterAdjusted = filter.copy();
                filterAdjusted.setCount(toMove);
                if (!InventoryTools.canInventoryHold(to, toSide, filterAdjusted))
                    continue;
                moved = InventoryTools.transferItems(from, to, filterAdjusted, foundCount, fromSide, toSide, ignoreDamage, ignoreTag);
                movedTotal += moved / filter.getMaxStackSize();
            }
            return movedTotal;
        }

        private final void readFromNBT(NBTTagCompound tag) {
            routeType = RouteType.values()[tag.getInteger("type")];
            target = new BlockPos(tag.getCompoundTag("position"));
            blockSide = tag.getInteger("blockSide");
            ignoreDamage = tag.getBoolean("ignoreDamage");
            ignoreTag = tag.getBoolean("ignoreTag");
            NBTTagList filterList = tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND);
            NBTTagCompound itemTag;
            int slot;
            for (int i = 0; i < filterList.tagCount(); i++) {
                itemTag = filterList.getCompoundTagAt(i);
                slot = itemTag.getInteger("slot");
                if(slot >= filters.length){
                    ItemStack[] temp = new ItemStack[slot+1];
                    System.arraycopy(filters, 0, temp, 0, filters.length);
                    filters = temp;
                }
                filters[slot] = new ItemStack(itemTag);
            }
        }

        private final NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setInteger("type", routeType.ordinal());
            tag.setTag("position", target.writeToNBT(new NBTTagCompound()));
            tag.setInteger("blockSide", blockSide);
            tag.setBoolean("ignoreDamage", ignoreDamage);
            tag.setBoolean("ignoreTag", ignoreTag);
            NBTTagList filterList = new NBTTagList();
            NBTTagCompound itemTag;
            for (int i = 0; i < filters.length; i++) {
                if (filters[i] == null) {
                    continue;
                }
                itemTag = filters[i].writeToNBT(new NBTTagCompound());
                itemTag.setInteger("slot", i);
                filterList.appendTag(itemTag);
            }
            tag.setTag("filterList", filterList);
            return tag;
        }

    }

    public enum RouteType {
        /*
         * fill target up to the specified quantity from couriers inventory
         */
        FILL_TARGET_TO("route.fill.upto"),

        /*
         * fill courier up to the specified quantity from targets inventory
         */
        FILL_COURIER_TO("route.take.upto"),

        /*
         * deposit any of the specified items from courier into target inventory
         * (no quantity limit)
         */
        DEPOSIT_ALL_OF("route.deposit.match"),

        /*
         * withdraw any of the specified items from target inventory into courier inventory
         * (no quantity limit)
         */
        WITHDRAW_ALL_OF("route.withdraw.match"),

        /*
         * deposit all items in courier inventory, except those matching filter items
         */
        DEPOSIT_ALL_EXCEPT("route.deposit.no_match"),

        /*
         * withdraw all items in target inventory except those matching filters
         */
        WITHDRAW_ALL_EXCEPT("route.withdraw.no_match"),
        
        /*
         * deposit specified ratio of items (ratio is 1/filterStacksize)
         */
        DEPOSIT_RATIO("route.deposit.ratio"),
        
        /*
         * withdraw specified ratio of items (ratio is 1/filterStacksize)
         */
        WITHDRAW_RATIO("route.withdraw.ratio"),
        
        /*
         * deposit exact number of items (or none at all if not possible)
         */
        DEPOSIT_EXACT("route.deposit.exact"),
        
        /*
         * withdraw exact number of items (or none at all if not possible)
         */
        WITHDRAW_EXACT("route.withdraw.exact"),
        
        /*
         * deposit a minimum of items
         */
        FILL_MINIMUM("route.fill.minimum"),
        
        /*
         * withdraw a minimum of items
         */
        TAKE_MINIMUM("route.take.minimum");

        final String key;

        RouteType(String key) {
            this.key = key;
        }

        public String getTranslationKey() {
            return key;
        }

        public static RouteType next(RouteType type) {
            return type == null ? RouteType.FILL_TARGET_TO : type.next();
        }
        
        public static RouteType previous(RouteType type) {
            return type == null ? RouteType.FILL_TARGET_TO : type.previous();
        }

        public RouteType next() {
            int ordinal = ordinal() + 1;
            if (ordinal >= RouteType.values().length) {
                ordinal = 0;
            }
            return RouteType.values()[ordinal];
        }
        
        public RouteType previous() {
            int ordinal = ordinal() - 1;
            if (ordinal < 0) {
                ordinal = RouteType.values().length - 1;
            }
            return RouteType.values()[ordinal];
        }

    }

    /*
     * do the routing action for the courier at the given route-point.  position/distance is not checked, should check in AI before calling<br>
     * returns the number of stacks processed for determining the length the courier should 'work' at the point
     */
    public int handleRouteAction(RoutePoint p, IInventory npc, IInventory target) {
        switch (p.routeType) {
            case FILL_COURIER_TO:
                return p.fillTo(target, npc, true);

            case FILL_TARGET_TO:
                return p.fillTo(npc, target, false);

            case DEPOSIT_ALL_EXCEPT:
                return p.depositAllItemsExcept(npc, target, false);

            case DEPOSIT_ALL_OF:
                return p.depositAllItems(npc, target, false);

            case WITHDRAW_ALL_EXCEPT:
                return p.depositAllItemsExcept(target, npc, true);

            case WITHDRAW_ALL_OF:
                return p.depositAllItems(target, npc, true);
                
            case DEPOSIT_RATIO:
                return p.depositRatio(npc, target, false);

            case WITHDRAW_RATIO:
                return p.depositRatio(target, npc, true);
                
            case DEPOSIT_EXACT:
                return p.depositExact(npc, target, false);
            
            case WITHDRAW_EXACT:
                return p.depositExact(target, npc, true);
            
            case FILL_MINIMUM:
                return p.fillAtLeast(npc, target, false);
                
            case TAKE_MINIMUM:
                return p.fillAtLeast(target, npc, true);

            default:
                return 0;
        }
    }


    public List<RoutePoint> getEntries() {
        return points;
    }

}
