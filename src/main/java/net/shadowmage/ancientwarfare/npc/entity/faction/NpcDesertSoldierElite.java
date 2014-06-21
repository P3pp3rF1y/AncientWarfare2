package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertSoldierElite extends NpcFactionSoldier
{

public NpcDesertSoldierElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "desert.soldier.elite";
  }

}
