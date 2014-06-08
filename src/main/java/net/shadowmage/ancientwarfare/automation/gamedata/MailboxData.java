package net.shadowmage.ancientwarfare.automation.gamedata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class MailboxData extends WorldSavedData
{

public static final String name = "AWMailboxData";

private MailboxSet publicMailboxes = new MailboxSet("public");
private HashMap<String, MailboxSet> privateMailboxes = new HashMap<String, MailboxSet>();

public MailboxData(String par1Str)
  {
  super(par1Str);
  }

public MailboxData()
  {
  super(name);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {  
  publicMailboxes = new MailboxSet("public");
  publicMailboxes.readFromNBT(tag.getCompoundTag("publicBoxes"));
  
  privateMailboxes.clear();
  NBTTagList privateBoxList = tag.getTagList("privateBoxes", Constants.NBT.TAG_COMPOUND);
  MailboxSet boxSet;
  for(int i = 0;i < privateBoxList.tagCount(); i++)
    {
    boxSet = new MailboxSet();
    boxSet.readFromNBT(privateBoxList.getCompoundTagAt(i));
    privateMailboxes.put(boxSet.owningPlayerName, boxSet);
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("publicBoxes", publicMailboxes.writeToNBT(new NBTTagCompound()));
  
  NBTTagList privateBoxList = new NBTTagList();
  NBTTagCompound setTag;
  for(MailboxSet set : this.privateMailboxes.values())
    {
    setTag = set.writeToNBT(new NBTTagCompound());
    privateBoxList.appendTag(setTag);
    }  
  tag.setTag("privateBoxes", privateBoxList);
  }

public void onTick(int length)
  {
  publicMailboxes.tick(length);
  for(MailboxSet set : this.privateMailboxes.values())
    {
    set.tick(length);
    }
  }

public boolean addMailbox(String owner, String name)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
//  AWLog.logDebug("creating mailbox of name: "+name+" for owner: "+owner + " in set: "+set);
  return set.addMailbox(name);
  }

public boolean deleteMailbox(String owner, String name)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
//  AWLog.logDebug("deleting mailbox of name: "+name+" for owner: "+owner + " in set: "+set);
  return set.deleteMailbox(name);
  }

public void addDeliverableItem(String owner, String name, ItemStack item, int dim, int x, int y, int z)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);  
  MailboxEntry entry = set.getOrCreateMailbox(name);    
//  AWLog.logDebug("adding deliverable item to: "+set.owningPlayerName +" :: "+entry+ " of: "+item);
  entry.addDeliverableItem(item, dim, x, y, z);
  markDirty();
  }

private MailboxSet getOrCreatePrivateMailbox(String owner)
  {
  if(owner==null){owner="";}
  if(!privateMailboxes.containsKey(owner))
    {
    privateMailboxes.put(owner, new MailboxSet(owner));
    markDirty();
    }
  return privateMailboxes.get(owner);
  }

public List<String> getPublicBoxNames()
  {
  ArrayList<String> names = new ArrayList<String>();
  MailboxSet set = publicMailboxes;
  names.addAll(set.mailboxes.keySet());
  return names;
  }

public List<String> getPrivateBoxNames(String owner)
  {
  ArrayList<String> names = new ArrayList<String>();
  if(privateMailboxes.containsKey(owner))
    {
    names.addAll(privateMailboxes.get(owner).mailboxes.keySet());
    }
  return names;
  }

public List<DeliverableItem> getDeliverableItems(String owner, String name, List<DeliverableItem> items, World world, int x, int y, int z)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
  return set.getDeliverableItems(name, items, world, x, y, z);
  }

public void removeDeliverableItem(String owner, String name, DeliverableItem item)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
  set.removeDeliverableItem(name, item);
  }

public void addMailboxReceiver(String owner, String name, TileMailbox box)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
  set.addReceiver(name, box);  
  }

private final class MailboxSet
{

private String owningPlayerName;
private HashMap<String, MailboxEntry> mailboxes = new HashMap<String, MailboxEntry>();

private MailboxSet(String name)
  {
  this.owningPlayerName = name;
  }

private MailboxSet(){}//nbt constructor

private boolean addMailbox(String name)
  {
  if(mailboxes.containsKey(name))
    {
    return false;
    }
  mailboxes.put(name, new MailboxEntry(name));
  markDirty();
  return true;
  }

private boolean deleteMailbox(String name)
  {
  if(!mailboxes.containsKey(name)){return false;}
  if(!mailboxes.get(name).incomingItems.isEmpty()){return false;}
  mailboxes.remove(name);
  return true;
  }

private void readFromNBT(NBTTagCompound tag)
  {
  mailboxes.clear();
  NBTTagList mailboxList = tag.getTagList("mailboxList", Constants.NBT.TAG_COMPOUND);
  NBTTagCompound mailboxTag;
  MailboxEntry entry;
  for(int i = 0; i <mailboxList.tagCount();i++)
    {
    mailboxTag = mailboxList.getCompoundTagAt(i);
    entry = new MailboxEntry();
    entry.readFromNBT(mailboxTag);
    mailboxes.put(entry.mailboxName, entry);
    }
  owningPlayerName = tag.getString("ownerName");
  }

private NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  NBTTagList mailboxList = new NBTTagList();
  NBTTagCompound mailboxTag;
  for(MailboxEntry entry : this.mailboxes.values())
    {
    mailboxTag = entry.writeToNBT(new NBTTagCompound());
    mailboxList.appendTag(mailboxTag);
    }  
  tag.setTag("mailboxList", mailboxList);
  tag.setString("ownerName", owningPlayerName);
  return tag;
  }

private void tick(int length)
  {
  for(MailboxEntry entry : this.mailboxes.values())
    {
    entry.tick(length);
    }
  }

private MailboxEntry getOrCreateMailbox(String name)
  {
  if(!this.mailboxes.containsKey(name))
    {
    this.mailboxes.put(name, new MailboxEntry(name));    
    markDirty();
    }
  return this.mailboxes.get(name);
  }

private List<DeliverableItem> getDeliverableItems(String name, List<DeliverableItem> items, World world, int x, int y, int z)
  {
  if(this.mailboxes.containsKey(name))
    {
    return this.mailboxes.get(name).getDeliverableItems(items, world, x, y, z);
    }
  return Collections.emptyList();
  }

private void removeDeliverableItem(String name, DeliverableItem item)
  {
  if(this.mailboxes.containsKey(name))
    {
    this.mailboxes.get(name).removeDeliverableItem(item);
    }
  }

private void addReceiver(String name, TileMailbox box)
  {
  if(this.mailboxes.containsKey(name))
    {
    this.mailboxes.get(name).addReceiver(box);
    }
  }
}

public final class MailboxEntry
{
private String mailboxName;
private List<DeliverableItem> incomingItems = new ArrayList<DeliverableItem>();
private List<TileMailbox> receivers = new ArrayList<TileMailbox>();

private MailboxEntry(String name)
  {
  this.mailboxName = name;
  }

private MailboxEntry(){}//nbt-constructor

private void addReceiver(TileMailbox tile)
  {
  this.receivers.add(tile);
  }

private void removeDeliverableItem(DeliverableItem item)
  {
  incomingItems.remove(item);
  markDirty();
  }

private List<DeliverableItem> getDeliverableItems(List<DeliverableItem> items, World world, int x, int y, int z)
  {
  int dim = world.provider.dimensionId;
  int time = 0;
  int timePerBlock = 10;//set time from config for per-block time
  int timeForDimension = 100;//set time from config for cross-dimensional items
  for(DeliverableItem item : this.incomingItems)
    {
    if(dim!=item.originDimension)
      {
      time = timeForDimension;
      }
    else
      {
      float dist = Trig.getDistance(item.x, item.y, item.z, x, y, z);
      time = (int)(dist * (float)timePerBlock);
      }
    if(item.deliveryTime>=time)
      {
      items.add(item);
      }
    }
  return items;
  }

private void addDeliverableItem(ItemStack item, int dimension, int x, int y, int z)
  {
  DeliverableItem item1 = new DeliverableItem(item, dimension, x, y, z);
  incomingItems.add(item1);
  markDirty();
  }

private void readFromNBT(NBTTagCompound tag)
  {
  mailboxName = tag.getString("name");
  NBTTagList itemList = tag.getTagList("itemList", Constants.NBT.TAG_COMPOUND);
  NBTTagCompound itemTag;
  DeliverableItem item;
  for(int i = 0; i < itemList.tagCount(); i++)
    {
    itemTag = itemList.getCompoundTagAt(i);
    item = new DeliverableItem();
    item.readFromNBT(itemTag);
    incomingItems.add(item);
    }
  }

private NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setString("name", mailboxName);  
  NBTTagList itemList = new NBTTagList();
  NBTTagCompound itemTag;
  for(DeliverableItem item : this.incomingItems)
    {
    itemTag = item.writeToNBT(new NBTTagCompound());
    itemList.appendTag(itemTag);
    }
  tag.setTag("itemList", itemList);
  return tag;
  }

private void tick(int length)
  {
  for(DeliverableItem item : this.incomingItems)
    {
    item.tick(length);
    }
  
  int dim;
  int time = 0;
  int timePerBlock = 10;//set time from config for per-block time
  int timeForDimension = 100;//set time from config for cross-dimensional items
  int x, y, z;
  Iterator<DeliverableItem> it;
  DeliverableItem item;
  ItemStack stack;
  for(TileMailbox box : receivers)
    {
    dim = box.getWorldObj().provider.dimensionId;
    x = box.xCoord;
    y = box.yCoord;
    z = box.zCoord;
    it = this.incomingItems.iterator();
    while(it.hasNext() && (item=it.next())!=null)
      {
      if(dim!=item.originDimension)
        {
        time = timeForDimension;
        }
      else
        {
        float dist = Trig.getDistance(item.x, item.y, item.z, x, y, z);
        time = (int)(dist * (float)timePerBlock);
        }
      if(item.deliveryTime>=time)//find if item is deliverable to this box
        {
        stack = item.item;
        stack = InventoryTools.mergeItemStack(box.inventory, stack, box.inventory.getAccessDirectionFor(RelativeSide.TOP));
        if(stack==null)
          {
          it.remove();          
          }
        break;
        }
      }
    }
  receivers.clear();
  }

@Override
public String toString()
  {
  return "MailboxEntry: "+name + " Items List: "+incomingItems;
  }
}

public final class DeliverableItem
{
int originDimension, x, y, z;
public ItemStack item;
int deliveryTime;//system milis at which this stack is deliverable

private DeliverableItem(ItemStack item, int dim, int x, int y, int z)
  {
  this.item = item;
  this.originDimension = dim;
  this.x = x;
  this.y = y;
  this.z = z;
  }

private DeliverableItem(){}

private void readFromNBT(NBTTagCompound tag)
  {
  item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
  deliveryTime = tag.getInteger("time");
  originDimension = tag.getInteger("dim");
  this.x = tag.getInteger("x");
  this.y = tag.getInteger("y");
  this.z = tag.getInteger("z");
  }

private NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("item", item.writeToNBT(new NBTTagCompound()));
  tag.setInteger("time", deliveryTime);
  tag.setInteger("dim", originDimension);
  tag.setInteger("x", x);
  tag.setInteger("y", y);
  tag.setInteger("z", z);
  return tag;
  }

private void tick(int length)
  {
  deliveryTime+=length;    
  markDirty();
  }
}

}
