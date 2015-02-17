package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileSterlingEngine;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;

public class ContainerTorqueGeneratorSterling extends ContainerTileBase<TileSterlingEngine> {

    public int guiHeight;
    public double energy;
    public int burnTime;
    public int burnTimeBase;

    public ContainerTorqueGeneratorSterling(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        addSlotToContainer(new Slot(tileEntity, 0, 8 + 4 * 18, 8 + 12) {
            @Override
            public boolean isItemValid(ItemStack par1ItemStack) {
                return TileEntityFurnace.isItemFuel(par1ItemStack);
            }
        });
        addPlayerSlots(8, 8 + 18 + 8 + 12 + 12, 4);
        guiHeight = 8 + 18 + 8 + 4 * 18 + 4 + 8 + 12 + 12;
    }

    @Override
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            double e = (double) par2;
            energy = e * 0.001d * tileEntity.getMaxTorque(tileEntity.getPrimaryFacing());
            refreshGui();
        }
        if (par1 == 1) {
            burnTime = par2;
            refreshGui();
        }
        if (par1 == 2) {
            burnTimeBase = par2;
            refreshGui();
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        double g = tileEntity.getTorqueStored(tileEntity.getPrimaryFacing());
        if (g != energy) {
            energy = g;
            g = g / tileEntity.getMaxTorque(tileEntity.getPrimaryFacing());
            int e = (int) (g * 1000.d);
            for (Object crafter : this.crafters) {
                ((ICrafting) crafter).sendProgressBarUpdate(this, 0, e);
            }
        }
        int b = tileEntity.getBurnTime();
        if (b != burnTime) {
            burnTime = b;
            for (Object crafter : this.crafters) {
                ((ICrafting) crafter).sendProgressBarUpdate(this, 1, b);
            }
        }
        b = tileEntity.getBurnTimeBase();
        if (b != burnTimeBase) {
            burnTimeBase = b;
            for (Object crafter : this.crafters) {
                ((ICrafting) crafter).sendProgressBarUpdate(this, 2, b);
            }
        }
    }

    /**
     * @return should always return null for normal implementation, not sure wtf the rest of the code is about
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex) {
        int slots = 1;
        Slot slot = this.getSlot(slotClickedIndex);
        if (slot == null || !slot.getHasStack()) {
            return null;
        }
        ItemStack stackFromSlot = slot.getStack();
        if (slotClickedIndex == 0)//click on input slot, merge into player inventory
        {
            this.mergeItemStack(stackFromSlot, slots, slots + 36, false);
        } else//click on player slot, attempt merge into te inventory
        {
            this.mergeItemStack(stackFromSlot, 0, 1, false);
        }
        if (stackFromSlot.stackSize == 0) {
            slot.putStack(null);
        } else {
            slot.onSlotChanged();
        }
        return null;
    }

}
