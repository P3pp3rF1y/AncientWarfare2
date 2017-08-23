package net.shadowmage.ancientwarfare.automation.tile.worksite;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileAutoCrafting extends TileWorksiteBase implements ISidedInventory, IInventoryChangedListener {

    public InventoryBasic bookSlot;
    public InventoryBasic outputInventory;
    public InventoryBasic resourceInventory;
    public InventoryBasic outputSlot;//the templated output slot, non-pullable
    public InventoryCrafting craftMatrix;//the 3x3 recipe template/matrix

    private boolean canCraftLastCheck = false;
    private boolean canHoldLastCheck = false;

    private int[] outputSlotIndices;
    private int[] resourceSlotIndices;

    public TileAutoCrafting() {
        Container dummy = new Container() {
            @Override
            public boolean canInteractWith(EntityPlayer var1) {
                return true;
            }

            @Override
            public void onCraftMatrixChanged(IInventory par1iInventory) {
                onLayoutMatrixChanged();
                onInventoryChanged(null); //TODO pass something else than null here - just pass in the inventory in parameter?
            }
        };
        craftMatrix = new InventoryCrafting(dummy, 3, 3);
        resourceInventory = new InventoryBasic(18, this);
        outputInventory = new InventoryBasic(9, this);
        outputSlot = new InventoryBasic(1);
        bookSlot = new InventoryBasic(1, this);
        resourceSlotIndices = new int[resourceInventory.getSizeInventory()];
        for (int i = 0; i < resourceSlotIndices.length; i++) {
            resourceSlotIndices[i] = i;
        }
        outputSlotIndices = new int[outputInventory.getSizeInventory()];
        for (int i = 0; i < outputSlotIndices.length; i++) {
            outputSlotIndices[i] = i + resourceSlotIndices.length;
        }
    }

    private boolean canCraft() {
        if (outputSlot.getStackInSlot(0).isEmpty()) {
            return false;
        }//no output stack, don't even bother checking
        ArrayList<ItemStack> compactedCraft = new ArrayList<ItemStack>();
        ItemStack stack1, stack2;
        boolean found;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            stack1 = craftMatrix.getStackInSlot(i);
            if (stack1.isEmpty()) {
                continue;
            }
            found = false;
            for (ItemStack stack3 : compactedCraft) {
                if (InventoryTools.doItemStacksMatch(stack3, stack1)) {
                    stack3.grow(1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                stack2 = stack1.copy();
                stack2.setCount(1);
                compactedCraft.add(stack2);
            }
        }
        found = true;
        for (ItemStack stack3 : compactedCraft) {
            if (InventoryTools.getCountOf(resourceInventory, -1, stack3) < stack3.getCount()) {
                found = false;
                break;
            }
        }
        return found;
    }

    public String getCrafterName() {
        return ItemResearchBook.getResearcherName(bookSlot.getStackInSlot(0));
    }

    public boolean tryCraftItem() {
        if (canCraft() && canHold()) {
            craftItem();
            return true;
        }
        return false;
    }

    private void craftItem() {
        ItemStack stack = this.outputSlot.getStackInSlot(0).copy();
        useResources();
        stack = InventoryTools.mergeItemStack(outputInventory, stack, -1);
        if (stack != null) {
            InventoryTools.dropItemInWorld(world, stack, pos);
        }
    }

    private void useResources() {
        ItemStack stack1;
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            stack1 = craftMatrix.getStackInSlot(i);
            if (stack1.isEmpty()) {
                continue;
            }
            if(InventoryTools.removeItems(resourceInventory, -1, stack1, 1) != null) {
                stack1 = InventoryTools.getConsumedItem(craftMatrix, resourceInventory, i, stack1);
                if (stack1 != null) {
                    InventoryTools.dropItemInWorld(world, stack1, pos);
                }
            }
        }
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRAFTING;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.bookSlot.deserializeNBT(tag.getCompoundTag("bookSlot"));
        this.resourceInventory.deserializeNBT(tag.getCompoundTag("resourceInventory"));
        this.outputInventory.deserializeNBT(tag.getCompoundTag("outputInventory"));
        this.outputSlot.deserializeNBT(tag.getCompoundTag("outputSlot"));
        InventoryTools.readInventoryFromNBT(craftMatrix, tag.getCompoundTag("craftMatrix"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("bookSlot", bookSlot.serializeNBT());
        tag.setTag("resourceInventory", resourceInventory.serializeNBT());
        tag.setTag("outputInventory", outputInventory.serializeNBT());
        tag.setTag("outputSlot", outputSlot.serializeNBT());
        tag.setTag("craftMatrix", InventoryTools.writeInventoryToNBT(craftMatrix, new NBTTagCompound()));
    }

    @Override
    public void setWorld(World world){
        super.setWorld(world);
        onLayoutMatrixChanged();
    }

    /** ***********************************INVENTORY METHODS*********************************************** */
    private void onLayoutMatrixChanged() {
        this.outputSlot.setInventorySlotContents(0, AWCraftingManager.INSTANCE.findMatchingRecipe(craftMatrix, world, getCrafterName()));
    }

    @Override
    public int getSizeInventory() {
        return resourceInventory.getSizeInventory() + outputInventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return resourceInventory.isEmpty() && outputInventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int slotIndex) {
        if (slotIndex >= resourceInventory.getSizeInventory()) {
            return outputInventory.getStackInSlot(slotIndex-resourceInventory.getSizeInventory());
        }
        return resourceInventory.getStackInSlot(slotIndex);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        if (slot >= resourceInventory.getSizeInventory()) {
            return outputInventory.decrStackSize(slot - resourceInventory.getSizeInventory(), amount);
        }
        return resourceInventory.decrStackSize(slot, amount);
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        if (var1 >= resourceInventory.getSizeInventory()) {
            return outputInventory.removeStackFromSlot(var1 - resourceInventory.getSizeInventory());
        }
        return resourceInventory.removeStackFromSlot(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        if (var1 >= resourceInventory.getSizeInventory()) {
            outputInventory.setInventorySlotContents(var1-resourceInventory.getSizeInventory(), var2);
            return;
        }
        resourceInventory.setInventorySlotContents(var1, var2);
    }

    @Override
    public void onInventoryChanged(IInventory internal){
        if(internal == bookSlot)
            onLayoutMatrixChanged();
        markDirty();
    }

    @Override
    public String getName() {
        return "autocrafting";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return true;
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return true;
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        resourceInventory.clear();
        outputInventory.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        if (side == EnumFacing.UP) {
            return resourceSlotIndices;
        } else if (side == EnumFacing.DOWN) {
            return outputSlotIndices;
        }
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack var2, EnumFacing side) {
        return side == EnumFacing.UP;//top, insert only
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack var2, EnumFacing side) {
        return side == EnumFacing.DOWN;//bottom, extract only
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_AUTO_CRAFT, pos);
        }
        return true;
    }

    @Override
    protected boolean processWork() {
        return tryCraftItem();
    }

    @Override
    protected boolean hasWorksiteWork() {
        return canCraftLastCheck && canHoldLastCheck && !outputSlot.getStackInSlot(0).isEmpty();
    }

    @Override
    protected void updateWorksite() {
        canCraftLastCheck = canCraft();
        canHoldLastCheck = canHold();
    }

    private boolean canHold() {
        ItemStack test = outputSlot.getStackInSlot(0);
        return !test.isEmpty() && InventoryTools.canInventoryHold(outputInventory, -1, test);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }
}
