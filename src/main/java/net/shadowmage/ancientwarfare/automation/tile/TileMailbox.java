package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData.DeliverableItem;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;

public class TileMailbox extends TileEntity implements IOwnable, IInventory, ISidedInventory
{

private boolean autoExport;//should automatically try and export from output side
private boolean privateBox;

public InventorySided inventory;

private String owningPlayerName;
private String mailboxName;
private String destinationName;

public TileMailbox()
  {
  inventory = new InventorySided(this, RotationType.FOUR_WAY, 36);
  int[] topIndices = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17};
  int[] bottomIndices = new int[]{18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35};
  inventory.setAccessibleSideDefault(RelativeSide.TOP, RelativeSide.TOP, topIndices);
  inventory.setAccessibleSideDefault(RelativeSide.BOTTOM, RelativeSide.BOTTOM, bottomIndices);
  }

@Override
public boolean canUpdate()
  {
  return true;
  }

@Override
public void updateEntity()
  {
  if(worldObj.isRemote){return;}
  if(mailboxName!=null)//try to receive mail
    {
    MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, worldObj, MailboxData.class);
    
    List<DeliverableItem> items = new ArrayList<DeliverableItem>();
    data.getDeliverableItems(privateBox? owningPlayerName : null, mailboxName, items, worldObj, xCoord, yCoord, zCoord);
    data.addMailboxReceiver(privateBox? owningPlayerName : null, mailboxName, this);
    
    if(destinationName!=null)//try to send mail
      {
      trySendItems(data);
      }
    }  
  }

private void trySendItems(MailboxData data)
  {
  ItemStack item;
  String owner = privateBox ? owningPlayerName : null;
  int dim = worldObj.provider.dimensionId;
  for(int k = 18; k < 36; k++)
    {
    item = inventory.getStackInSlot(k);
    if(item!=null)
      {
      data.addDeliverableItem(owner, destinationName, item, dim, xCoord, yCoord, zCoord);
      inventory.setInventorySlotContents(k, null);
      break;
      }
    }
  }

@Override
public void setOwnerName(String commandSenderName)
  {
  this.owningPlayerName = commandSenderName;
  }

public String getMailboxName()
  {
  return mailboxName;
  }

public String getTargetName()
  {
  return destinationName;
  }

public void setMailboxName(String name)
  {
  if(worldObj.isRemote){return;}
  mailboxName = name;
  }

public void setTargetName(String name)
  {
  if(worldObj.isRemote){return;}
  destinationName = name;
  }

public boolean isAutoExport(){return autoExport;}
public boolean isPrivateBox(){return privateBox;}
public void setAutoExport(boolean val){autoExport = val;}

public void setPrivateBox(boolean val)
  {
  if(worldObj.isRemote){return;}
  if(val!=privateBox)
    {
    mailboxName = null;
    destinationName = null;
    }
  privateBox = val;
  }

@Override
public String getOwnerName()
  {
  return owningPlayerName;
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  owningPlayerName = tag.getString("ownerName");
  if(tag.hasKey("targetName")){destinationName = tag.getString("targetName");}
  if(tag.hasKey("mailboxName")){mailboxName = tag.getString("mailboxName");}
  if(tag.hasKey("inventory")){inventory.readFromNBT(tag.getCompoundTag("inventory"));}  
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setString("ownerName", owningPlayerName);
  if(destinationName!=null){tag.setString("targetName", destinationName);}
  if(mailboxName!=null){tag.setString("mailboxName", mailboxName);}
  
  NBTTagCompound tag1 = new NBTTagCompound();
  inventory.writeToNBT(tag1);
  tag.setTag("inventory", tag1);
  }

@Override
public int[] getAccessibleSlotsFromSide(int p_94128_1_)
  {
  return inventory.getAccessibleSlotsFromSide(p_94128_1_);
  }

@Override
public boolean canInsertItem(int slot, ItemStack stack,int side)
  {
  return inventory.canInsertItem(slot, stack, side);
  }

@Override
public boolean canExtractItem(int slot, ItemStack stack, int side)
  {
  return inventory.canExtractItem(slot, stack, side);
  }

@Override
public int getSizeInventory()
  {
  return inventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int slot)
  {
  return inventory.getStackInSlot(slot);
  }

@Override
public ItemStack decrStackSize(int slot, int amt)
  {
  return inventory.decrStackSize(slot, amt);
  }

@Override
public ItemStack getStackInSlotOnClosing(int slot)
  {
  return inventory.getStackInSlotOnClosing(slot);
  }

@Override
public void setInventorySlotContents(int slot, ItemStack stack)
  {
  inventory.setInventorySlotContents(slot, stack);
  }

@Override
public String getInventoryName()
  {
  return inventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return inventory.hasCustomInventoryName();
  }

@Override
public int getInventoryStackLimit()
  {
  return inventory.getInventoryStackLimit();
  }

@Override
public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
  {
  return inventory.isUseableByPlayer(p_70300_1_);
  }

@Override
public void openInventory(){}

@Override
public void closeInventory(){}

@Override
public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
  {
  return inventory.isItemValidForSlot(p_94041_1_, p_94041_2_);
  }

}
