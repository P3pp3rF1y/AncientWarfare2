package net.shadowmage.ancientwarfare.npc.gamedata;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
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

public int getStandingFor(World world, String playerName)
  {
  return 0;
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
