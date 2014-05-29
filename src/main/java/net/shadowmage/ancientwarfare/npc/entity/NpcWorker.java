package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWork;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class NpcWorker extends NpcPlayerOwned implements IWorker
{

//protected WorkOrder workOrders;

private NpcAIWork workAI;

public NpcWorker(World par1World)
  {
  super(par1World);    
  this.tasks.addTask(0, new EntityAISwimming(this));
  this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
  this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
  
  this.tasks.addTask(2, new NpcAIFollowPlayer(this));
  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 1.0D));
  this.tasks.addTask(4, new NpcAIGetFood(this));  
  this.tasks.addTask(5, new NpcAIIdleWhenHungry(this)); 
  this.tasks.addTask(6, (workAI = new NpcAIWork(this)));
  this.tasks.addTask(7, new NpcAIMoveHome(this, 80.f, 8.f, 40.f, 3.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  }

@Override
public String getNpcSubType()
  {
  WorkType type = getWorkTypeFromEquipment();
  switch(type)
  {
  case CRAFTING:
  return "craftsman";
  case FARMING:
  return "farmer";
  case FORESTRY:
  return "lumberjack";
  case MINING:
  return "miner";
  case RESEARCH:
  return "researcher";
  case NONE:
  default:
  return "";  
  }
  }

@Override
public String getNpcType()
  {
  return "worker";
  }

@Override
public float getWorkEffectiveness(WorkType type)
  {
  return 1.f;//TODO base this off of worker level?
  }

@Override
public boolean canWorkAt(WorkType type)
  {
  return type==getWorkTypeFromEquipment();
  }

@Override
public Team getWorkerTeam()
  {
  return getTeam();
  }

@Override
public boolean isValidOrdersStack(ItemStack stack)
  {
  return stack!=null && stack.getItem()==AWNpcItemLoader.workOrder;
  }

protected WorkType getWorkTypeFromEquipment()
  {
  ItemStack stack = getEquipmentInSlot(0);
  if(stack!=null && stack.getItem()!=null)
    {
    if(stack.getItem() instanceof ItemHoe){return WorkType.FARMING;}
    else if(stack.getItem() instanceof ItemAxe){return WorkType.FORESTRY;}
    else if(stack.getItem() instanceof ItemPickaxe){return WorkType.MINING;}    
    else if(stack.getItem() == AWItems.automationHammer){return WorkType.CRAFTING;}
    //else if(stack.getItem() == AWItems.researchQuil){return WorkType.RESEARCH}//TODO add researcher custom item -- quill?
    }
  return WorkType.NONE;
  }

@Override
public void onOrdersInventoryChanged()
  {
  this.workAI.onOrdersChanged();
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  if(tag.hasKey("workAI")){workAI.readFromNBT(tag.getCompoundTag("workAI"));}
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setTag("workAI", workAI.writeToNBT(new NBTTagCompound()));
  }

}
