package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.item.ItemHammer;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertPlayerOwned;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandGuard;
import net.shadowmage.ancientwarfare.npc.ai.NpcAICommandMove;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFindWorksite;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFleeHostiles;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWork;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWorkRandom;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class NpcWorker extends NpcPlayerOwned implements IWorker
{

public BlockPosition autoWorkTarget;
private NpcAIWork workAI;
private NpcAIWorkRandom workRandomAI;

public NpcWorker(World par1World)
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
  this.tasks.addTask(6, (workAI = new NpcAIWork(this)));
  this.tasks.addTask(7, (workRandomAI = new NpcAIWorkRandom(this)));
  this.tasks.addTask(8, new NpcAIMoveHome(this, 80.f, 8.f, 40.f, 3.f));
  
  //post-100 -- used by delayed shared tasks (look at random stuff, wander)
  this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
  this.tasks.addTask(102, new NpcAIWander(this, 0.625D));
  this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
  
  this.targetTasks.addTask(0, new NpcAIFindWorksite(this));
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

public void handleWorksiteBroadcast(IWorkSite site, BlockPosition pos)
  {
  
  }

@Override
public String getNpcType()
  {
  return "worker";
  }

@Override
public float getWorkEffectiveness(WorkType type)
  {
  float effectiveness=1.f;
  if(type==this.getWorkTypeFromEquipment())
    {
    float level = this.getLevelingStats().getLevel(getNpcFullType());
    
    effectiveness += level*0.05f;    
    if(getEquipmentInSlot(0)==null){return effectiveness;}
    Item item = getEquipmentInSlot(0).getItem();    
    if(item instanceof ItemTool)//TODO handle hoe item
      {
      ItemTool tool = (ItemTool)getEquipmentInSlot(0).getItem();
      effectiveness += tool.func_150913_i().getEfficiencyOnProperMaterial()*0.05f;
      }    
    else if(item instanceof ItemHammer)
      {
      ItemHammer hammer = (ItemHammer)item;
      effectiveness += hammer.getMaterial().getEfficiencyOnProperMaterial()*0.05f;
      }
    else if(getEquipmentInSlot(0)!=null)
      {
      effectiveness += level*0.05f;
      }        
    }
  else{effectiveness=0.f;}
  AWLog.logDebug("getting worker effectiveness for: "+this.getNpcFullType()+" :: "+effectiveness);
  return effectiveness;
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
    else if(stack.getItem() instanceof ItemHammer){return WorkType.CRAFTING;}
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
  if(tag.hasKey("workRandomAI")){workRandomAI.readFromNBT(tag.getCompoundTag("workRandomAI"));}
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {
  super.writeEntityToNBT(tag);
  tag.setTag("workAI", workAI.writeToNBT(new NBTTagCompound()));
  tag.setTag("workRandomAI", workRandomAI.writeToNBT(new NBTTagCompound()));
  }

}
