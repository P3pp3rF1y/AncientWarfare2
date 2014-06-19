package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1Civilian extends NpcFactionCivilian
{

public NpcCustom_1Civilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "civilian_1.civilian";
  }

}
