package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateCivilianFemale extends NpcFactionCivilian
{

public NpcPirateCivilianFemale(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "pirate.civilian.female";
  }

}
