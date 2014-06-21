package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2SoldierElite extends NpcFactionSoldier
{

public NpcCustom_2SoldierElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "custom_2.soldier.elite";
  }

}
