package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateLeaderElite extends NpcFactionLeader
{

public NpcPirateLeaderElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "pirate.leader";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
