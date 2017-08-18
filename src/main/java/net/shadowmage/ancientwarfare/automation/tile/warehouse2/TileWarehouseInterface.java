package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.ArrayList;
import java.util.List;

public class TileWarehouseInterface extends TileControlled implements IInventory, IInteractableTile {

    final InventoryBasic inventory = new InventoryBasic(27);

    private boolean init = false;
    private final List<InterfaceFillRequest> fillRequests = new ArrayList<InterfaceFillRequest>();
    private final List<InterfaceEmptyRequest> emptyRequests = new ArrayList<InterfaceEmptyRequest>();
    List<WarehouseInterfaceFilter> filters = new ArrayList<WarehouseInterfaceFilter>();
    List<ContainerWarehouseInterface> viewers = new ArrayList<ContainerWarehouseInterface>();

    public TileWarehouseInterface() {

    }

    public void addViewer(ContainerWarehouseInterface viewer) {
        if (!hasWorldObj() || worldObj.isRemote) {
            return;
        }
        viewers.add(viewer);
    }

    public void removeViewer(ContainerWarehouseInterface viewer) {
        viewers.remove(viewer);
    }

    public void updateViewers() {
        for (ContainerWarehouseInterface v : viewers) {
            v.onInterfaceFiltersChanged();
        }
    }

    public List<WarehouseInterfaceFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<WarehouseInterfaceFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
        recalcRequests();
        updateViewers();
        markDirty();
    }

    @Override
    protected void updateTile() {
        if (worldObj.isRemote) {
            return;
        }
        if (!init) {
            init = true;
            recalcRequests();
        }
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        ItemStack stack = inventory.decrStackSize(var1, var2);
        if(stack!=null)
            markDirty();
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        ItemStack stack = inventory.getStackInSlotOnClosing(var1);
        recalcRequests();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
        recalcRequests();
        markDirty();
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return inventory.isUseableByPlayer(var1);
    }

    @Override
    public void openInventory() {
    }//noop

    @Override
    public void closeInventory() {
    }//noop

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_OUTPUT, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
        filters = INBTSerialable.Helper.read(tag, "filterList", WarehouseInterfaceFilter.class);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
        INBTSerialable.Helper.write(tag, "filterList", getFilters());
    }

    public void recalcRequests() {
        if (worldObj.isRemote) {
            return;
        }
        fillRequests.clear();
        emptyRequests.clear();
        ItemStack stack;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            stack = inventory.getStackInSlot(i);
            if (stack == null) {
                continue;
            }
            if (!matchesFilter(stack)) {
                emptyRequests.add(new InterfaceEmptyRequest(i, stack.stackSize));
            } else//matches, remove extras
            {
                int count = InventoryTools.getCountOf(inventory, -1, stack);
                int max = getFilterQuantity(stack);
                if (count > max) {
                    emptyRequests.add(new InterfaceEmptyRequest(i, count - max));
                }
            }
        }

        int count;
        for (WarehouseInterfaceFilter filter : filters) {
            if (filter.getFilterItem() == null) {
                continue;
            }
            count = InventoryTools.getCountOf(inventory, -1, filter.getFilterItem());
            if (count < filter.getFilterQuantity()) {
                fillRequests.add(new InterfaceFillRequest(filter.getFilterItem().copy(), filter.getFilterQuantity() - count));
            }
        }
        TileWarehouseBase twb = (TileWarehouseBase) getController();
        if (twb != null) {
            twb.onIterfaceInventoryChanged(this);
        }
    }

    protected boolean matchesFilter(ItemStack stack) {
        if (filters.isEmpty()) {
            return false;
        }
        for (WarehouseInterfaceFilter filter : filters) {
            if (filter.apply(stack)) {
                return true;
            }
        }
        return false;
    }

    protected int getFilterQuantity(ItemStack stack) {
        int qty = 0;
        for (WarehouseInterfaceFilter filter : filters) {
            if (filter.apply(stack)) {
                qty += filter.getFilterQuantity();
            }
        }
        return qty;
    }

    public List<InterfaceFillRequest> getFillRequests() {
        return fillRequests;
    }

    public List<InterfaceEmptyRequest> getEmptyRequests() {
        return emptyRequests;
    }

    public static class InterfaceFillRequest {
        final ItemStack requestedItem;
        final int requestAmount;

        public InterfaceFillRequest(ItemStack item, int amount) {
            requestedItem = item;
            requestAmount = amount;
        }
    }

    public static class InterfaceEmptyRequest {
        final int slotNum;
        final int count;

        public InterfaceEmptyRequest(int slot, int count) {
            slotNum = slot;
            this.count = count;
        }
    }

}
