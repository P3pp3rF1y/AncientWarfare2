package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseInterface;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TileWarehouseInterface extends TileControlled implements IInventory, IInteractableTile {

    InventoryBasic inventory = new InventoryBasic(27);

    boolean init = false;
    List<InterfaceFillRequest> fillRequests = new ArrayList<InterfaceFillRequest>();
    List<InterfaceEmptyRequest> emptyRequests = new ArrayList<InterfaceEmptyRequest>();
    List<WarehouseInterfaceFilter> filters = new ArrayList<WarehouseInterfaceFilter>();
    HashSet<ContainerWarehouseInterface> viewers = new HashSet<ContainerWarehouseInterface>();

    public TileWarehouseInterface() {

    }

    public void addViewer(ContainerWarehouseInterface viewer) {
        if (worldObj.isRemote) {
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
    protected void searchForController() {
        BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
        BlockPosition min = pos.copy();
        BlockPosition max = min.copy();
        min.offset(-16, -4, -16);
        max.offset(16, 4, 16);
        for (TileEntity te : WorldTools.getTileEntitiesInArea(worldObj, min.x, min.y, min.z, max.x, max.y, max.z)) {
            if (te instanceof TileWarehouseBase) {
                TileWarehouseBase twb = (TileWarehouseBase) te;
                if (BlockTools.isPositionWithinBounds(pos, twb.getWorkBoundsMin(), twb.getWorkBoundsMax())) {
                    twb.addInterfaceTile(this);
                    break;
                }
            }
        }
    }

    @Override
    protected boolean isValidController(IControllerTile tile) {
        return tile instanceof TileWarehouseBase;//TODO validate bounding area
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
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_OUTPUT, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
        filters = WarehouseInterfaceFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseInterfaceFilter>());
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
        tag.setTag("filterList", WarehouseInterfaceFilter.writeFilterList(getFilters()));
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
            if (filter.isItemValid(stack)) {
                return true;
            }
        }
        return false;
    }

    protected int getFilterQuantity(ItemStack stack) {
        int qty = 0;
        for (WarehouseInterfaceFilter filter : filters) {
            if (filter.isItemValid(stack)) {
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
        ItemStack requestedItem;
        int requestAmount;

        public InterfaceFillRequest(ItemStack item, int amount) {
            requestedItem = item;
            requestAmount = amount;
        }
    }

    public static class InterfaceEmptyRequest {
        int slotNum;
        int count;

        public InterfaceEmptyRequest(int slot, int count) {
            slotNum = slot;
            this.count = count;
        }
    }

}
