package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2ArcherElite extends NpcFactionArcher
{

public NpcCustom_2ArcherElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "custom_2.archer";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
