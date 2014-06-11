package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class NpcBanditArcher extends NpcFactionArcher
{

public NpcBanditArcher(World par1World)
  {
  super(par1World);
  }

@Override
public String getNpcType()
  {
  return "bandit.archer";
  }

}
