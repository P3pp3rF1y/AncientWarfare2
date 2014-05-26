package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;

public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob
{

private EntityAIBase collideAI;
private EntityAIBase arrowAI;

public NpcCombat(World par1World)
  {
  super(par1World);
//  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));//TODO change this to a self-defense task
  //get food
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 4.f, 40.f, 20.f));
  //idle
  collideAI = new EntityAIAttackOnCollide(this, EntityLivingBase.class, 1.0D, false);
  arrowAI = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
  
    
  //TODO
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      // TODO implement target-list stuff from config
      // TODO implement team-checking for players and other npcs
      return false;
      }    
    };
  
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
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
  this.tasks.removeTask(arrowAI);
  this.tasks.removeTask(collideAI);
  ItemStack stack = getEquipmentInSlot(0);
  if(stack!=null && stack.getItem()==Items.bow)
    {
    this.tasks.addTask(7, arrowAI);
    }
  else
    {
    this.tasks.addTask(7, collideAI);
    }
  //TODO set damage
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

@Override
public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2)
  {
  // TODO Auto-generated method stub
  
  }

}
