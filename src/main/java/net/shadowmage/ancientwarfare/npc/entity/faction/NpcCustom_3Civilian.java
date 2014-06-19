package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_3Civilian extends NpcFactionCivilian
{

public NpcCustom_3Civilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "civilian_3.civilian";
  }

}
