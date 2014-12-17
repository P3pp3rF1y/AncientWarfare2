package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class WorldData extends WorldSavedData
{
public static final String name = "AWWorldData";

NBTTagCompound dataTag = new NBTTagCompound();

/**
 * reflection constructor for mc-vanilla code
 * @param p_i2141_1_
 */
public WorldData(String p_i2141_1_)
  {
  super(name);
  }

public WorldData()
  {
  this(name);
  }

public final boolean get(String key){return dataTag.getBoolean(key);}

public final void set(String name, boolean val)
  {
  dataTag.setBoolean(name, val);
  markDirty();
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  this.dataTag = tag.getCompoundTag("AWWorldData");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("AWWorldData", this.dataTag);
  }


}
