package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateCivilianMale extends NpcFactionCivilian
{

public NpcPirateCivilianMale(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "pirate.civilian.male";
  }

}
