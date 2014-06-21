package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateArcherElite extends NpcFactionArcher
{

public NpcPirateArcherElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "pirate.archer.elite";
  }

}
