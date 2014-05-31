package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NpcPriest extends NpcPlayerOwned
{

public NpcPriest(World par1World)
  {
  super(par1World);
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {

  }

@Override
public String getNpcSubType()
  {
  return "";
  }

@Override
public String getNpcType()
  {
  return "priest";
  }

}
