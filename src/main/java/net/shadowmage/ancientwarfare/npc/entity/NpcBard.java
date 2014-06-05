package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertPlayerOwned;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIBard;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandGuard;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandMove;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;

public class NpcBard extends NpcPlayerOwned
{

/**
 * bard tune playing stats.
 * server-only -- need synched to client in tune-select GUI
 */
public int bardTuneNumber;
public int bardPlayLength;//num of ticks for the tune
public int bardPlayChance;//0-100, chance out of 100 to play a tune
public int bardPlayRecheckDelay;//how many ticks should pass between rechecking the play-delay?

public NpcBard(World par1World)
  {
  super(par1World);
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  this.tasks.addTask(0, (horseAI=new NpcAIRideHorse(this)));
  this.tasks.addTask(1, (alertAI=new NpcAIAlertPlayerOwned(this)));  
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(2, new NpcAICommandGuard(this));
  this.tasks.addTask(2, new NpcAICommandMove(this));
  this.tasks.addTask(3, new NpcAIFleeHostiles(this));
  this.tasks.addTask(4, new NpcAIGetFood(this));  
  this.tasks.addTask(5, new NpcAIIdleWhenHungry(this)); 
  
  this.tasks.addTask(7, new NpcAIMoveHome(this, 80.f, 8.f, 40.f, 3.f));
  this.tasks.addTask(8, new NpcAIBard(this));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return false;
  }

@Override
public void onOrdersInventoryChanged()
  {
  }

@Override
public String getNpcSubType()
  {
  return "";
  }

@Override
public String getNpcType()
  {
  return "bard";
  }

@Override
public boolean hasAltGui()
  {
  return true;
  }

@Override
public void openAltGui(EntityPlayer player)
  {
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_BARD, getEntityId(), 0, 0);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {
  super.readEntityFromNBT(tag);
  bardTuneNumber = tag.getInteger("bardTuneNumber");
  bardPlayLength = tag.getInteger("bardPlayLength");
  bardPlayChance = tag.getInteger("bardPlayChance");
  bardPlayRecheckDelay = tag.getInteger("bardPlayRecheckDelay");  
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setInteger("bardTuneNumber", bardTuneNumber);  
  tag.setInteger("bardPlayLength", bardPlayLength);
  tag.setInteger("bardPlayChance", bardPlayChance);
  tag.setInteger("bardPlayRecheckDelay", bardPlayRecheckDelay);
  }

}
