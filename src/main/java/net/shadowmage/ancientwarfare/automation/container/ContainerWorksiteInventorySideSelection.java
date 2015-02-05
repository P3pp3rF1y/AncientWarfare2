package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

import java.util.HashMap;

public class ContainerWorksiteInventorySideSelection extends ContainerTileBase<TileWorksiteBoundedInventory> {

    public HashMap<RelativeSide, RelativeSide> sideMap = new HashMap<RelativeSide, RelativeSide>();
    public InventorySided inventory;

    public ContainerWorksiteInventorySideSelection(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        inventory = tileEntity.inventory;

        for (RelativeSide rSide : inventory.rType.getValidSides()) {
            sideMap.put(rSide, inventory.getRemappedSide(rSide));
        }
    }

    @Override
    public void sendInitData() {
        sendAccessMap();
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        handleAccessMapTag(tag);
        if (tag.hasKey("closeGUI")) {
            tileEntity.onBlockClicked(player);//hack to open the worksites GUI
        }
        refreshGui();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        synchAccessMap();
    }

    private void handleAccessMapTag(NBTTagCompound tag) {
        if (tag.hasKey("accessMap")) {
            NBTTagCompound accessTag = tag.getCompoundTag("accessMap");
            int[] rMap = accessTag.getIntArray("rMap");
            int[] rMap2 = accessTag.getIntArray("iMap");
            RelativeSide rSide;
            RelativeSide iSide;
            for (int i = 0; i < rMap.length && i < rMap2.length; i++) {
                rSide = RelativeSide.values()[rMap[i]];
                iSide = RelativeSide.values()[rMap2[i]];
                sideMap.put(rSide, iSide);
            }
        }
        if (tag.hasKey("accessChange")) {
            NBTTagCompound slotTag = tag.getCompoundTag("accessChange");
            RelativeSide base = RelativeSide.values()[slotTag.getInteger("baseSide")];
            RelativeSide access = RelativeSide.values()[slotTag.getInteger("accessSide")];
            sideMap.put(base, access);
            if (!player.worldObj.isRemote) {
                tileEntity.inventory.remapSideAccess(base, access);
            }
        }
    }

    private void sendAccessMap() {
        int l = sideMap.size();
        int rMap[] = new int[l];
        int iMap[] = new int[l];
        int index = 0;
        for (RelativeSide rSide : sideMap.keySet()) {
            rMap[index] = rSide.ordinal();
            iMap[index] = sideMap.get(rSide).ordinal();
            index++;
        }
        NBTTagCompound accessTag = new NBTTagCompound();
        accessTag.setIntArray("rMap", rMap);
        accessTag.setIntArray("iMap", iMap);
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("accessMap", accessTag);
        sendDataToClient(tag);
    }

    private void synchAccessMap() {
        InventorySided inventory = tileEntity.inventory;
        NBTTagCompound tag;
        NBTTagCompound slotTag;
        RelativeSide rSide2, rSide3;
        for (RelativeSide rSide : inventory.rType.getValidSides()) {
            rSide2 = inventory.getRemappedSide(rSide);
            rSide3 = sideMap.get(rSide);
            if (rSide2 != rSide3) {
                sideMap.put(rSide, rSide2);

                tag = new NBTTagCompound();
                slotTag = new NBTTagCompound();
                slotTag.setInteger("baseSide", rSide.ordinal());
                slotTag.setInteger("accessSide", rSide2.ordinal());
                tag.setTag("accessChange", slotTag);
                sendDataToClient(tag);
            }
        }
    }

    public void sendSlotChange(RelativeSide base, RelativeSide access) {
        NBTTagCompound tag;
        NBTTagCompound slotTag;
        tag = new NBTTagCompound();
        slotTag = new NBTTagCompound();
        slotTag.setInteger("baseSide", base.ordinal());
        slotTag.setInteger("accessSide", access.ordinal());
        tag.setTag("accessChange", slotTag);
        sendDataToServer(tag);
    }

}
