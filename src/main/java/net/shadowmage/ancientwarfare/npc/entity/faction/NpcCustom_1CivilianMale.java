package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcCustom_1CivilianMale extends NpcFactionCivilian
{

public NpcCustom_1CivilianMale(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "civilian_1.civilian.male";
  }
}
