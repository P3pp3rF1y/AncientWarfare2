package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_2Civilian extends NpcFactionCivilian
{

public NpcCustom_2Civilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "civilian_2.civilian";
  }

}
