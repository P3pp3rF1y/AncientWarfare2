package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcNativeLeaderElite extends NpcFactionLeader
{

public NpcNativeLeaderElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "native.leader";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
