package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.world.World;

public class NpcBanditCivilian extends NpcFactionCivilian
{

public NpcBanditCivilian(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.civilian";
  }

}
