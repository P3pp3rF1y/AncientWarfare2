package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockViewer;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TileWarehouseStockViewer extends TileControlled implements IOwnable, IInteractableTile {

    List<WarehouseStockFilter> filters = new ArrayList<WarehouseStockFilter>();
    String ownerName = "";
    private boolean shouldUpdate = false;

    private Set<ContainerWarehouseStockViewer> viewers = new HashSet<ContainerWarehouseStockViewer>();

    public TileWarehouseStockViewer() {
    }

    public void updateViewers() {
        for (ContainerWarehouseStockViewer viewer : viewers) {
            viewer.onFiltersChanged();
        }
    }

    public void addViewer(ContainerWarehouseStockViewer viewer) {
        viewers.add(viewer);
    }

    public void removeViewer(ContainerWarehouseStockViewer viewer) {
        viewers.add(viewer);
    }

    public List<WarehouseStockFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<WarehouseStockFilter> filters) {
        this.filters.clear();
        this.filters.addAll(filters);
        shouldUpdate = false;//set to false, as we are manually updating right now
        recountFilters(false);//recount filters, do not send update
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);//to re-send description packet to client with new filters
    }

    /**
     * should be called whenever controller tile is set or warehouse inventory updated
     */
    private void recountFilters(boolean sendToClients) {
        TileWarehouseBase twb = (TileWarehouseBase) getController();
        int count;
        int index = 0;
        if (twb == null) {
            count = 0;
            for (WarehouseStockFilter filter : this.filters) {
                if (count != filter.quantity) {
                    filter.quantity = 0;
                    if (sendToClients) {
                        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), index, count);
                    }
                }
                index++;
            }
        } else {
            for (WarehouseStockFilter filter : this.filters) {
                count = filter.item == null ? 0 : twb.getCountOf(filter.getFilterItem());
                if (count != filter.quantity) {
                    filter.quantity = count;
                    if (sendToClients) {
                        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), index, count);
                    }
                }
                index++;
            }
        }
    }

    @Override
    public void setOwnerName(String name) {
        name = name == null ? "" : name;
        ownerName = name;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STOCK, xCoord, yCoord, zCoord);
        }
        return true;
    }

    @Override
    protected void updateTile() {
        if (shouldUpdate) {
            shouldUpdate = false;
            recountFilters(true);
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
                    twb.addStockViewer(this);
                    break;
                }
            }
        }
    }

    /**
     * should be called on SERVER whenever warehouse inventory changes
     */
    public void onWarehouseInventoryUpdated() {
        shouldUpdate = true;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", WarehouseStockFilter.getTagList(filters));
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.filters.clear();
        this.filters.addAll(WarehouseStockFilter.readFilterList(pkt.func_148857_g().getTagList("filterList", Constants.NBT.TAG_COMPOUND)));
        updateViewers();
    }

    @Override
    protected boolean isValidController(IControllerTile tile) {
        return tile instanceof TileWarehouseBase;
    }

    @Override
    public boolean receiveClientEvent(int a, int b) {
        if (worldObj.isRemote) {
            if (a >= 0 && a < filters.size()) {
                filters.get(a).quantity = b;
                updateViewers();
            }
        }
        return true;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        filters.addAll(WarehouseStockFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND)));
        ownerName = tag.getString("ownerName");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("filterList", WarehouseStockFilter.getTagList(filters));
        tag.setString("ownerName", ownerName);
    }

    public static class WarehouseStockFilter {
        ItemStack item;
        ItemHashEntry hashKey;
        int quantity;

        public WarehouseStockFilter() {
        }

        public WarehouseStockFilter(ItemStack item, int qty) {
            this.item = item;
            this.quantity = qty;
            this.hashKey = item == null ? null : new ItemHashEntry(item);
        }

        public void setItem(ItemStack item) {
            this.item = item;
            this.hashKey = item == null ? null : new ItemHashEntry(item);
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public ItemStack getFilterItem() {
            return item;
        }

        public int getQuantity() {
            return quantity;
        }

        public void readFromNBT(NBTTagCompound tag) {
            item = tag.hasKey("item") ? InventoryTools.readItemStack(tag.getCompoundTag("item")) : null;
            hashKey = item == null ? null : new ItemHashEntry(item);
            quantity = tag.getInteger("quantity");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            if (item != null) {
                tag.setTag("item", InventoryTools.writeItemStack(item, new NBTTagCompound()));
            }
            tag.setInteger("quantity", quantity);
            return tag;
        }

        public static NBTTagList getTagList(List<WarehouseStockFilter> filters) {
            NBTTagList list = new NBTTagList();
            for (WarehouseStockFilter filter : filters) {
                list.appendTag(filter.writeToNBT(new NBTTagCompound()));
            }
            return list;
        }

        public static List<WarehouseStockFilter> readFilterList(NBTTagList list) {
            ArrayList<WarehouseStockFilter> filters = new ArrayList<WarehouseStockFilter>();
            WarehouseStockFilter filter;
            for (int i = 0; i < list.tagCount(); i++) {
                filter = new WarehouseStockFilter();
                filter.readFromNBT(list.getCompoundTagAt(i));
                filters.add(filter);
            }
            return filters;
        }
    }
}
