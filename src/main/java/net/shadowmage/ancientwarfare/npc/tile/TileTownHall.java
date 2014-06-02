package net.shadowmage.ancientwarfare.npc.tile;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.entity.NpcPlayerOwned;

public class TileTownHall extends TileEntity implements IOwnable, IInventory
{

String ownerName = "";
int broadcastRange = 80;
int updateDelayTicks = 0;
int updateDelayMaxTicks = 20*5;//5 second broadcast frequency

InventoryBasic inventory = new InventoryBasic(27);

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
  }

private void broadcast()
  {
  List<NpcPlayerOwned> npcs = getNpcsInArea();
  BlockPosition pos = new BlockPosition(xCoord, yCoord, zCoord);
  for(NpcPlayerOwned npc : npcs)
    {
    if(npc.canBeCommandedBy(getOwnerName()))
      {
      npc.handleTownHallBroadcast(this, pos);      
      }
    }
  }

public void handleNpcDeath(NpcPlayerOwned npc, DamageSource source)
  {
  
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
  //TODO read inventory
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("owner", ownerName);
  //TODO write inventory
  }

@Override
public int getSizeInventory(){return inventory.getSizeInventory();}

@Override
public ItemStack getStackInSlot(int var1){return inventory.getStackInSlot(var1);}

@Override
public ItemStack decrStackSize(int var1, int var2){return inventory.decrStackSize(var1, var2);}

@Override
public ItemStack getStackInSlotOnClosing(int var1){return inventory.getStackInSlotOnClosing(var1);}

@Override
public void setInventorySlotContents(int var1, ItemStack var2){inventory.setInventorySlotContents(var1, var2);}

@Override
public String getInventoryName(){return inventory.getInventoryName();}

@Override
public boolean hasCustomInventoryName(){return inventory.hasCustomInventoryName();}

@Override
public int getInventoryStackLimit(){return inventory.getInventoryStackLimit();}

@Override
public boolean isUseableByPlayer(EntityPlayer var1){return true;}

@Override
public void openInventory(){}

@Override
public void closeInventory(){}

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2){return inventory.isItemValidForSlot(var1, var2);}

public static class NpcDeathEntry
{
ItemStack stackToSpawn;
String npcType;
String deathCause;
}

}
