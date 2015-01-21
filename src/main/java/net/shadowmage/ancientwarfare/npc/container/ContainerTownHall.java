package net.shadowmage.ancientwarfare.npc.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.ArrayList;
import java.util.List;

public class ContainerTownHall extends ContainerBase {

    public TileTownHall townHall;

    List<NpcDeathEntry> deathList = new ArrayList<NpcDeathEntry>();

    public ContainerTownHall(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (te instanceof TileTownHall) {
            townHall = (TileTownHall) te;
            IInventory inv = (IInventory) te;
            int xPos, yPos;
            for (int i = 0; i < inv.getSizeInventory(); i++) {
                xPos = (i % 9) * 18 + 8;
                yPos = (i / 9) * 18 + 8 + 16;
                addSlotToContainer(new Slot(inv, i, xPos, yPos));
            }
            addPlayerSlots(player, 8, 8 + 3 * 18 + 8 + 16, 4);
            if (!player.worldObj.isRemote) {
                deathList.addAll(townHall.getDeathList());
                townHall.addViewer(this);
            }
        } else {
            throw new IllegalArgumentException("cannot open town hall gui for tile: " + te);
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
            townHall.clearDeathNotices();
        }
    }

    @Override
    public void sendInitData() {
        sendDeathListToClient();
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        townHall.removeViewer(this);
    }

    public void onTownHallDeathListUpdated() {
        this.deathList.clear();
        this.deathList.addAll(townHall.getDeathList());
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
        Slot theSlot = (Slot) this.inventorySlots.get(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();
            if (slotClickedIndex < townHall.getSizeInventory())//book slot
            {
                if (!this.mergeItemStack(slotStack, townHall.getSizeInventory(), townHall.getSizeInventory() + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else {
                if (!this.mergeItemStack(slotStack, 0, townHall.getSizeInventory(), false))//merge into player inventory
                {
                    return null;
                }
            }
            if (slotStack.stackSize == 0) {
                theSlot.putStack((ItemStack) null);
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
