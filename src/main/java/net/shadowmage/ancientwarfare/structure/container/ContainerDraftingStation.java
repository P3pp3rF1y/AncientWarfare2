package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

import javax.annotation.Nonnull;

public class ContainerDraftingStation extends ContainerStructureSelectionBase {

    public boolean isStarted = false;
    private boolean isFinished = false;
    private int remainingTime;
    private int totalTime;
    public final NonNullList<ItemStack> neededResources = NonNullList.create();

    private final TileDraftingStation tile;

    public ContainerDraftingStation(EntityPlayer player, int x, int y, int z) {
        super(player);
        tile = (TileDraftingStation) player.world.getTileEntity(new BlockPos(x, y, z));
        if (tile == null) {
            throw new IllegalArgumentException("No drafting station");
        }
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
                addSlotToContainer(new SlotItemHandler(tile.inputSlots, slotNum, xp, yp));
            }
        }

        addSlotToContainer(new SlotItemHandler(tile.outputSlot, 0, 8 + 4 * 18, 94 - 16 - 18) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        });

        this.addPlayerSlots(156);
    }

    @Override
    public boolean canInteractWith(EntityPlayer var1){
        return tile.getDistanceSq(var1.posX, var1.posY, var1.posZ) <= 64D;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        @Nonnull ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot theSlot = this.getSlot(slotClickedIndex);
        if (theSlot != null && theSlot.getHasStack()) {
            @Nonnull ItemStack slotStack = theSlot.getStack();
            slotStackCopy = slotStack.copy();

            int playerSlotStart = tile.inputSlots.getSlots();
            int playerSlotEnd = playerSlotStart + playerSlots;
            if (slotClickedIndex < playerSlotStart)//storage slots
            {
                if (!this.mergeItemStack(slotStack, playerSlotStart, playerSlotEnd, false))//merge into player inventory
                {
                    return ItemStack.EMPTY;
                }
            } else if (slotClickedIndex < playerSlotEnd)//player slots, merge into storage
            {
                if (!this.mergeItemStack(slotStack, 0, playerSlotStart, false))//merge into storage
                {
                    return ItemStack.EMPTY;
                }
            }
            if (slotStack.getCount() == 0) {
                theSlot.putStack(ItemStack.EMPTY);
            } else {
                theSlot.onSlotChanged();
            }
            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }
            theSlot.onTake(par1EntityPlayer, slotStack);
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

    private NBTTagList getResourceListTag(NonNullList<ItemStack> resources) {
        NBTTagList list = new NBTTagList();
        NBTTagCompound tag;
        for (ItemStack item : resources) {
            tag = new NBTTagCompound();
            item.writeToNBT(tag);
            list.appendTag(tag);
        }
        return list;
    }

    private void readResourceList(NBTTagList list, NonNullList<ItemStack> resources) {
        NBTTagCompound tag;
        @Nonnull ItemStack stack;
        for (int i = 0; i < list.tagCount(); i++) {
            tag = list.getCompoundTagAt(i);
            stack = new ItemStack(tag);
            if (!stack.isEmpty()) {
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
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("structName")) {
            if (player.world.isRemote) {
                this.structureName = tag.getString("structName");
            } else {
                tile.setTemplate(tag.getString("structName"));
            }
        }
        else if (tag.hasKey("clearName")) {
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
        else if (tag.hasKey("start")) {
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
        } else if (structureName != null && !structureName.equals(tileName)) {
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
