package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.api.IAncientWarfareFarmable;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.List;

/**
 * abstract base class for worksite based tile-entities (or at least a template to copy from)
 * <p/>
 * handles the management of worker references and work-bounds, as well as inventory bridge methods.
 * <p/>
 * All implementing classes must initialize the inventory field in their constructor, or things
 * will go very crashy when the block is placed in the world.
 *
 * @author Shadowmage
 */
public abstract class TileWorksiteBoundedInventory extends TileWorksiteBounded implements ISidedInventory {

    public InventorySided inventory;

    public TileWorksiteBoundedInventory() {

    }

    public void openAltGui(EntityPlayer player) {
        //noop, must be implemented by individual tiles, if they have an alt-control gui
    }

    /**
     * attempt to add an item stack to this worksites inventory.<br>
     * iterates through input sides in the order given,
     * so should pick the most restrictive inventory first,
     * least restrictive last
     */
    public final void addStackToInventory(ItemStack stack, RelativeSide... sides) {
        int[] slots = inventory.getRawIndicesCombined(sides);
        stack = InventoryTools.mergeItemStack(inventory, stack, slots);
        if (stack != null) {
            InventoryTools.dropItemInWorld(world, stack, pos);
        }
    }

    protected boolean harvestBlock(BlockPos pos, RelativeSide... relativeSides) {
        int[] combinedIndices = inventory.getRawIndicesCombined(relativeSides);
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        NonNullList<ItemStack> stacks = NonNullList.create();
        if(block instanceof IAncientWarfareFarmable) {
            stacks = ((IAncientWarfareFarmable) block).doHarvest(world, pos, getFortune());
        } else {
            block.getDrops(stacks, world, pos, state, getFortune());
            if (!InventoryTools.canInventoryHold(inventory, combinedIndices, stacks)) {
                return false;
            }
            if (!BlockTools.canBreakBlock(world, getOwnerAsPlayer(), pos, state)) {
                return false;
            }
            world.playEvent(2001, pos, Block.getIdFromBlock(block) + (meta << 12));
            if (!world.setBlockToAir(pos)) {
                return false;
            }

            if (ModAccessors.TREECAPITATOR_LOADED)
                ModAccessors.TREECAPITATOR.doTreecapitate(world, block, meta, x, y, z);
            
            if (ModAccessors.ENVIROMINE_LOADED)
                ModAccessors.ENVIROMINE.schedulePhysUpdate(world, x, y, z, true, "Normal");
        }
        for (ItemStack stack : stacks) {
            stack = InventoryTools.mergeItemStack(inventory, stack, combinedIndices);//was already validated that items would fit via canInventoryHold call
            if (stack != null)//but just in case, drop into world anyway if not null..
            {
                InventoryTools.dropItemInWorld(world, stack, pos);
            }
        }
        return true;
    }

    protected <T extends Entity> List<T> getEntitiesWithinBounds(Class<? extends T> clazz){
        BlockPos p1 = getWorkBoundsMin();
        BlockPos p2 = getWorkBoundsMax();
        AxisAlignedBB bb = new AxisAlignedBB(p1.getX(), p1.getY(), p1.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1);
        return world.getEntitiesWithinAABB(clazz, bb);
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (inventory != null) {
            NBTTagCompound invTag = new NBTTagCompound();
            inventory.writeToNBT(invTag);
            tag.setTag("inventory", invTag);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("inventory") && inventory != null) {
            inventory.readFromNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public final int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public final ItemStack getStackInSlot(int var1) {
        return inventory.getStackInSlot(var1);
    }

    @Override
    public final ItemStack decrStackSize(int var1, int var2) {
        return inventory.decrStackSize(var1, var2);
    }

    @Override
    public final ItemStack getStackInSlotOnClosing(int var1) {
        return inventory.getStackInSlotOnClosing(var1);
    }

    @Override
    public final void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
    }

    @Override
    public final String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public final boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public final int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public final boolean isUseableByPlayer(EntityPlayer var1) {
        return inventory.isUseableByPlayer(var1);
    }

    @Override
    public final void openInventory() {
        inventory.openInventory();
    }

    @Override
    public final void closeInventory() {
        inventory.closeInventory();
    }

    @Override
    public final boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public final int[] getAccessibleSlotsFromSide(int var1) {
        return inventory.getAccessibleSlotsFromSide(var1);
    }

    @Override
    public final boolean canInsertItem(int var1, ItemStack var2, int var3) {
        return inventory.canInsertItem(var1, var2, var3);
    }

    @Override
    public final boolean canExtractItem(int var1, ItemStack var2, int var3) {
        return inventory.canExtractItem(var1, var2, var3);
    }

}
