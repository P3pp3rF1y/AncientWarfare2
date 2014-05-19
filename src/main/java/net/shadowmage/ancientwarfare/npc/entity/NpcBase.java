package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
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
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable
{

/**
 * user-set name for this NPC -- set via name tag items or other means
 */
private String npcName = "";

/**
 * the owner of this NPC, used for checking teams
 * for HOSTILE mobs, this will="AWHOSTILE", and be unchangeable
 */
private String ownerName = "";//the owner of this NPC, used for checking teams

private String followingPlayerName;//set/cleared onInteract from player if player.team==this.team

private int foodValueRemaining = -1;//set to -1 to disable upkeep (used by hostile npcs, others)

private int upkeepDimensionId;
private BlockPosition upkeepPoint;

public NpcBase(World par1World)
  {
  super(par1World);
  this.getNavigator().setBreakDoors(true);
  this.getNavigator().setAvoidsWater(true);
  this.tasks.addTask(0, new EntityAISwimming(this));
//  this.tasks.addTask(1, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
  //1 reserved for attack task for soldier npcs, unused for civilian types
  this.tasks.addTask(2, new NpcAIFollowPlayer(this, 1.d, 10.f, 2.f));
  this.tasks.addTask(3, new NpcAIGetFood(this));
  this.tasks.addTask(4, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));//this should be set to a generic 'flee' AI for civilians
//  this.tasks.addTask(5, new NpcAIIdleWhenHungry(this));
  this.tasks.addTask(6, new EntityAIMoveIndoors(this));
  this.tasks.addTask(7, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(8, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(9, new EntityAIMoveTowardsRestriction(this, 0.6D));  
  this.tasks.addTask(10, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(11, new EntityAIWander(this, 1.0D));
  this.tasks.addTask(12, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  
  //target tasks
//  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
//  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));
  }

@Override
protected boolean canDespawn()
  {
  return false;
  }

@Override
protected boolean isAIEnabled()
  {
  return true;
  }

@Override
protected void applyEntityAttributes()
  {
  super.applyEntityAttributes();
  this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.d);
  this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0D);
  this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.23000000417232513D);
  this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
  this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(3.0D);  
  }

public int getFoodRemaining()
  {
  return foodValueRemaining;
  }

public BlockPosition getUpkeepPoint()
  {
  return upkeepPoint;
  }

public int getUpkeepDimensionId()
  {
  return upkeepDimensionId;
  }

@Override
public void setOwnerName(String name)
  {
  ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public Team getTeam()
  {
  return worldObj.getScoreboard().getPlayersTeam(ownerName);
  }

public EntityLivingBase getFollowingEntity()
  {
  if(followingPlayerName==null){return null;}
  return worldObj.getPlayerEntityByName(followingPlayerName);
  }

public void setFollowingEntity(EntityLivingBase entity)
  {
  if(entity instanceof EntityPlayer)
    {
    this.followingPlayerName = entity.getCommandSenderName();        
    }
  }

@Override
public boolean allowLeashing()
  {
  return false;
  }

@Override
protected boolean interact(EntityPlayer par1EntityPlayer)
  {
  if(par1EntityPlayer.worldObj.isRemote){return false;}
  Team t = par1EntityPlayer.getTeam();
  Team t1 = getTeam();
  if(t==t1)
    {
    if(this.followingPlayerName==null)
      {
      this.followingPlayerName = par1EntityPlayer.getCommandSenderName();
      AWLog.logDebug("set following player name to: "+this.followingPlayerName);      
      }
    else if(this.followingPlayerName.equals(par1EntityPlayer.getCommandSenderName()))
      {
      this.followingPlayerName = null;
      AWLog.logDebug("set following player name to: "+this.followingPlayerName);  
      }
    else
      {
      this.followingPlayerName = par1EntityPlayer.getCommandSenderName();   
      AWLog.logDebug("set following player name to: "+this.followingPlayerName);     
      }
    return true;
    }
  return true;
  }

public void readAdditionalItemData(NBTTagCompound tag)
  {
//TODO should be implemented by subclasses to read in the inventory/etc saved into item NBT from an NPC repack
  }

public void repackEntity(EntityPlayer player)
  {
  //TODO repack into an item-stack, attempt to merge into playe inventory else drop into world.
  }

public ItemStack getItemToSpawn()
  {
  return null;//TODO
  }

@Override
public void readEntityFromNBT(NBTTagCompound par1nbtTagCompound)
  {
//TODO
  super.readEntityFromNBT(par1nbtTagCompound);
  }

@Override
public void writeEntityToNBT(NBTTagCompound par1nbtTagCompound)
  {
//TODO
  super.writeEntityToNBT(par1nbtTagCompound);
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {
//TODO
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
//TODO
  }

}
