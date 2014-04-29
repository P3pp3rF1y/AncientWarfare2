package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData.DeliverableItem;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.inventory.ISidedTile;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class TileMailbox extends TileEntity implements ISidedTile
{

private boolean autoExport;//should automatically try and export from output side
private boolean privateBox;

public InventorySided inventory;

private String owningPlayerName;
private String mailboxName;
private String destinationName;

public TileMailbox()
  {
  inventory = new InventorySided(36, this);
  
  this.inventory.addSlotViewMap(InventorySide.TOP, 8, 9, "guistrings.inventory.side.top");
  for(int i =0; i <18; i++)
    {
    this.inventory.addSidedMapping(InventorySide.TOP, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.TOP, i, (i%9)*18, (i/9)*18);
    }
    
  this.inventory.addSlotViewMap(InventorySide.BOTTOM, 8, (2*18)+12+9, "guistrings.inventory.side.bottom");
  for(int i = 18, k = 0; i<36; i++, k++)
    {
    this.inventory.addSidedMapping(InventorySide.BOTTOM, i, true, true);
    this.inventory.addSlotViewMapping(InventorySide.BOTTOM, i, (k%9)*18, (k/9)*18);
    }
  }

public void checkOutputDirection()
  {
  int check = RelativeSide.getAccessDirection(RelativeSide.REAR, getTileMeta());
  AWLog.logDebug("checking output direction for rear: "+check + " :: "+ForgeDirection.getOrientation(check));
  }

@Override
public int getTileMeta()
  {
  return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
  }

@Override
public void onInventoryChanged()
  {
  
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
    tryReceiveItems(data, items);
    
    if(destinationName!=null)//try to send mail
      {
      trySendItems(data);
      }
    }  
  }

private void tryReceiveItems(MailboxData data, List<DeliverableItem> items)
  {  
  ItemStack item;
  String owner = privateBox ? owningPlayerName : null;
  for(DeliverableItem deliverable : items)
    {
    item = deliverable.item;
    item = InventoryTools.mergeItemStack(inventory, item, inventory.getAccessDirectionFor(InventorySide.TOP));
    if(item==null)
      {
      data.removeDeliverableItem(owner, mailboxName, deliverable);
      }
    break;
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

public void setOwningPlayer(String commandSenderName)
  {
  if(worldObj.isRemote){return;}
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
  AWLog.logDebug("set mailbox name to: "+name);
  }

public void setTargetName(String name)
  {
  if(worldObj.isRemote){return;}
  destinationName = name;
  AWLog.logDebug("set target name to: "+name);
  }

public boolean isAutoExport(){return autoExport;}
public boolean isPrivateBox(){return privateBox;}
public void setAutoExport(boolean val){autoExport = val;}

public void setPrivateBox(boolean val)
  {
  if(worldObj.isRemote){return;}
  AWLog.logDebug("setting private box: "+val);
  if(val!=privateBox)
    {
    mailboxName = null;
    destinationName = null;
    }
  privateBox = val;
  }

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

}
