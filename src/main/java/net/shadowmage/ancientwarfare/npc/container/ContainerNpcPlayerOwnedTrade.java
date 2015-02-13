package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemBackpack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcTrader;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

public class ContainerNpcPlayerOwnedTrade extends ContainerNpcBase<NpcTrader> {

    public POTradeList tradeList;
    public final IInventory tradeInput = new InventoryBasic(9);
    public final InventoryBackpack storage;

    public ContainerNpcPlayerOwnedTrade(EntityPlayer player, int x, int y, int z) {
        super(player, x);
        this.tradeList = entity.getTradeList();
        this.entity.trader = player;

        int startY = 240 - 4 - 8 - 4 * 18;
        int gx = 0, gy = 0, sx, sy;
        for (int i = 0; i < 9; i++) {
            sx = gx * 18 + 8 + 9 * 18 + 18;
            sy = gy * 18 + startY + 16;
            addSlotToContainer(new Slot(tradeInput, i, sx, sy));
            gx++;
            if (gx >= 3) {
                gx = 0;
                gy++;
            }
            if (gy >= 3) {
                break;
            }
        }

        addPlayerSlots(8, startY, 4);

        storage = ItemBackpack.getInventoryFor(entity.getEquipmentInSlot(0));
        if(storage!=null){
            for (int i = 0; i < storage.getSizeInventory(); i++) {
                /**
                 * add backpack items to slots in container so that they are synchronized to client side inventory/container
                 * --will be used to validate trades on client-side
                 */
                addSlotToContainer(new Slot(storage, i, 100000, 100000));
            }
        } else {
            storage = null;
        }
    }

    @Override
    public void sendInitData() {
        if (tradeList != null) {
            NBTTagCompound tag = new NBTTagCompound();
            tradeList.writeToNBT(tag);

            NBTTagCompound packetTag = new NBTTagCompound();
            packetTag.setTag("tradeData", tag);
            sendDataToClient(packetTag);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("tradeData")) {
            tradeList = new POTradeList();
            tradeList.readFromNBT(tag.getCompoundTag("tradeData"));
        }
        if (tag.hasKey("doTrade")) {
            tradeList.performTrade(player, tradeInput, storage, tag.getInteger("doTrade"));
        }
        refreshGui();
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        this.entity.trader = null;
        if (storage != null) {
            ItemBackpack.writeBackpackToItem(storage, this.entity.getEquipmentInSlot(0));
        }
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote) {
            InventoryTools.dropInventoryInWorld(player.worldObj, tradeInput, player.posX, player.posY, player.posZ);
        }
    }
}
