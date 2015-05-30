package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.item.ItemRoutingOrder;

import java.util.List;

public class RoutingOrder extends OrderingList<RoutingOrder.RoutePoint> implements INBTSerialable {

    int routeDimension;

    public RoutingOrder() {
    }

    public void addRoutePoint(int side, int x, int y, int z) {
        add(new RoutePoint(side, x, y, z));
    }

    private boolean check(int index){
        return index >= 0 && index < size();
    }

    public void changeRouteType(int index) {
        if (check(index)) {
            get(index).changeRouteType();
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

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        clear();
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entryList.tagCount(); i++) {
            add(new RoutePoint(entryList.getCompoundTagAt(i)));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList list = new NBTTagList();
        for (RoutePoint p : points) {
            list.appendTag(p.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("entryList", list);
        return tag;
    }

    public static RoutingOrder getRoutingOrder(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemRoutingOrder) {
            RoutingOrder order = new RoutingOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemRoutingOrder) {
            stack.setTagInfo("orders", writeToNBT(new NBTTagCompound()));
        }
    }

    public static class RoutePoint {
        boolean ignoreDamage, ignoreTag;
        RouteType routeType = RouteType.FILL_TARGET_TO;
        BlockPosition target = new BlockPosition();
        int blockSide = 0;
        ItemStack[] filters = new ItemStack[8];

        private RoutePoint(NBTTagCompound tag) {
            readFromNBT(tag);
        }

        public RoutePoint(int side, int x, int y, int z) {
            this.target = new BlockPosition(x, y, z);
            this.blockSide = side;
        }

        private void changeBlockSide() {
            blockSide = blockSide == 5 ? 0 : blockSide + 1;
        }

        private void changeRouteType() {
            routeType = routeType.next();
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

        public BlockPosition getTarget() {
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
            ItemStack stack;
            int stackSize = 0;
            int fromIndices[];
            boolean shouldMove;
            if (from instanceof ISidedInventory && fromSide >= 0) {
                fromIndices = ((ISidedInventory) from).getAccessibleSlotsFromSide(fromSide);
            } else {
                fromIndices = InventoryTools.getIndiceArrayForSpread(0, from.getSizeInventory());
            }
            for (int index : fromIndices) {
                shouldMove = false;
                stack = from.getStackInSlot(index);
                if (stack == null) {
                    continue;
                }
                stackSize = stack.stackSize;
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
                    if (stack == null) {
                        from.setInventorySlotContents(index, null);
                    }
                }
                if (stack == null || stack.stackSize != stackSize) {
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
            ItemStack stack;
            int stackSize = 0;
            int fromIndices[];
            boolean shouldMove;
            if (from instanceof ISidedInventory && fromSide >= 0) {
                fromIndices = ((ISidedInventory) from).getAccessibleSlotsFromSide(fromSide);
            } else {
                fromIndices = InventoryTools.getIndiceArrayForSpread(0, from.getSizeInventory());
            }
            for (int index : fromIndices) {
                shouldMove = true;
                stack = from.getStackInSlot(index);
                if (stack == null) {
                    continue;
                }
                stackSize = stack.stackSize;
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
                    if (stack == null) {
                        from.setInventorySlotContents(index, null);
                    }
                }
                if (stack == null || stack.stackSize != stackSize) {
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
                toMove = filter.stackSize;
                if (foundCount > toMove) {
                    continue;
                }
                toMove -= foundCount;
                m1 = InventoryTools.transferItems(from, to, filter, toMove, fromSide, toSide, ignoreDamage, ignoreTag);
                moved += m1 / filter.getMaxStackSize();
            }
            return moved;
        }

        private final void readFromNBT(NBTTagCompound tag) {
            routeType = RouteType.values()[tag.getInteger("type")];
            target = new BlockPosition(tag.getCompoundTag("position"));
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
                filters[slot] = InventoryTools.readItemStack(itemTag);
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
                itemTag = InventoryTools.writeItemStack(filters[i]);
                itemTag.setInteger("slot", i);
                filterList.appendTag(itemTag);
            }
            tag.setTag("filterList", filterList);
            return tag;
        }

    }

    public enum RouteType {
        /**
         * fill target up to the specified quantity from couriers inventory
         */
        FILL_TARGET_TO("route.fill.target"),

        /**
         * fill courier up to the specified quantity from targets inventory
         */
        FILL_COURIER_TO("route.fill.courier"),

        /**
         * deposit any of the specified items from courier into target inventory
         * (no quantity limit)
         */
        DEPOSIT_ALL_OF("route.deposit.match"),

        /**
         * withdraw any of the specified items from target inventory into courier inventory
         * (no quantity limit)
         */
        WITHDRAW_ALL_OF("route.withdraw.match"),

        /**
         * deposit all items in courier inventory, except those matching filter items
         */
        DEPOSIT_ALL_EXCEPT("route.deposit.no_match"),

        /**
         * withdraw all items in target inventory except those matching filters
         */
        WITHDRAW_ALL_EXCEPT("route.withdraw.no_match");

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

        public RouteType next() {
            int ordinal = ordinal() + 1;
            if (ordinal >= RouteType.values().length) {
                ordinal = 0;
            }
            return RouteType.values()[ordinal];
        }

    }

    /**
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

            default:
                return 0;
        }
    }


    public List<RoutePoint> getEntries() {
        return points;
    }

}
