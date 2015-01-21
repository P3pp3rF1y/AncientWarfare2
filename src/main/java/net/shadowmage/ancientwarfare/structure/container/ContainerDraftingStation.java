package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

import java.util.ArrayList;
import java.util.List;

public class ContainerDraftingStation extends ContainerStructureSelectionBase {

    public boolean isStarted = false;
    public boolean isFinished = false;
    public int remainingTime;
    public int totalTime;
    public List<ItemStack> neededResources = new ArrayList<ItemStack>();

    private TileDraftingStation tile;

    public ContainerDraftingStation(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        tile = (TileDraftingStation) player.worldObj.getTileEntity(x, y, z);

        structureName = tile.getCurrentTemplateName();
        neededResources.addAll(tile.getNeededResources());
        isStarted = tile.isStarted();
        isFinished = tile.isFinished();
        remainingTime = tile.getRemainingTime();
        totalTime = tile.getTotalTime();

        int y2 = 94;

        int xp;
        int yp;
        int slotNum;
        for (int y1 = 0; y1 < 3; y1++) {
            for (int x1 = 0; x1 < 9; x1++) {
                slotNum = y1 * 9 + x1;
                xp = 8 + x1 * 18;
                yp = y2 + y1 * 18;
                addSlotToContainer(new Slot(tile.inputSlots, slotNum, xp, yp));
            }
        }

        ItemSlotFilter filter = new ItemSlotFilter() {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }
        };

        addSlotToContainer(new SlotFiltered(tile.outputSlot, 0, 8 + 4 * 18, 94 - 16 - 18, filter));

        this.addPlayerSlots(player, 8, 156, 4);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        ItemStack slotStackCopy = null;
        Slot theSlot = (Slot) this.inventorySlots.get(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();

            int storageSlotsStart = 0;
            int storageSlotLen = 27;
            int playerSlotStart = storageSlotLen;
            if (slotClickedIndex >= storageSlotsStart && slotClickedIndex < storageSlotsStart + storageSlotLen)//storage slots
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart + 36, false))//merge into player inventory
                {
                    return null;
                }
            } else if (slotClickedIndex >= playerSlotStart && slotClickedIndex < 36 + playerSlotStart)//player slots, merge into storage
            {
                if (!this.mergeItemStack(slotStack, storageSlotsStart, storageSlotsStart + 9, false))//merge into storage
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

    @Override
    public void sendInitData() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("isStarted", isStarted);
        tag.setBoolean("isFinished", isFinished);
        tag.setInteger("remainingTime", remainingTime);
        tag.setInteger("totalTime", totalTime);
        if (structureName != null) {
            tag.setString("structName", structureName);
        }
        tag.setTag("resourceList", getResourceListTag(neededResources));
        this.sendDataToClient(tag);
    }

    private NBTTagList getResourceListTag(List<ItemStack> resources) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag;
        for (ItemStack item : resources) {
            tag = new NBTTagCompound();
            item.writeToNBT(tag);
            list.appendTag(tag);
        }
        return list;
    }

    private void readResourceList(NBTTagList list, List<ItemStack> resources) {
        NBTTagCompound tag;
        ItemStack stack;
        for (int i = 0; i < list.tagCount(); i++) {
            tag = list.getCompoundTagAt(i);
            stack = ItemStack.loadItemStackFromNBT(tag);
            if (stack != null) {
                resources.add(stack);
            }
        }
    }

    public void handleStopInput() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("stop", true);
        this.sendDataToServer(tag);
    }

    public void handleStartInput() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("start", true);
        this.sendDataToServer(tag);
    }

    @Override
    public void handleNameSelection(String name) {
        super.handleNameSelection(name);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("structName")) {
            if (player.worldObj.isRemote) {
                this.structureName = tag.getString("structName");
            } else {
                tile.setTemplate(tag.getString("structName"));
            }
        }
        if (tag.hasKey("clearName")) {
            structureName = null;
        }
        if (tag.hasKey("isStarted")) {
            isStarted = tag.getBoolean("isStarted");
        }
        if (tag.hasKey("isFinished")) {
            isFinished = tag.getBoolean("isFinished");
        }
        if (tag.hasKey("remainingTime")) {
            remainingTime = tag.getInteger("remainingTime");
        }
        if (tag.hasKey("totalTime")) {
            totalTime = tag.getInteger("totalTime");
        }
        if (tag.hasKey("resourceList")) {
            neededResources.clear();
            readResourceList(tag.getTagList("resourceList", Constants.NBT.TAG_COMPOUND), neededResources);
        }
        if (tag.hasKey("stop")) {
            tile.stopCurrentWork();
        }
        if (tag.hasKey("start")) {
            tile.tryStart();
        }
        refreshGui();
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        String tileName = tile.getCurrentTemplateName();
        NBTTagCompound tag = null;
        if ((structureName == null && tileName != null) || (tileName == null && structureName != null)) {
            tag = new NBTTagCompound();
            this.structureName = tileName;
            if (this.structureName == null) {
                tag.setBoolean("clearName", true);
            } else {
                tag.setString("structName", structureName);
            }
        } else if (structureName != null && tileName != null && !structureName.equals(tileName)) {
            structureName = tileName;
            tag = new NBTTagCompound();
            tag.setString("structName", structureName);
        }
        if (tile.isFinished() != isFinished) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            isFinished = tile.isFinished();
            tag.setBoolean("isFinished", isFinished);
        }
        if (tile.isStarted() != isStarted) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            isStarted = tile.isStarted();
            tag.setBoolean("isStarted", isStarted);
        }
        if (tile.getRemainingTime() != remainingTime) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            remainingTime = tile.getRemainingTime();
            tag.setInteger("remainingTime", remainingTime);
        }
        if (tile.getTotalTime() != totalTime) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            totalTime = tile.getTotalTime();
            tag.setInteger("totalTime", totalTime);
        }
        if (!neededResources.equals(tile.getNeededResources())) {
            if (tag == null) {
                tag = new NBTTagCompound();
            }
            neededResources.clear();
            neededResources.addAll(tile.getNeededResources());
            NBTTagList list = getResourceListTag(neededResources);
            tag.setTag("resourceList", list);
        }
        if (tag != null) {
            sendDataToClient(tag);
        }
    }

}
