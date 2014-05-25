package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;

public class NpcCombat extends NpcPlayerOwned
{

public NpcCombat(World par1World)
  {
  super(par1World);
//  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));//TODO change this to a self-defense task
  //get food
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 4.f, 40.f, 20.f));
  //idle
  // TODO add long-range attack command
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

@Override
public void onWeaponInventoryChanged()
  {
  super.onWeaponInventoryChanged();
  // TODO set damage from weapon / type
  }

@Override
public String getNpcSubType()
  {
  //TODO lookup type based on item equipped in main slot
  return "";
  }

@Override
public String getNpcType()
  {
  return "combat";
  }

}
