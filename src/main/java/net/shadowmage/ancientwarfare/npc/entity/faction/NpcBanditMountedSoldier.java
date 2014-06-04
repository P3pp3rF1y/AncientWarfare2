package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditMountedSoldier extends NpcFactionMountedSoldier
{

public NpcBanditMountedSoldier(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.cavalry";
  }

}
