package net.shadowmage.ancientwarfare.automation.tile.worksite;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.upgrade.WorksiteUpgrade;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileOreProcessor extends TileWorksiteBase implements ISidedInventory,IInventoryChangedListener {

    private final InventoryBasic inventory;

    public TileOreProcessor() {
        inventory = new InventoryBasic(2, this);
    }

    @Override
    public void onInventoryChanged(IInventory internal) {
        markDirty();
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, @Nullable EnumHand hand) {
        // TODO implement GUI
        return true;
    }

    @Override
    public void onBlockBroken() {
        super.onBlockBroken();
        if (!world.isRemote) {
            InventoryTools.dropInventoryInWorld(world, inventory, pos);
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
    public int[] getSlotsForFace(EnumFacing side) {
        // TODO implement re-mappable relative block sides
        return null;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
        return slot == 0 && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        //TODO set from recipe list
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
        inventory.clear();
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return inventory.isEmpty();
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
    public ItemStack removeStackFromSlot(int slot) {
        return inventory.removeStackFromSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        inventory.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getName() {
        return inventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return inventory.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }//NOOP

    @Override
    public void closeInventory(EntityPlayer player) {
    }//NOOP

    @Override
    public EnumSet<WorksiteUpgrade> getValidUpgrades() {
        return EnumSet.noneOf(WorksiteUpgrade.class);
    }//NOOP


    //************************************** STANDARD NBT / DATA PACKET METHODS ****************************************//
    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("inventory", inventory.serializeNBT());
        return tag;
    }
}
