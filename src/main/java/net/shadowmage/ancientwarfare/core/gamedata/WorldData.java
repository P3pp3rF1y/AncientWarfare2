//TODO world capability
package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WorldData extends WorldSavedData {

    NBTTagCompound dataTag = new NBTTagCompound();

    /*
     * reflection constructor for mc-vanilla code
     */
    public WorldData(String par) {
        super(par);
    }

    public final boolean get(String key) {
        return dataTag.getBoolean(key);
    }

    public final void set(String name, boolean val) {
        dataTag.setBoolean(name, val);
        markDirty();
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        this.dataTag = tag.getCompoundTag("AWWorldData");
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setTag("AWWorldData", this.dataTag);
    }


}
