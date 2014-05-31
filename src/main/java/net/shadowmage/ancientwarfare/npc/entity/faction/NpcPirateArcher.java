package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class NpcPirateArcher extends NpcFactionArcher
{

public NpcPirateArcher(World par1World)
  {
  super(par1World);
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2)
  {

  }

@Override
public String getNpcType()
  {
  return "pirate.archer";
  }

}
