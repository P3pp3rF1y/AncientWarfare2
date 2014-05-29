package net.shadowmage.ancientwarfare.npc.tile;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.NpcCombat;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;

public class TileTownHall extends TileEntity implements IOwnable
{

String ownerName = "";
int broadcastRange = 80;
int updateDelayTicks = 0;
int updateDelayMaxTicks = 20*5;//5 second broadcast frequency

int recentAlertTimer = 0;
int recentAlertMaxTicks = 10*5;

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  updateDelayTicks--;
  if(updateDelayTicks<=0)
    {
    broadcast();
    updateDelayTicks = updateDelayMaxTicks;
    }
  recentAlertTimer--;
  }

private void broadcast()
  {
  AWLog.logDebug("broadcasting from town hall tile...");
  List<NpcPlayerOwned> npcs = getNpcsInArea();
  BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
  for(NpcPlayerOwned npc : npcs)
    {
    if(npc.canBeCommandedBy(getOwnerName()))
      {
      AWLog.logDebug("xmit to entity: "+npc);
      npc.handleTownHallBroadcast(this, pos);      
      }
    else
      {
      AWLog.logDebug("npc cannot be commanded by owner..: "+npc.getOwnerName()+" this: "+getOwnerName());
      }
    }
  }

public void handleAlert(BlockPosition pos)
  {
  
  }

public void handleNpcCombatAlert(NpcBase npc, Entity target)
  {
  AWLog.logDebug("receiving combat alert from: "+npc +" at: "+target);
  if(recentAlertTimer<=0)
    {
    Command cmd = new Command(CommandType.ATTACK, target.getEntityId());
    Command cmd2 = new Command(CommandType.MOVE, xCoord, yCoord, zCoord);
    List<NpcPlayerOwned> npcs = getNpcsInArea();
    for(NpcPlayerOwned npc1 : npcs)
      {
      if(npc1==npc){continue;}//do not issue orders to alerting npc
      if(!npc1.canBeCommandedBy(getOwnerName())){continue;}
      if(npc1 instanceof NpcCombat)
        {
        AWLog.logDebug("commanding npc: "+npc1+" to attack target: "+cmd);
        npc1.setCurrentCommand(cmd);
        }
      else
        {
        AWLog.logDebug("commanding npc: "+npc1+" to move to town hall:: "+cmd2);
        npc1.setCurrentCommand(cmd2);
        }
      }
    recentAlertTimer = recentAlertMaxTicks;
    }
  }

private List<NpcPlayerOwned> getNpcsInArea()
  {
  AxisAlignedBB bb = AxisAlignedBB.getAABBPool().getAABB(xCoord-broadcastRange, yCoord-broadcastRange/2, zCoord-broadcastRange, xCoord+broadcastRange+1, yCoord+broadcastRange/2+1, zCoord+broadcastRange+1);
  List<NpcPlayerOwned> npcs = worldObj.getEntitiesWithinAABB(NpcPlayerOwned.class, bb);
  return npcs;
  }

@Override
public void setOwnerName(String name)
  {
  if(name==null){name="";}
  this.ownerName = name;
  }

@Override
public String getOwnerName()
  {
  return ownerName;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  ownerName = tag.getString("owner");
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("owner", ownerName);
  }


}
