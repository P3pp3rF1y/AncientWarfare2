package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditLeaderElite extends NpcFactionLeader
{

public NpcBanditLeaderElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.leader";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
