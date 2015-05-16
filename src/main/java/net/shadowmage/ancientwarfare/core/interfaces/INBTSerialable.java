package net.shadowmage.ancientwarfare.core.interfaces;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Olivier on 15/05/2015.
 */
public interface INBTSerialable {
    void readFromNBT(NBTTagCompound tag);

    NBTTagCompound writeToNBT(NBTTagCompound tag);
}
