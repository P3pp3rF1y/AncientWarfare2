package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMelee;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;

public class NpcHostile extends NpcBase implements IRangedAttackMob
{

private EntityAIBase collideAI;
private EntityAIBase arrowAI;

public NpcHostile(World par1World)
  {
  super(par1World);
  collideAI = new NpcAIAttackMelee(this, 0.8D, false);
  arrowAI = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F); 
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      return var1 instanceof EntityZombie;//TODO set up selector from config file
      }    
    };
    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  
//  this.tasks.addTask(2, new NpcAIFollowPlayer(this, 1.d, 10.f, 2.f));
//  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));//TODO change to a flee on low health
//  this.tasks.addTask(4, new NpcAIGetFood(this));  
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 20.f, 40.f, 5.f));
//  this.tasks.addTask(6, new NpcAIIdleWhenHungry(this)); 
  
  //post-100 -- used by delayed shared tasks (stay near home, look at random stuff, wander)
  this.tasks.addTask(100, new EntityAIMoveTowardsRestriction(this, 0.6D)); 
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new EntityAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));      
  
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }

@Override
public void onOrdersInventoryChanged()
  {
  //noop
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  //noop on hostile
  return false;
  }

@Override
public void onWeaponInventoryChanged()
  {
  //noop for hostile
  }

@Override
public String getNpcSubType()
  {
  //TODO lookup type based on item equipped in main slot and 'faction'
  return null;
  }

@Override
public String getNpcType()
  {
  return "hostile";
  }

@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
  
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2)
  {
  // TODO Auto-generated method stub
  
  }

}
