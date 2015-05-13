package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventorySlotlessBasic;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileWarehouseStorage extends TileControlled implements IWarehouseStorageTile, IInteractableTile {

    InventorySlotlessBasic inventory;
    List<WarehouseStorageFilter> filters = new ArrayList<WarehouseStorageFilter>();

    Set<ContainerWarehouseStorage> viewers = new HashSet<ContainerWarehouseStorage>();

    public TileWarehouseStorage() {
        inventory = new InventorySlotlessBasic(9 * 64);
    }

    @Override
    public ItemStack tryAdd(ItemStack cursorStack) {
        int moved = insertItem(cursorStack, cursorStack.stackSize);
        cursorStack.stackSize -= moved;
        TileWarehouseBase twb = (TileWarehouseBase) getController();
        if (twb != null) {
            twb.changeCachedQuantity(cursorStack, moved);
        }
        if (cursorStack.stackSize <= 0) {
            return null;
        }
        return cursorStack;
    }

    @Override
    protected void updateTile() {

    }

    public void onTileBroken() {
        ItemQuantityMap qtm = new ItemQuantityMap();
        addItems(qtm);
        if (getController() != null) {
            getController().removeControlledTile(this);
        }
        List<ItemStack> list = qtm.getItems();
        for (ItemStack stack : list) {
            InventoryTools.dropItemInWorld(worldObj, stack, xCoord, yCoord, zCoord);
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
                    twb.addStorageTile(this);
                    break;
                }
            }
        }
    }

    @Override
    protected boolean isValidController(IControllerTile tile) {
        return tile instanceof TileWarehouseBase;//TODO validate inside of bounding area
    }

    @Override
    public int getStorageAdditionSize() {
        return 9 * 64;
    }

    @Override
    public void onWarehouseInventoryUpdated(TileWarehouseBase warehouse) {

    }

    @Override
    public List<WarehouseStorageFilter> getFilters() {
        return filters;
    }

    @Override
    public void setFilters(List<WarehouseStorageFilter> filters) {
        List<WarehouseStorageFilter> old = new ArrayList<WarehouseStorageFilter>();
        old.addAll(this.filters);
        this.filters.clear();
        this.filters.addAll(filters);
        if (this.getController() != null) {
            ((TileWarehouseBase) this.getController()).onStorageFilterChanged(this, old, this.filters);
        }
        updateViewers();
    }

    @Override
    public void addItems(ItemQuantityMap map) {
        inventory.getItems(map);
    }

    @Override
    public int getQuantityStored(ItemStack filter) {
        return inventory.getQuantityStored(filter);
    }

    @Override
    public int getAvailableSpaceFor(ItemStack filter) {
        return inventory.getAvailableSpaceFor(filter);
    }

    @Override
    public int extractItem(ItemStack filter, int amount) {
        int removed = inventory.extractItem(filter, amount);
        updateViewersForInventory();
        return removed;
    }

    @Override
    public int insertItem(ItemStack filter, int amount) {
        int inserted = inventory.insertItem(filter, amount);
        updateViewersForInventory();
        return inserted;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
        WarehouseStorageFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), filters);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
        tag.setTag("filterList", WarehouseStorageFilter.writeFilterList(filters));
    }

    @Override
    public void addViewer(ContainerWarehouseStorage containerWarehouseStorage) {
        if (worldObj == null || worldObj.isRemote) {
            return;
        }
        viewers.add(containerWarehouseStorage);
    }

    @Override
    public void removeViewer(ContainerWarehouseStorage containerWarehouseStorage) {
        viewers.remove(containerWarehouseStorage);
    }

    protected void updateViewers() {
        for (ContainerWarehouseStorage viewer : viewers) {
            viewer.onFilterListUpdated();
        }
    }

    protected void updateViewersForInventory() {
        for (ContainerWarehouseStorage viewer : viewers) {
            viewer.onStorageInventoryUpdated();
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    public void handleSlotClick(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        if (filter != null && player.inventory.getItemStack() == null) {
            tryGetItem(player, filter, shiftClick);
        } else if (player.inventory.getItemStack() != null) {
            tryAddItem(player, player.inventory.getItemStack());
        }
    }

    private void tryAddItem(EntityPlayer player, ItemStack cursorStack) {
        int stackSize = cursorStack.stackSize;
        int moved;
        moved = insertItem(cursorStack, cursorStack.stackSize);
        cursorStack.stackSize -= moved;
        TileWarehouseBase twb = (TileWarehouseBase) getController();
        if (twb != null) {
            twb.changeCachedQuantity(cursorStack, moved);
        }
        if (cursorStack.stackSize <= 0) {
            player.inventory.setItemStack(null);
        }
        if (stackSize != cursorStack.stackSize) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            playerMP.updateHeldItem();
        }
    }

    private void tryGetItem(EntityPlayer player, ItemStack filter, boolean shiftClick) {
        ItemStack newCursorStack = filter.copy();
        newCursorStack.stackSize = 0;
        int count;
        int toMove;
        count = getQuantityStored(filter);
        toMove = newCursorStack.getMaxStackSize() - newCursorStack.stackSize;
        toMove = toMove > count ? count : toMove;
        if (toMove > 0) {
            newCursorStack.stackSize += toMove;
            extractItem(filter, toMove);
            TileWarehouseBase twb = (TileWarehouseBase) getController();
            if (twb != null) {
                twb.changeCachedQuantity(filter, -toMove);
            }
        }
        if (newCursorStack.stackSize > 0) {
            if (shiftClick) {
                newCursorStack = InventoryTools.mergeItemStack(player.inventory, newCursorStack, -1);
            }
            if (newCursorStack != null) {
                player.inventory.setItemStack(newCursorStack);
                EntityPlayerMP playerMP = (EntityPlayerMP) player;
                playerMP.updateHeldItem();
            }
        }
    }

}
