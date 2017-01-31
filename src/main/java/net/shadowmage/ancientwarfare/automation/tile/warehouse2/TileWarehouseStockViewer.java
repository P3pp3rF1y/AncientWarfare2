package net.shadowmage.ancientwarfare.automation.tile.warehouse2;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStockViewer;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.*;

public class TileWarehouseStockViewer extends TileControlled implements IOwnable, IInteractableTile {

    private final List<WarehouseStockFilter> filters = new ArrayList<WarehouseStockFilter>();
    private UUID owner;
    private String ownerName = "";
    private boolean shouldUpdate = false;

    private final Set<ContainerWarehouseStockViewer> viewers = new HashSet<ContainerWarehouseStockViewer>();

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
        viewers.remove(viewer);
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
                if (count != filter.getQuantity()) {
                    filter.setQuantity(0);
                    if (sendToClients) {
                        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), index, count);
                    }
                }
                index++;
            }
        } else {
            for (WarehouseStockFilter filter : this.filters) {
                count = filter.getFilterItem() == null ? 0 : twb.getCountOf(filter.getFilterItem());
                if (count != filter.getQuantity()) {
                    filter.setQuantity(count);
                    if (sendToClients) {
                        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), index, count);
                    }
                }
                index++;
            }
        }
    }

    @Override
    public boolean isOwner(EntityPlayer player){
        if(player == null)
            return false;
        if(owner!=null)
            return player.getUniqueID().equals(owner);
        return player.getCommandSenderName().equals(ownerName);
    }

    @Override
    public void setOwner(EntityPlayer player) {
        this.owner = player.getUniqueID();
        this.ownerName = player.getCommandSenderName();
    }
    
    @Override
    public void setOwner(String ownerName, UUID ownerUuid) {
        this.ownerName = ownerName;
        this.owner = ownerUuid;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }
    
    @Override
    public UUID getOwnerUuid() {
        return owner;
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

    /**
     * should be called on SERVER whenever warehouse inventory changes
     */
    public void onWarehouseInventoryUpdated() {
        shouldUpdate = true;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        INBTSerialable.Helper.write(tag, "filterList", filters);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.filters.clear();
        this.filters.addAll(INBTSerialable.Helper.read(pkt.func_148857_g(), "filterList", WarehouseStockFilter.class));
        updateViewers();
    }

    @Override
    public boolean receiveClientEvent(int a, int b) {
        if (worldObj.isRemote) {
            if (a >= 0 && a < filters.size()) {
                filters.get(a).setQuantity(b);
                updateViewers();
            }
        }
        return true;
    }

    private void checkOwnerName(){
        if(hasWorldObj()){
            if(owner!=null) {
                EntityPlayer player = worldObj.func_152378_a(owner);
                if (player != null) {
                    setOwner(player);
                }
            }else if(ownerName!=null){
                EntityPlayer player = worldObj.getPlayerEntityByName(ownerName);
                if(player!=null){
                    setOwner(player);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        filters.addAll(INBTSerialable.Helper.read(tag, "filterList", WarehouseStockFilter.class));
        ownerName = tag.getString("ownerName");
        if(tag.hasKey("ownerId"))
            owner = UUID.fromString(tag.getString("ownerId"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        INBTSerialable.Helper.write(tag, "filterList", filters);
        checkOwnerName();
        tag.setString("ownerName", ownerName);
        if(owner!=null)
            tag.setString("ownerId", owner.toString());
    }

    public static class WarehouseStockFilter implements INBTSerialable{
        private ItemStack item;
        ItemHashEntry hashKey;
        private int quantity;

        public WarehouseStockFilter() {
        }

        public WarehouseStockFilter(ItemStack item, int qty) {
            setQuantity(qty);
            setItem(item);
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

        @Override
        public void readFromNBT(NBTTagCompound tag) {
            setItem(tag.hasKey("item") ? InventoryTools.readItemStack(tag.getCompoundTag("item")) : null);
            setQuantity(tag.getInteger("quantity"));
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            if (item != null) {
                tag.setTag("item", InventoryTools.writeItemStack(item));
            }
            tag.setInteger("quantity", quantity);
            return tag;
        }
    }
}
