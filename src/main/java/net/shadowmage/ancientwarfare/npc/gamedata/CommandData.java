package net.shadowmage.ancientwarfare.npc.gamedata;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class CommandData extends WorldSavedData
{

public static final String name = "AWCommandData";
private  int nextBatonId = 0;

private HashMap<Integer, Object> commandSets = new HashMap<Integer, Object>();

public CommandData(String par1Str)
  {
  super(par1Str);
  }

public CommandData()
  {
  super(name);
  }

public int getNextBatonId()
  {
  return nextBatonId++;
  }

public void onNpcClicked(NpcBase npc)
  {
  
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  nextBatonId = tag.getInteger("nextBatonId");
  // TODO Auto-generated method stub
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  tag.setInteger("nextBatonId", nextBatonId);
  // TODO Auto-generated method stub
  }

}
