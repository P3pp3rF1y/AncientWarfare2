package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseInterfaceFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;

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
        addPlayerSlots(8 + 8 + 3 * 18 + 80 + 8);
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        tileEntity.removeViewer(this);
        super.onContainerClosed(par1EntityPlayer);
    }

    @Override
    public void sendInitData() {
        sendDataToClient(getTagToSend());
    }

    public void sendFiltersToServer()//should be called whenever filters change, so that the base container/etc can be updated
    {
        sendDataToServer(getTagToSend());
    }

    public NBTTagCompound getTagToSend(){
        NBTTagCompound tag = new NBTTagCompound();
        INBTSerialable.Helper.write(tag, "filterList", filters);
        return tag;
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("filterList")) {
            List<WarehouseInterfaceFilter> filters = INBTSerialable.Helper.read(tag, "filterList", WarehouseInterfaceFilter.supply());
            if (player.worldObj.isRemote) {
                this.filters = filters;
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
