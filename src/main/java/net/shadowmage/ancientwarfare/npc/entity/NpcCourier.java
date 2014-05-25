package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;

public class NpcCourier extends NpcPlayerOwned
{

public NpcCourier(World par1World)
  {
  super(par1World);
  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
  //get food
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 4.f, 40.f, 20.f));
  //idle
//  this.tasks.addTask(7, (workAI = new NpcAIWork(this)));//TODO make courier work AI
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
public String getNpcSubType()
  {
  return "";//TODO make a liquid courier?? how to define liquid filters?
  }

@Override
public String getNpcType()
  {
  return "courier";
  }


}
