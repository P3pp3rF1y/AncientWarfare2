package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.ArrayList;
import java.util.List;

public class ContainerTownHall extends ContainerTileBase<TileTownHall> {

    List<NpcDeathEntry> deathList = new ArrayList<NpcDeathEntry>();

    public ContainerTownHall(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        int xPos, yPos;
        for (int i = 0; i < tileEntity.getSizeInventory(); i++) {
            xPos = (i % 9) * 18 + 8;
            yPos = (i / 9) * 18 + 8 + 16;
            addSlotToContainer(new Slot(tileEntity, i, xPos, yPos));
        }
        addPlayerSlots(8, 8 + 3 * 18 + 8 + 16, 4);
        if (!player.worldObj.isRemote) {
            deathList.addAll(tileEntity.getDeathList());
            tileEntity.addViewer(this);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("deathList")) {
            deathList.clear();
            NBTTagList list = tag.getTagList("deathList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                deathList.add(new NpcDeathEntry(list.getCompoundTagAt(i)));
            }
            refreshGui();
        }
        if (tag.hasKey("clear")) {
            tileEntity.clearDeathNotices();
        }
    }

    @Override
    public void sendInitData() {
        sendDeathListToClient();
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        tileEntity.removeViewer(this);
    }

    public void onTownHallDeathListUpdated() {
        this.deathList.clear();
        this.deathList.addAll(tileEntity.getDeathList());
        sendDeathListToClient();
    }

    private void sendDeathListToClient() {
        NBTTagList list = new NBTTagList();
        for (NpcDeathEntry entry : deathList) {
            list.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("deathList", list);
        sendDataToClient(tag);
    }

    public List<NpcDeathEntry> getDeathList() {
        return deathList;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            if (slotClickedIndex < tileEntity.getSizeInventory())//book slot
            {
                if (!this.mergeItemStack(slotStack, tileEntity.getSizeInventory(), tileEntity.getSizeInventory() + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 0, tileEntity.getSizeInventory(), false))//merge into player inventory
                {
                    return null;
                }
            }
            if (slotStack.stackSize == 0) {
                theSlot.putStack(null);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.stackSize == slotStackCopy.stackSize) {
                return null;
            }
            theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
        }
        return slotStackCopy;
    }

}
