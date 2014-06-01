package net.shadowmage.ancientwarfare.npc.ai;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class NpcAICourier extends NpcAI
{

public NpcAICourier(NpcBase npc)
  {
  super(npc);
  }

@Override
public boolean shouldExecute()
  {
  return false;
  }

public void readFromNBT(NBTTagCompound tag){}//TODO

public NBTTagCompound writeToNBT(NBTTagCompound tag){return tag;}//TODO

}
