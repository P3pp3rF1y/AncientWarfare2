package net.shadowmage.ancientwarfare.automation.tile.torque;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.TorqueCell;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class TileSterlingEngine extends TileTorqueSingleCell implements IInventory {

    InventoryBasic fuelInventory = new InventoryBasic(1) {
        public boolean isItemValidForSlot(int var1, net.minecraft.item.ItemStack var2) {
            return TileEntityFurnace.getItemBurnTime(var2) > 0;
        }
    };

    int burnTime = 0;
    int burnTimeBase = 0;

    public TileSterlingEngine() {
        torqueCell = new TorqueCell(0, 4, 1600, AWAutomationStatics.med_efficiency_factor);
    }

    @Override
    public void updateEntity() {
        super.updateEntity();
        if (!worldObj.isRemote) {
            if (burnTime <= 0 && torqueCell.getEnergy() < torqueCell.getMaxEnergy()) {
                if (fuelInventory.getStackInSlot(0) != null) {
                    //if fueled, consume one, set burn-ticks to fuel value
                    int ticks = TileEntityFurnace.getItemBurnTime(fuelInventory.getStackInSlot(0));
                    if (ticks > 0) {
                        fuelInventory.decrStackSize(0, 1);
                        burnTime = ticks;
                        burnTimeBase = ticks;
                    }
                }
            } else if (burnTime > 0) {
                torqueCell.setEnergy(torqueCell.getEnergy() + AWAutomationStatics.sterling_generator_output);
                burnTime--;
            }
        }
    }

    @Override
    public boolean onBlockClicked(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_TORQUE_GENERATOR_STERLING, xCoord, yCoord, zCoord);
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
    public ItemStack getStackInSlot(int var1) {
        return fuelInventory.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2) {
        return fuelInventory.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1) {
        return fuelInventory.getStackInSlotOnClosing(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2) {
        fuelInventory.setInventorySlotContents(var1, var2);
    }

    @Override
    public String getInventoryName() {
        return fuelInventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return fuelInventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return fuelInventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return fuelInventory.isUseableByPlayer(var1);
    }

    @Override
    public void openInventory() {
        fuelInventory.openInventory();
    }

    @Override
    public void closeInventory() {
        fuelInventory.closeInventory();
    }

    @Override
    public boolean isItemValidForSlot(int var1, ItemStack var2) {
        return fuelInventory.isItemValidForSlot(var1, var2);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        burnTime = tag.getInteger("burnTicks");
        burnTimeBase = tag.getInteger("burnTicksBase");
        if (tag.hasKey("inventory")) {
            fuelInventory.readFromNBT(tag.getCompoundTag("inventory"));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("burnTicks", burnTime);
        tag.setInteger("burnTicksBase", burnTimeBase);
        tag.setTag("inventory", fuelInventory.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public boolean canInputTorque(ForgeDirection from) {
        return false;
    }

}
