package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseInterfaceFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

import java.util.ArrayList;
import java.util.List;

public class ContainerWarehouseInterface extends ContainerTileBase<TileWarehouseInterface> {

    public List<WarehouseInterfaceFilter> filters = new ArrayList<WarehouseInterfaceFilter>();

    public ContainerWarehouseInterface(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(tileEntity, i, (i % 3) * 18 + 8 + 3 * 18, (i / 3) * 18 + 8 + 80 + 8));
        }
        tileEntity.addViewer(this);
        filters.addAll(tileEntity.getFilters());
        addPlayerSlots(8, 8 + 8 + 3 * 18 + 80 + 8, 4);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        tileEntity.removeViewer(this);
        super.onContainerClosed(par1EntityPlayer);
    }

    @Override
    public void sendInitData() {
        NBTTagList filterTagList = WarehouseInterfaceFilter.writeFilterList(filters);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", filterTagList);
        sendDataToClient(tag);
    }

    public void sendFiltersToServer()//should be called whenever filters change, so that the base container/etc can be updated
    {
        NBTTagList filterTagList = WarehouseInterfaceFilter.writeFilterList(filters);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("filterList", filterTagList);
        sendDataToServer(tag);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("filterList")) {
            List<WarehouseInterfaceFilter> filters = WarehouseInterfaceFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseInterfaceFilter>());
            if (player.worldObj.isRemote) {
                this.filters.clear();
                this.filters.addAll(filters);
                refreshGui();
            } else {
                tileEntity.setFilters(filters);
            }
        }
    }

    public void onInterfaceFiltersChanged() {
        filters.clear();
        filters.addAll(tileEntity.getFilters());
        sendInitData();
    }

}
