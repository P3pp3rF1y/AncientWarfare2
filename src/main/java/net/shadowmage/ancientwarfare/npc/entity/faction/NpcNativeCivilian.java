package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeCivilian extends NpcFactionCivilian
{

public NpcNativeCivilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "native.civilian";
  }

}
