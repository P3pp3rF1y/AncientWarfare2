package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcDesertArcherElite extends NpcFactionArcher
{

public NpcDesertArcherElite(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "desert.archer";
  }

@Override
public String getNpcSubType()
  {
  return "elite";
  }

}
