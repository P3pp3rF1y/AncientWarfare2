package net.shadowmage.ancientwarfare.npc.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAlertPlayerOwned;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public abstract class NpcPlayerOwned extends NpcBase
{

private Command playerIssuedCommand;//TODO load/save
private int foodValueRemaining = 0;
protected NpcAIAlertPlayerOwned alertAI;

private BlockPosition townHallPosition;

public NpcPlayerOwned(World par1World)
  {
  super(par1World);
  }

@Override
public void handleAlertBroadcast(NpcBase broadcaster, EntityLivingBase target)
  {
  if(alertAI!=null)
    {
    alertAI.handleAlert(broadcaster, target);
    }  
  }

@Override
public void setTownHallPosition(BlockPosition pos)
  {
  AWLog.logDebug("setting town hall position to: "+pos);
  if(pos!=null){this.townHallPosition = pos.copy();}
  else{this.townHallPosition=null;}
  }

@Override
public BlockPosition getTownHallPosition()
  {
  return townHallPosition;
  }

@Override
public TileTownHall getTownHall()
  {
  if(getTownHallPosition()!=null)    
    {
    BlockPosition pos = getTownHallPosition();
    TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
    if(te instanceof TileTownHall)
      {
      return (TileTownHall)te;
      }
    }
  return null;
  }

@Override
public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position)
  {
  validateTownHallPosition();
  if(getTownHallPosition()!=null)
    {
    BlockPosition pos = getTownHallPosition();
    double curDist = getDistanceSq(pos.x+0.5d, pos.y, pos.z+0.5d);
    double newDist = getDistanceSq(position.x+0.5d, position.y, position.z+0.5d);
    if(newDist<curDist)
      {
      setTownHallPosition(position);
      }
    }
  else
    {
    setTownHallPosition(position);
    }
  }

private boolean validateTownHallPosition()
  {
  if(getTownHallPosition()==null){return false;}  
  BlockPosition pos = getTownHallPosition();
  if(!worldObj.blockExists(pos.x, pos.y, pos.z)){return true;}//cannot validate, unloaded...assume good 
  TileEntity te = worldObj.getTileEntity(pos.x, pos.y, pos.z);
  if(te instanceof TileTownHall)
    {
    if(canBeCommandedBy(((TileTownHall) te).getOwnerName()))
      {
      return true;
      }
    }
  setTownHallPosition(null);
  return false;
  }

@Override
public Command getCurrentCommand()
  {
  return playerIssuedCommand;
  }

@Override
public void handlePlayerCommand(Command cmd)
  {  
  if(cmd==null)
    {
    this.playerIssuedCommand=null;
    }
  else if(cmd.type==CommandType.ATTACK || cmd.type==CommandType.ATTACK_AREA || cmd.type==CommandType.GUARD || cmd.type==CommandType.MOVE)
    {
    this.playerIssuedCommand=cmd;    
    AWLog.logDebug("set player issued command to: "+cmd);
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
    AWLog.logDebug("clearing home from player-issued command!");
    }
  else if(cmd.type==CommandType.CLEAR_UPKEEP)
    {
    UpkeepOrder orders = UpkeepOrder.getUpkeepOrder(upkeepStack);
    if(orders!=null)
      {
      orders.removeUpkeepPoint();
      UpkeepOrder.writeUpkeepOrder(upkeepStack, orders);
      AWLog.logDebug("clearing upkeep from player-issued command!");
      }    
    }
  else if(cmd.type==CommandType.CLEAR_COMMAND)
    {
    this.playerIssuedCommand = null;
    AWLog.logDebug("clearing player-issued commands!!");
    }
  }

@Override
public boolean isHostileTowards(Entity e)
  {
  if(e instanceof NpcPlayerOwned)
    {
    NpcPlayerOwned npc = (NpcPlayerOwned)e;
    Team t = npc.getTeam();
    return isHostileTowards(t);
    }
  else if(e instanceof NpcFaction)
    {
    NpcFaction npc = (NpcFaction)e;
    return npc.isHostileTowards(this);//cheap trick to determine if should be hostile or not
    }
  else if(e instanceof EntityPlayer)
    {
    Team t = worldObj.getScoreboard().getPlayersTeam(e.getCommandSenderName());
    return isHostileTowards(t);
    }
  else
    {
    String n = EntityList.getEntityString(e);
    List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
    if(targets.contains(n))
      {
      return true;
      }
    }
  return false;
  }

protected boolean isHostileTowards(Team team)
  {
  Team a = getTeam();
  if(a!=null && team!=null && a!=team){return true;}
  return false;
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

@Override
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
    if(par1EntityPlayer.isSneaking() && this.canBeCommandedBy(par1EntityPlayer.getCommandSenderName()))
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
public void travelToDimension(int par1)
  {
  this.townHallPosition=null;
  super.travelToDimension(par1);
  }

@Override
public void readEntityFromNBT(NBTTagCompound tag)
  {  
  super.readEntityFromNBT(tag);
  foodValueRemaining = tag.getInteger("foodValue");
  if(tag.hasKey("command")){playerIssuedCommand = new Command(tag.getCompoundTag("command"));} 
  if(tag.hasKey("townHall")){townHallPosition = new BlockPosition(tag.getCompoundTag("townHall"));}
  onWeaponInventoryChanged();
  }

@Override
public void writeEntityToNBT(NBTTagCompound tag)
  {  
  super.writeEntityToNBT(tag);
  tag.setInteger("foodValue", foodValueRemaining);
  if(playerIssuedCommand!=null){tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));}
  if(townHallPosition!=null){tag.setTag("townHall", townHallPosition.writeToNBT(new NBTTagCompound()));}
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
