package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMelee;
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
  this.tasks.addTask(5, new NpcAIMoveHome(this, 80.f, 20.f, 40.f, 5.f));
  //idle
  collideAI = new NpcAIAttackMelee(this, 0.8D, false);
  arrowAI = new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F);
  
    
  //TODO
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity var1)
      {
      return var1 instanceof EntityZombie;
      }    
    };
  
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityZombie.class, 0, true, false, selector));
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
  return "";
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
