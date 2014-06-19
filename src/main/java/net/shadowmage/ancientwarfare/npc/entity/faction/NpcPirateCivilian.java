package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcPirateCivilian extends NpcFactionCivilian
{

public NpcPirateCivilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "priate.civilian";
  }

}
