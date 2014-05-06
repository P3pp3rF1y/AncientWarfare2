package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;

public interface IInventorySaveable extends IInventory
{

public void readFromNBT(NBTTagCompound tag);
public NBTTagCompound writeToNBT(NBTTagCompound tag);

}
