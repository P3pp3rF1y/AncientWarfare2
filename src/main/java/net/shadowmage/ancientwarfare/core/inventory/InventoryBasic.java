package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class InventoryBasic extends net.minecraft.inventory.InventoryBasic implements INBTSerializable<NBTTagCompound> {

    public InventoryBasic(int size) {
        super("AW.InventoryBasic", false, size);
    }

    public InventoryBasic(int size, IInventoryChangedListener listener) {
        this(size);
        addInventoryChangeListener(listener);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return InventoryTools.writeInventoryToNBT(this, new NBTTagCompound());
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        InventoryTools.readInventoryFromNBT(this, tag);
    }
}
