package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class NpcPirateMountedArcher extends NpcFactionMountedArcher
{

public NpcPirateMountedArcher(World par1World)
  {
  super(par1World);
  // TODO Auto-generated constructor stub
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2)
  {
  // TODO Auto-generated method stub

  }

@Override
public String getNpcType()
  {
  return "pirate.mounted_archer";
  }

}
