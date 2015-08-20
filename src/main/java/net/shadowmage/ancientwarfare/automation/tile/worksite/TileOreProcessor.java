package net.shadowmage.ancientwarfare.automation.tile.worksite;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.EnumSet;

public class TileOreProcessor extends TileWorksiteBase implements ISidedInventory,IInvBasic {

    private final InventoryBasic inventory;

    public TileOreProcessor() {
        inventory = new InventoryBasic(2, this);
    }

    @Override
    public void onInventoryChanged(net.minecraft.inventory.InventoryBasic internal) {
        markDirty();
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        // TODO implement GUI
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        // TODO implement re-mappable relative block sides
        return null;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        //TODO set from recipe list
        return true;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return slot == 0 && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return true;
    }

    @Override
    public void onBlockBroken() {
        super.onBlockBroken();
        if (!worldObj.isRemote) {
            InventoryTools.dropInventoryInWorld(worldObj, inventory, xCoord, yCoord, zCoord);
        }
    }

    @Override
    protected boolean processWork() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected boolean hasWorksiteWork() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void updateWorksite() {
        // TODO Auto-generated method stub
    }

    @Override
    public WorkType getWorkType() {
        return WorkType.CRAFTING;
    }

//************************************** BRIDGE/TEMPLATE/ACCESSOR METHODS ****************************************//

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amt) {
        return inventory.decrStackSize(slot, amt);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return inventory.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory() {
    }//NOOP

    @Override
    public void closeInventory() {
    }//NOOP

    @Override
    public BlockPosition getWorkBoundsMin() {
        return null;
    }//NOOP

    @Override
    public BlockPosition getWorkBoundsMax() {
        return null;
    }//NOOP

    @Override
    public boolean userAdjustableBlocks() {
        return false;
    }//NOOP

    @Override
    public boolean hasWorkBounds() {
        return false;
    }//NOOP

    @Override
    public int getBoundsMaxWidth() {
        return 0;
    }//NOOP

    @Override
    public int getBoundsMaxHeight() {
        return 0;
    }//NOOP

    @Override
    public void setBounds(BlockPosition p1, BlockPosition p2) {
    }//NOOP

    @Override
    public void setWorkBoundsMax(BlockPosition max) {
    }//NOOP

    @Override
    public void setWorkBoundsMin(BlockPosition min) {
    }//NOOP

    @Override
    public void onBoundsAdjusted() {
    }//NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP


    //************************************** STANDARD NBT / DATA PACKET METHODS ****************************************//
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readFromNBT(tag.getCompoundTag("inventory"));
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.writeToNBT(new NBTTagCompound()));
    }

}
