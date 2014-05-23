package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NpcCombat extends NpcPlayerOwned
{

public NpcCombat(World par1World)
  {
  super(par1World);
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  // TODO Auto-generated method stub
  
  }


}
