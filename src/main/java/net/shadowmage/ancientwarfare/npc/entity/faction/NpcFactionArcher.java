package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.command.IEntitySelector;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertFaction;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFactionRangedAttack;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFindCommanderFaction;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIStayAtHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;

public abstract class NpcFactionArcher extends NpcFaction implements IRangedAttackMob
{

public NpcFactionArcher(World par1World)
  {
  super(par1World);  
  IEntitySelector selector = new IEntitySelector()
    {
    @Override
    public boolean isEntityApplicable(Entity entity)
      {
      if(!isHostileTowards(entity)){return false;}
      if(hasHome())
        {
        ChunkCoordinates home = getHomePosition();
        double dist = entity.getDistanceSq(home.posX+0.5d, home.posY, home.posZ+0.5d);
        if(dist>30*30){return false;}
        }
      return true;
      }    
    };
    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(1, new NpcAIFindCommanderFaction(this));  
  this.tasks.addTask(1, (alertAI = new NpcAIAlertFaction(this)));
  this.tasks.addTask(1, new NpcAIFollowPlayer(this));
//  this.tasks.addTask(2, new NpcAIMoveHome(this, 50.f, 5.f, 30.f, 5.f)); 
  this.tasks.addTask(2, new NpcAIStayAtHome(this));
  this.tasks.addTask(3, new NpcAIFactionRangedAttack(this));
  
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));   
      
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }

@Override
public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2)
  {
  AWLog.logDebug("Executing NPC Ranged attack.  Target: "+par1EntityLivingBase);
  AWLog.logDebug("Target Info:  dead: "+par1EntityLivingBase.isDead+" loaded in world list: "+par1EntityLivingBase.worldObj.getLoadedEntityList().contains(par1EntityLivingBase));
  AWLog.logDebug("Called from: ");
//  new Exception().printStackTrace();
  // TODO clean this up, increase max attack distance
  
  //TODO get attack damage to use from monster attributes
  
  EntityArrow entityarrow = new EntityArrow(this.worldObj, this, par1EntityLivingBase, 1.6F, (float)(14 - this.worldObj.difficultySetting.getDifficultyId() * 4));
  
  int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
  int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
  
  entityarrow.setDamage((double)(par2 * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.difficultySetting.getDifficultyId() * 0.11F));

  if(i > 0)
    {
    entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
    }

  if(j > 0)
    {
    entityarrow.setKnockbackStrength(j);
    }

  if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0)
    {
    entityarrow.setFire(100);
    }

  this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
  this.worldObj.spawnEntityInWorld(entityarrow);
  }

}
