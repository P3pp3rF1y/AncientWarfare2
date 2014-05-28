package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;

public abstract class NpcPlayerOwned extends NpcBase
{

private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;

public NpcPlayerOwned(World par1World)
  {
  super(par1World);
  }

@Override
public Command getCurrentCommand()
  {
  return playerIssuedCommand;
  }

@Override
public void setCurrentCommand(Command cmd)
  {  
  if(cmd.type==CommandType.ATTACK || cmd.type==CommandType.ATTACK_AREA || cmd.type==CommandType.GUARD || cmd.type==CommandType.MOVE)
    {
    this.playerIssuedCommand=cmd;    
    }
  else if(cmd.type==CommandType.SET_HOME)
    {
    setHomeArea(cmd.x, cmd.y, cmd.z, 40);
    }
  else if(cmd.type==CommandType.SET_UPKEEP)
    {
    UpkeepOrder orders = UpkeepOrder.getUpkeepOrder(upkeepStack);
    if(orders!=null)
      {
      orders.addUpkeepPosition(worldObj, new BlockPosition(cmd.x, cmd.y, cmd.z));
      orders.setUpkeepAmount(6000);//TODO set from config or baton GUI somehow??
      UpkeepOrder.writeUpkeepOrder(upkeepStack, orders);
      AWLog.logDebug("set upkeep position for npc from command...: "+orders);
      }
    }
  else if(cmd.type==CommandType.CLEAR_HOME)
    {
    detachHome();
    }
  else if(cmd.type==CommandType.CLEAR_UPKEEP)
    {
    UpkeepOrder orders = UpkeepOrder.getUpkeepOrder(upkeepStack);
    if(orders!=null)
      {
      orders.removeUpkeepPoint();
      UpkeepOrder.writeUpkeepOrder(upkeepStack, orders);
      }    
    }
  else if(cmd.type==CommandType.CLEAR_COMMAND)
    {
    this.playerIssuedCommand = null;
    }
  }

@Override
public void onWeaponInventoryChanged()
  {
  updateTexture();
  }

@Override
public int getFoodRemaining()
  {
  return foodValueRemaining;
  }

@Override
public void setFoodRemaining(int food)
  {
  this.foodValueRemaining = food;
  }

@Override
public BlockPosition getUpkeepPoint()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepPosition();
    }
  return null;
  }

public int getUpkeepBlockSide()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepBlockSide();
    }
  return 0;
  }

@Override
public int getUpkeepDimensionId()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepDimension();
    }
  return 0;
  }

@Override
public int getUpkeepAmount()
  {
  UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
  if(order!=null)
    {
    return order.getUpkeepAmount();
    }
  return 0;
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
public void onLivingUpdate()
  {  
  super.onLivingUpdate();
  if(foodValueRemaining>0){foodValueRemaining--;}
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  if(tag.hasKey("command")){playerIssuedCommand = new Command(tag.getCompoundTag("command"));}  
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  if(playerIssuedCommand!=null){tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));}
  }

@Override
public void readAdditionalItemData(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

@Override
public void writeAdditionalItemData(NBTTagCompound tag)
  {
  // TODO Auto-generated method stub
  }

}
