package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class NpcVikingMountedArcher extends NpcFactionMountedArcher
{

public NpcVikingMountedArcher(World par1World)
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
  return "viking.mounted_archer";
  }

}
