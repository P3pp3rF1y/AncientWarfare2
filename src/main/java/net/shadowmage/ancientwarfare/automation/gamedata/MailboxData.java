package net.shadowmage.ancientwarfare.automation.gamedata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants;

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

public void onTick()
  {
  publicMailboxes.tick();
  for(MailboxSet set : this.privateMailboxes.values())
    {
    set.tick();
    }
  }

public boolean addMailbox(String owner, String name)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
  return set.addMailbox(name);
  }

public void addDeliverableItem(String owner, String name, ItemStack item, int ticks)
  {
  MailboxSet set = owner==null ? publicMailboxes : getOrCreatePrivateMailbox(owner);
  if(owner==null && !publicMailboxes.mailboxes.containsKey(name))
    {
    MailboxEntry entry = set.getOrCreateMailbox(name);    
    entry.addDeliverableItem(item, ticks);
    }
  markDirty();
  }

private MailboxSet getOrCreatePrivateMailbox(String owner)
  {
  if(!privateMailboxes.containsKey(owner))
    {
    privateMailboxes.put(owner, new MailboxSet(owner));
    markDirty();
    }
  return privateMailboxes.get(owner);
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

private void tick()
  {
  for(MailboxEntry entry : this.mailboxes.values())
    {
    entry.tick();
    }
  }

public MailboxEntry getOrCreateMailbox(String name)
  {
  if(!this.mailboxes.containsKey(name))
    {
    this.mailboxes.put(name, new MailboxEntry(name));    
    markDirty();
    }
  return this.mailboxes.get(name);
  }
}

public final class MailboxEntry
{
private String mailboxName;
private List<DeliverableItem> incomingItems = new ArrayList<DeliverableItem>();

private MailboxEntry(String name)
  {
  this.mailboxName = name;
  }

private MailboxEntry(){}//nbt-constructor

public void removeDeliverableItem(DeliverableItem item)
  {
  incomingItems.remove(item);
  markDirty();
  }

public List<DeliverableItem> getDeliverableItems(List<DeliverableItem> items)
  {
  for(DeliverableItem item : this.incomingItems)
    {
    if(item.deliveryTime<=0)
      {
      items.add(item);
      }
    }
  return items;
  }

public void addDeliverableItem(ItemStack item, int ticks)
  {
  DeliverableItem item1 = new DeliverableItem();
  item1.deliveryTime = ticks;
  item1.item = item;
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

private void tick()
  {
  for(DeliverableItem item : this.incomingItems)
    {
    item.tick();
    }
  }
}

private final class DeliverableItem
{
ItemStack item;
long deliveryTime;//system milis at which this stack is deliverable

private void readFromNBT(NBTTagCompound tag)
  {
  item = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
  deliveryTime = tag.getLong("time");
  }

private NBTTagCompound writeToNBT(NBTTagCompound tag)
  {
  tag.setTag("item", item.writeToNBT(new NBTTagCompound()));
  tag.setLong("time", deliveryTime);
  return tag;
  }

private void tick()
  {
  if(deliveryTime>0)
    {
    deliveryTime--;    
    }
  markDirty();
  }
}

}
