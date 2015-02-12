package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.nbt.NBTTagCompound;

public abstract class NpcOrders {

    public abstract void readFromNBT(NBTTagCompound tag);

    public abstract NBTTagCompound writeToNBT(NBTTagCompound tag);

}
