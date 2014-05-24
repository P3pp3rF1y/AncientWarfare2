package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWork;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public class NpcWorker extends NpcPlayerOwned implements IWorker
{

//protected WorkOrder workOrders;

private NpcAIWork workAI;

public NpcWorker(World par1World)
  {
  super(par1World);  
  //this should be set to a generic 'flee' AI for civilians
  this.tasks.addTask(3, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
  //get food
  //idle
  this.tasks.addTask(6, new EntityAIMoveIndoors(this));
  this.tasks.addTask(7, (workAI = new NpcAIWork(this)));
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
    else if(stack.getItem() == AWItems.automationHammer){return WorkType.CRAFTING;}//TODO move hammer into core as it is used by multiple modules
    //TODO add researcher custom item -- quill?
    //else if(stack.getItem() == AWItems.researchQuil){return WorkType.RESEARCH}
    }
  return WorkType.NONE;
  }

@Override
public void onOrdersInventoryChanged()
  {
  this.workAI.onOrdersChanged();
  }

}
