package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingCivilian extends NpcFactionCivilian
{

public NpcVikingCivilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "viking.civilian";
  }

}
