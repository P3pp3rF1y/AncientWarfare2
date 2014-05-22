package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class FactionData extends WorldSavedData
{

public static final String name = "AWFactionData";

public FactionData(String par1Str)
  {
  super(par1Str);
  }

public FactionData()
  {
  super(name);
  }

@Override
public void readFromNBT(NBTTagCompound var1)
  {

  }

@Override
public void writeToNBT(NBTTagCompound var1)
  {

  }

}
