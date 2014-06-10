package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIArrowAttack;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertFaction;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFindCommanderFaction;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorseFaction;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;

public abstract class NpcFactionMountedArcher extends NpcFactionMounted implements IRangedAttackMob
{

public NpcFactionMountedArcher(World par1World)
  {
  super(par1World); 
  this.setCurrentItemOrArmor(0, new ItemStack(Items.bow));
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
  this.tasks.addTask(0, (horseAI=new NpcAIRideHorseFaction(this)));
  this.tasks.addTask(1, new NpcAIFindCommanderFaction(this));  
  this.tasks.addTask(1, (alertAI = new NpcAIAlertFaction(this)));
  this.tasks.addTask(1, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAIMoveHome(this, 80.f, 20.f, 40.f, 5.f)); 
  this.tasks.addTask(3, new EntityAIArrowAttack(this, 1.0D, 20, 60, 15.0F));
  
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));   
      
  this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
  this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityLivingBase.class, 0, true, false, selector));
  }

}
