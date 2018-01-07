package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

public class TileAutoCrafting extends TileWorksiteBase implements IInventoryChangedListener {
    public ItemStackHandler bookSlot = new ItemStackHandler(1) {

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return ItemResearchBook.getResearcherName(stack) != null ? super.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        protected void onContentsChanged(int slot) {
            onLayoutMatrixChanged();
            markDirty();
        }
    };
    public ItemStackHandler outputInventory = new ItemStackHandler(9) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    public ItemStackHandler resourceInventory = new ItemStackHandler(18) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }
    };
    public InventoryBasic outputSlot = new InventoryBasic(1);//the templated output slot, non-pullable
    public InventoryCrafting craftMatrix = new InventoryCrafting(new Container() {
        @Override
        public void onCraftMatrixChanged(IInventory inventoryIn) {
            onLayoutMatrixChanged();
            markDirty();
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn) {
            return true;
        }
    },3, 3);//the 3x3 recipe template/matrix

    private boolean canCraftLastCheck = false;
    private boolean canHoldLastCheck = false;

    private IRecipe recipe;

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
    }

    private boolean canCraft() {
        if (outputSlot.getStackInSlot(0).isEmpty()) {
            return false;
        }//no output stack, don't even bother checking
        ArrayList<ItemStack> compactedCraft = new ArrayList<>();
        @Nonnull ItemStack stack1;
        @Nonnull ItemStack stack2;
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
            if (InventoryTools.getCountOf(resourceInventory, stack3) < stack3.getCount()) {
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
        @Nonnull ItemStack stack = this.outputSlot.getStackInSlot(0).copy();
        useResources();
        stack = InventoryTools.mergeItemStack(outputInventory, stack);
        if (!stack.isEmpty()) {
            InventoryTools.dropItemInWorld(world, stack, pos);
        }
    }

    private void useResources() {
        for (int i = 0; i < craftMatrix.getSizeInventory(); i++) {
            ItemStack stack = craftMatrix.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            if(!InventoryTools.removeItems(resourceInventory, stack, 1).isEmpty()) {
                if (recipe != null) {
                    NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(craftMatrix);
                    InventoryTools.dropItemsInWorld(world, remainingItems, pos);
                }
            }
        }
    }

    private void updateRecipe() {
        if (AWCraftingManager.findMatchingRecipe(craftMatrix, world, getCrafterName()).isEmpty()) {
            recipe = CraftingManager.findMatchingRecipe(craftMatrix, world);
        } else {
            recipe = null;
        }
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRAFTING;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        bookSlot.deserializeNBT(tag.getCompoundTag("bookSlot"));
        resourceInventory.deserializeNBT(tag.getCompoundTag("resourceInventory"));
        outputInventory.deserializeNBT(tag.getCompoundTag("outputInventory"));
        outputSlot.deserializeNBT(tag.getCompoundTag("outputSlot"));
        InventoryTools.readInventoryFromNBT(craftMatrix, tag.getCompoundTag("craftMatrix"));
        updateRecipe();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("bookSlot", bookSlot.serializeNBT());
        tag.setTag("resourceInventory", resourceInventory.serializeNBT());
        tag.setTag("outputInventory", outputInventory.serializeNBT());
        tag.setTag("outputSlot", outputSlot.serializeNBT());
        tag.setTag("craftMatrix", InventoryTools.writeInventoryToNBT(craftMatrix, new NBTTagCompound()));
        return tag;
    }

    @Override
    public void setWorld(World world){
        super.setWorld(world);
        onLayoutMatrixChanged();
    }

    /* ***********************************INVENTORY METHODS*********************************************** */
    private void onLayoutMatrixChanged() {
        this.outputSlot.setInventorySlotContents(0, AWCraftingManager.findMatchingRecipe(craftMatrix, world, getCrafterName()));
        updateRecipe();
    }

    @Override
    public void onInventoryChanged(IInventory internal){
        if(internal == bookSlot)
            onLayoutMatrixChanged();
        markDirty();
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
        @Nonnull ItemStack test = outputSlot.getStackInSlot(0);
        return !test.isEmpty() && InventoryTools.canInventoryHold(outputInventory, test);
    }

    @Override
    public boolean shouldRenderInPass(int pass) {
        return pass == 0;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && facing != null) {
            if (facing == EnumFacing.DOWN) {
                return (T) outputInventory;
            } else {
                return (T) resourceInventory;
            }
        }

        return super.getCapability(capability, facing);
    }
}
