package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBoundedInventory;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

import java.util.HashMap;

public class ContainerWorksiteInventorySideSelection extends ContainerTileBase<TileWorksiteBoundedInventory> {
    public final HashMap<RelativeSide, RelativeSide> sideMap = new HashMap<>();
    private static final String MACHINE_SIDE_KEY = "machineSide";
    private static final String INVENTORY_SIDE_KEY = "inventorySide";
    private static final String ACCESS_CHANGE_KEY = "accessChange";

    public ContainerWorksiteInventorySideSelection(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

        for(RelativeSide rSide : tileEntity.getInventorySideMappings().keySet()) {
            sideMap.put(rSide, tileEntity.getInventorySideMappings().get(rSide));
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
            tileEntity.onBlockClicked(player, null);//hack to open the worksites GUI
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
        } else if(tag.hasKey(ACCESS_CHANGE_KEY)) {
            NBTTagCompound slotTag = tag.getCompoundTag(ACCESS_CHANGE_KEY);
            RelativeSide machineSide = RelativeSide.values()[slotTag.getInteger(MACHINE_SIDE_KEY)];
            RelativeSide inventorySide = RelativeSide.values()[slotTag.getInteger(INVENTORY_SIDE_KEY)];
            sideMap.put(machineSide, inventorySide);
            if (!player.world.isRemote) {
                tileEntity.setInventorySideMappings(machineSide, inventorySide);
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
        NBTTagCompound tag;
        NBTTagCompound slotTag;
        RelativeSide rSide2, rSide3;
        for(RelativeSide rSide : tileEntity.getInventorySideMappings().keySet()) {
            rSide2 = tileEntity.getInventorySideMappings().get(rSide);
            rSide3 = sideMap.get(rSide);
            if (rSide2 != rSide3) {
                sideMap.put(rSide, rSide2);

                tag = new NBTTagCompound();
                slotTag = new NBTTagCompound();
                slotTag.setInteger(MACHINE_SIDE_KEY, rSide.ordinal());
                slotTag.setInteger(INVENTORY_SIDE_KEY, rSide2.ordinal());
                tag.setTag(ACCESS_CHANGE_KEY, slotTag);
                sendDataToClient(tag);
            }
        }
    }

    public void sendSlotChange(RelativeSide base, RelativeSide access) {
        NBTTagCompound tag;
        NBTTagCompound slotTag;
        tag = new NBTTagCompound();
        slotTag = new NBTTagCompound();
        slotTag.setInteger(MACHINE_SIDE_KEY, base.ordinal());
        slotTag.setInteger(INVENTORY_SIDE_KEY, access.ordinal());
        tag.setTag(ACCESS_CHANGE_KEY, slotTag);
        sendDataToServer(tag);
    }

    public void close() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("closeGUI", true);
        sendDataToServer(tag);
    }
}
