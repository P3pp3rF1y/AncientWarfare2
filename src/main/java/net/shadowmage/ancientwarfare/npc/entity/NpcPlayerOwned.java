package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIGetFood;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

public abstract class NpcPlayerOwned extends NpcBase
{

private int foodValueRemaining = 0;

public NpcPlayerOwned(World par1World)
  {
  super(par1World);  
  //3 should be flee hostiles when low-health (or based on morale check?)
  this.tasks.addTask(4, new NpcAIGetFood(this));  
//  this.tasks.addTask(5, new NpcAIIdleWhenHungry(this));  
  }

@Override
public void onUpkeepInventoryChanged()
  {
  //should inform upkeep AI about upkeep point change
  }

@Override
public ItemStack getEquipmentInSlot(int par1)
  {
  return super.getEquipmentInSlot(par1);
  }

@Override
public int getFoodRemaining()
  {
  return foodValueRemaining;
  }

@Override
public BlockPosition getUpkeepPoint()
  {
  return upkeepStack==null ? null : AWNpcItemLoader.upkeepOrder.getOrders(upkeepStack).getUpkeepPosition();
  }

public int getUpkeepBlockSide()
  {
  int side = 0;
  if(upkeepStack!=null)
    {
    side = AWNpcItemLoader.upkeepOrder.getOrders(upkeepStack).getUpkeepBlockSide();    
    }
  return side;
  }

@Override
public int getUpkeepDimensionId()
  {
  return upkeepStack==null ? 0 : AWNpcItemLoader.upkeepOrder.getOrders(upkeepStack).getUpkeepDimension();
  }

@Override
public boolean requiresUpkeep()
  {
  return true;
  }

@Override
protected boolean interact(EntityPlayer par1EntityPlayer)
  {
  if(par1EntityPlayer.worldObj.isRemote){return false;}
  Team t = par1EntityPlayer.getTeam();
  Team t1 = getTeam();
  if(t==t1)
    {
    if(par1EntityPlayer.isSneaking())
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
      }
    else
      {
      NetworkHandler.INSTANCE.openGui(par1EntityPlayer, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);
      }
    return true;
    }
  return true;
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  }

@Override
public void writeSpawnData(ByteBuf buffer)
  {
  
  }

@Override
public void readSpawnData(ByteBuf additionalData)
  {
  
  }

@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

@Override
public void writeAddtionalItemData(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

}
