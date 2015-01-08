package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.item.ItemHammer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMedicBase;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAttackRanged;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFindCommander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedPatrol;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob
{

private EntityAIBase collideAI;
private EntityAIBase arrowAI;
private NpcAIPlayerOwnedPatrol patrolAI;

public NpcCombat(World par1World)
  {
  super(par1World);
  collideAI = new NpcAIAttackMeleeLongRange(this);
  arrowAI = new NpcAIPlayerOwnedAttackRanged(this); 
  
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity entity)
      {
      return isHostileTowards(entity);
      }    
    };
    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIPlayerOwnedRideHorse(this)));
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
  this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
  this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
  //6--empty....
  //7==combat task, inserted from onweaponinventoryupdated
  this.tasks.addTask(8, new NpcAIMedicBase(this));
  this.tasks.addTask(9, (patrolAI = new NpcAIPlayerOwnedPatrol(this)));
  
  this.tasks.addTask(10, new NpcAIMoveHome(this, 50.f, 5.f, 20.f, 5.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));      
  
  this.targetTasks.addTask(0, new NpcAIPlayerOwnedFindCommander(this));
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }

@Override
protected void applyEntityAttributes()
  {
  super.applyEntityAttributes();    
  this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(AWNPCStatics.npcAttackDamage);
  }



@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return stack!=null && stack.getItem()==AWNpcItemLoader.combatOrder;
  }

@Override
public void onOrdersInventoryChanged()
  {
  patrolAI.onOrdersInventoryChanged();
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
  if(!worldObj.isRemote)
    {
    this.tasks.removeTask(arrowAI);
    this.tasks.removeTask(collideAI);
    ItemStack stack = getEquipmentInSlot(0);
    Item item = stack==null ? null : stack.getItem();
    
    // Inserting QuiverBow recognition here (for b78)
    if (Loader.isModLoaded("quiverchevsky"))
    {
    	if (item instanceof com.domochevsky.quiverbow.weapons._WeaponBase)
    	{
    		this.tasks.addTask(7, arrowAI);	// Ranged fighter
    	}
    	// else, not holding a QB
    }
    // else, QB isn't loaded, so nevermind. Back to regular attack handling
    
    if(item==Items.bow)
      {
      this.tasks.addTask(7, arrowAI);
      }
    else
      {
      this.tasks.addTask(7, collideAI);
      }
    }
  }

@Override
public String getNpcSubType()
  {
  return getSubtypeFromEquipment();
  }

protected String getSubtypeFromEquipment()
  {
  ItemStack stack = getEquipmentInSlot(0);  
  if(stack!=null && stack.getItem()!=null)
    {
    Item item = stack.getItem();
    if(item instanceof ItemSword){return "soldier";}
    else if(item instanceof ItemAxe){return "medic";}
    else if(item instanceof ItemHammer){return "engineer";}
    else if(item==Items.bow){return "archer";}
    else if(item instanceof ItemCommandBaton){return "commander";}
    }
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
  if(tag.hasKey("patrolAI")){patrolAI.readFromNBT(tag.getCompoundTag("patrolAI"));}
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setTag("patrolAI", patrolAI.writeToNBT(new NBTTagCompound()));
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
  {
  // TODO clean this up, increase max attack distance
  
  //TODO get attack damage to use from monster attributes
  
  EntityArrow entityarrow = new EntityArrow(this.worldObj, this, par1EntityLivingBase, 1.6F, (float)(14 - this.worldObj.difficultySetting.getDifficultyId() * 4));
  
  int bonusDamage = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
  int knockBackStrenght = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
  
  entityarrow.setDamage((double)(par2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting.getDifficultyId() * 0.11F));

  if(bonusDamage > 0)
    {
    entityarrow.setDamage(entityarrow.getDamage() + (double)bonusDamage * 0.5D + 0.5D);
    }

  knockBackStrenght/=2;
  if(knockBackStrenght > 0)
    {
    entityarrow.setKnockbackStrength(knockBackStrenght);
    }

  if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0)
    {
    entityarrow.setFire(100);
    }

  this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
  this.worldObj.spawnEntityInWorld(entityarrow);
  }

}
