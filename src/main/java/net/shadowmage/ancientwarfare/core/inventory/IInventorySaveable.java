package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface IInventorySaveable extends IInventory, INBTSerializable<NBTTagCompound> {

}
