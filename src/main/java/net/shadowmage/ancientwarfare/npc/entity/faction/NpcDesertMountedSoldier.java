package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertMountedSoldier extends NpcFactionMountedSoldier
{

public NpcDesertMountedSoldier(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "desert.cavalry";
  }

}
