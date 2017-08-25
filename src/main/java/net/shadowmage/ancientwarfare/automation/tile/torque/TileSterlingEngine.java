package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

import javax.annotation.Nonnull;

public class TileSterlingEngine extends TileTorqueSingleCell implements IInventory {

    private final InventoryBasic fuelInventory = new InventoryBasic(1) {
        @Override
        public boolean isItemValidForSlot(int var1, ItemStack var2) {
            return TileEntityFurnace.getItemBurnTime(var2) > 0;
        }
    };

    int burnTime = 0;
    int burnTimeBase = 0;

    public TileSterlingEngine() {
        torqueCell = new TorqueCell(0, 4, 1600, AWAutomationStatics.med_efficiency_factor);
    }

    @Override
    public void update() {
        super.update();
        if (!world.isRemote) {
            if (burnTime <= 0 && torqueCell.getEnergy() < torqueCell.getMaxEnergy()) {
                //if fueled, consume one, set burn-ticks to fuel value
                int ticks = TileEntityFurnace.getItemBurnTime(getStackInSlot(0));
                if (ticks > 0) {
                    decrStackSize(0, 1);
                    burnTime = ticks;
                    burnTimeBase = ticks;
                }
            } else if (burnTime > 0) {
                torqueCell.setEnergy(torqueCell.getEnergy() + AWAutomationStatics.sterling_generator_output);
                burnTime--;
            }
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player, EnumHand hand) {
        if (!player.world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, pos);
        }
        return true;
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getBurnTimeBase() {
        return burnTimeBase;
    }

    @Override
    public int getSizeInventory() {
        return fuelInventory.getSizeInventory();
    }

    @Override
    public boolean isEmpty() {
        return fuelInventory.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int var1) {
        return fuelInventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        @Nonnull ItemStack stack = fuelInventory.decrStackSize(var1, var2);
        if(!stack.isEmpty())
            markDirty();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int var1) {
        return fuelInventory.removeStackFromSlot(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        fuelInventory.setInventorySlotContents(var1, var2);
        markDirty();
    }

    @Override
    public String getName() {
        return fuelInventory.getName();
    }

    @Override
    public boolean hasCustomName() {
        return fuelInventory.hasCustomName();
    }

    @Override
    public int getInventoryStackLimit() {
        return fuelInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer var1) {
        return fuelInventory.isUsableByPlayer(var1);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        fuelInventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        fuelInventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return fuelInventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public int getField(int id) {
        return fuelInventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        fuelInventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return fuelInventory.getFieldCount();
    }

    @Override
    public void clear() {
        fuelInventory.clear();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        burnTime = tag.getInteger("burnTicks");
        burnTimeBase = tag.getInteger("burnTicksBase");
        if (tag.hasKey("inventory")) {
            fuelInventory.deserializeNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("burnTicks", burnTime);
        tag.setInteger("burnTicksBase", burnTimeBase);
        tag.setTag("inventory", fuelInventory.serializeNBT());
        return tag;
    }

    @Override
    public boolean canInputTorque(EnumFacing from) {
        return false;
    }

}
