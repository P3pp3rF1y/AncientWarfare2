package net.shadowmage.ancientwarfare.npc.entity;

import cpw.mods.fml.common.registry.EntityRegistry;
import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMelee;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMelee2;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWork;

public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob
{

private EntityAIBase collideAI;
private EntityAIBase arrowAI;

public NpcCombat(World par1World)
  {
  super(par1World);
  collideAI = new NpcAIAttackMelee2(this);
  arrowAI = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F); 
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity entity)
      {
      String name = EntityList.getEntityString(entity);
      if(AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType()).contains(name))
        {
        return true;
        }
      return false;
      }    
    };
    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
//  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));//TODO change to a flee on low health
  this.tasks.addTask(4, new NpcAIGetFood(this));  
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 20.f, 40.f, 5.f));
  this.tasks.addTask(6, new NpcAIIdleWhenHungry(this)); 
  //7==combat task, inserted onweaponinventoryupdated
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new EntityAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));      
  
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }



@Override
public boolean attackEntityAsMob(Entity target)
  {
  float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
  int knockback = 0;

  if (target instanceof EntityLivingBase)
    {
    damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)target);
    knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)target);
    }

  boolean targetHit = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);

  if (targetHit)
    {
    if (knockback > 0)
      {
      target.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)knockback * 0.5F));
      this.motionX *= 0.6D;
      this.motionZ *= 0.6D;
      }
    int fireDamage = EnchantmentHelper.getFireAspectModifier(this);

    if (fireDamage > 0)
      {
      target.setFire(fireDamage * 4);
      }
    if (target instanceof EntityLivingBase)
      {
      EnchantmentHelper.func_151384_a((EntityLivingBase)target, this);
      }
    EnchantmentHelper.func_151385_b(this, target);
    }

  return targetHit;
  }

//@Override
//public void onLivingUpdate()
//  {
//  super.onLivingUpdate();
//  if(this.getAITarget()!=null && this.getAITarget().isDead){this.setTarget(null);}
//  }

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
public void setCurrentItemOrArmor(int par1, ItemStack par2ItemStack)
  {  
  super.setCurrentItemOrArmor(par1, par2ItemStack);
  if(par1==0){onWeaponInventoryChanged();}
  }

@Override
public void onWeaponInventoryChanged()
  {
  super.onWeaponInventoryChanged();
  AWLog.logDebug("weapon inventory changed, setting combat ai..");
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
  return "soldier";
  }

@Override
public String getNpcType()
  {
  return "combat";
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  onWeaponInventoryChanged();
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase var1, float var2)
  {
  // TODO Auto-generated method stub
  AWLog.logDebug("should do ranged attack!!");
  }

}
