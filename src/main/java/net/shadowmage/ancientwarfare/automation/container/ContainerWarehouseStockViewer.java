package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

import java.util.ArrayList;
import java.util.List;

public class ContainerWarehouseStockViewer extends ContainerTileBase<TileWarehouseStockViewer> {

    public List<WarehouseStockFilter> filters = new ArrayList<WarehouseStockFilter>();

    public ContainerWarehouseStockViewer(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        filters.addAll(tileEntity.getFilters());
        tileEntity.addViewer(this);
        addPlayerSlots(8, 88, 4);//240-8-4-4*18
    }

    /**
     * should be called from the tile whenever its client-side filters change
     */
    public void onFiltersChanged() {
        refreshGui();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("filterList")) {
            List<WarehouseStockFilter> filters = WarehouseStockFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND));
            tileEntity.setFilters(filters);
        }
        super.handlePacketData(tag);
    }

    public void sendFiltersToServer() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", WarehouseStockFilter.getTagList(filters));
        sendDataToServer(tag);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        tileEntity.removeViewer(this);
        super.onContainerClosed(par1EntityPlayer);
    }

}
