package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIBard;

public class NpcBard extends NpcPlayerOwned
{

public NpcBard(World par1World)
  {
  super(par1World);
  
  this.tasks.addTask(5, new NpcAIBard(this));
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
  return "bard";
  }

}
