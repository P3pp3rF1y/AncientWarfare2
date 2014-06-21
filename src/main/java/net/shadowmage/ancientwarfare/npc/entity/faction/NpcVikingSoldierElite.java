package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcVikingSoldierElite extends NpcFactionSoldier
{

public NpcVikingSoldierElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "viking.soldier";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
