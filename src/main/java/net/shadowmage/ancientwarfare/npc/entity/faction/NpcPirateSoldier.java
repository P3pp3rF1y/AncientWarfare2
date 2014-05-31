package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateSoldier extends NpcFactionSoldier
{

public NpcPirateSoldier(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "pirate.soldier";
  }

}
