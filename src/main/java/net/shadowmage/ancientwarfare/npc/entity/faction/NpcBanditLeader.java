package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditLeader extends NpcFactionLeader
{

public NpcBanditLeader(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.leader";
  }

}
