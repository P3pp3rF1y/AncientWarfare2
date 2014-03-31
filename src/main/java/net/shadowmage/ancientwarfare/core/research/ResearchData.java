package net.shadowmage.ancientwarfare.core.research;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class ResearchData extends WorldSavedData
{

private HashMap<String, Set<Integer>> playerResearchData = new HashMap<String, Set<Integer>>();

public static final String name = "AWResearchData";

public ResearchData(String par1Str)
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
