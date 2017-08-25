package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
        if (!stack.isEmpty()) {
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
            world.playEvent(2001, pos, Block.getStateId(state));
            if (!world.setBlockToAir(pos)) {
                return false;
            }

            if (ModAccessors.TREECAPITATOR_LOADED) {
                //TODO implement integration with the new treecapitator port ??
                //ModAccessors.TREECAPITATOR.doTreecapitate(world, block, meta, x, y, z);
            }

            if (ModAccessors.ENVIROMINE_LOADED) {
                //TODO enviromine support
                //ModAccessors.ENVIROMINE.schedulePhysUpdate(world, pos, true, "Normal");
            }
        }
        for (ItemStack stack : stacks) {
            stack = InventoryTools.mergeItemStack(inventory, stack, combinedIndices);//was already validated that items would fit via canInventoryHold call
            if (!stack.isEmpty())//but just in case, drop into world anyway if not null..
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
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (inventory != null) {
            tag.setTag("inventory", inventory.serializeNBT());
        }
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey("inventory") && inventory != null) {
            inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public final int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return false;
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
    public final ItemStack removeStackFromSlot(int var1) {
        return inventory.removeStackFromSlot(var1);
    }

    @Override
    public final void setInventorySlotContents(int var1, ItemStack var2) {
        inventory.setInventorySlotContents(var1, var2);
    }

    @Override
    public final String getName() {
        return inventory.getName();
    }

    @Override
    public final boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public final int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public final boolean isUsableByPlayer(EntityPlayer var1) {
        return inventory.isUsableByPlayer(var1);
    }

    @Override
    public final void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public final void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public final boolean isItemValidForSlot(int var1, ItemStack var2) {
        return inventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return inventory.canInsertItem(index, stack, direction);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return inventory.canExtractItem(index, stack, direction);
    }
}
