package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.NBTSerializableUtils;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
        if (!hasWorld() || world.isRemote) {
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
        if (world.isRemote) {
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
    public boolean isEmpty() {
        return inventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        ItemStack stack = inventory.decrStackSize(var1, var2);
        if(!stack.isEmpty())
            markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        ItemStack stack = inventory.removeStackFromSlot(var1);
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
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return inventory.isUsableByPlayer(var1);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }//noop

    @Override
    public void closeInventory(EntityPlayer player) {
    }//noop

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_OUTPUT, pos);
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
        filters = NBTSerializableUtils.read(tag, "filterList", WarehouseInterfaceFilter.class);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
        NBTSerializableUtils.write(tag, "filterList", getFilters());
        return tag;
    }

    public void recalcRequests() {
        if (world.isRemote) {
            return;
        }
        fillRequests.clear();
        emptyRequests.clear();
        ItemStack stack;
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!matchesFilter(stack)) {
                emptyRequests.add(new InterfaceEmptyRequest(i, stack.getCount()));
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
