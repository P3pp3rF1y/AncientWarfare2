package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class ContainerMailbox extends ContainerBase
{

public TileMailbox worksite;
public int guiHeight;

/**
 * synched stats
 */
public HashMap<RelativeSide, RelativeSide> sideMap = new HashMap<RelativeSide, RelativeSide>();
public String targetName;
public String mailboxName;
public boolean autoExport;
public boolean privateBox;
public List<String> publicBoxNames = new ArrayList<String>();
public List<String> privateBoxNames = new ArrayList<String>();

public ContainerMailbox(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  worksite = (TileMailbox) player.worldObj.getTileEntity(x, y, z);
  InventorySided inventory = worksite.inventory;
  
  for(RelativeSide rSide : inventory.rType.getValidSides())
    {
    sideMap.put(rSide, inventory.getRemappedSide(rSide));
    }    
  
  int xPos, yPos, x1, y1;
  for(int i = 0; i<18; i++)
    {
    x1 = i%9;
    y1 = i/9;
    xPos = x1*18 + 8;
    yPos = y1*18 + 8 + 12;
    addSlotToContainer(new Slot(inventory, i, xPos, yPos));
    }
  
  for(int i = 0; i<18; i++)
    {
    x1 = i%9;
    y1 = i/9;
    xPos = x1*18 + 8;
    yPos = y1*18 + 8 + 12 + 2*18 + 12;
    addSlotToContainer(new Slot(inventory, i+18, xPos, yPos));
    }
    
  y1 = 8+12+12 + 4*18;
  guiHeight = addPlayerSlots(player, 8, y1+12, 4) + 8 + 24;  
  
  if(!player.worldObj.isRemote)
    {
    MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, player.worldObj, MailboxData.class);
    publicBoxNames.addAll(data.getPublicBoxNames());
    privateBoxNames.addAll(data.getPrivateBoxNames(worksite.getOwnerName()));
    privateBox = worksite.isPrivateBox();
    autoExport = worksite.isAutoExport();
    mailboxName = worksite.getMailboxName();
    targetName = worksite.getTargetName();
    }
  }

@Override
public void sendInitData()
  {
  sendAccessMap();  
  NBTTagCompound tag = new NBTTagCompound();  
  if(mailboxName!=null)
    {
    tag.setString("mailboxName", mailboxName);
    }
  if(targetName!=null)
    {
    tag.setString("targetName", targetName);
    }
  
  tag.setBoolean("privateBox", privateBox);
  tag.setBoolean("autoExport", autoExport);  
    
  NBTTagList nameList = new NBTTagList();
  for(String boxName : publicBoxNames)
    {
    nameList.appendTag(new NBTTagString(boxName));
    }    
  tag.setTag("publicBoxNames", nameList);
  
  nameList = new NBTTagList();
  for(String boxName : privateBoxNames)
    {
    nameList.appendTag(new NBTTagString(boxName));
    }    
  tag.setTag("privateBoxNames", nameList); 
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  handleAccessMapTag(tag);
  if(tag.hasKey("autoExport"))
    {
    autoExport = tag.getBoolean("autoExport");
    worksite.setAutoExport(autoExport);
    }
  if(tag.hasKey("privateBox"))
    {
    privateBox = tag.getBoolean("privateBox");
    worksite.setPrivateBox(privateBox);
    }
  if(tag.hasKey("clearMailbox"))
    {
    mailboxName = null;
    worksite.setMailboxName(mailboxName);
    }
  if(tag.hasKey("clearTarget"))
    {
    targetName = null;
    worksite.setTargetName(targetName);
    }
  if(tag.hasKey("mailboxName"))
    {
    mailboxName = tag.getString("mailboxName");
    worksite.setMailboxName(mailboxName);
    }
  if(tag.hasKey("targetName"))
    {
    targetName = tag.getString("targetName");
    worksite.setTargetName(targetName);
    }
  if(tag.hasKey("publicBoxNames"))
    {
    publicBoxNames.clear();
    NBTTagList nameList = tag.getTagList("publicBoxNames", Constants.NBT.TAG_STRING);
    for(int i = 0; i < nameList.tagCount(); i++)
      {
      publicBoxNames.add(nameList.getStringTagAt(i));
      }
    }
  if(tag.hasKey("privateBoxNames"))
    {
    privateBoxNames.clear();
    NBTTagList nameList = tag.getTagList("privateBoxNames", Constants.NBT.TAG_STRING);
    for(int i = 0; i < nameList.tagCount(); i++)
      {
      privateBoxNames.add(nameList.getStringTagAt(i));
      }
    }
  if(tag.hasKey("addMailbox"))
    {
    String name = tag.getString("addMailbox");
    MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, player.worldObj, MailboxData.class);
    data.addMailbox(worksite.isPrivateBox()? worksite.getOwnerName():null, name);
    }
  if(tag.hasKey("deleteMailbox"))
    {
    String name = tag.getString("deleteMailbox");
    MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, player.worldObj, MailboxData.class);
    data.deleteMailbox(worksite.isPrivateBox()? worksite.getOwnerName():null, name);
    }
  refreshGui();
  }

private void sendAccessMap()
  {
  int l = sideMap.size();
  int rMap[] = new int[l];
  int iMap[] = new int[l];  
  int index = 0;
  for(RelativeSide rSide : sideMap.keySet())
    {
    rMap[index]=rSide.ordinal();
    iMap[index]=sideMap.get(rSide).ordinal();
    index++;
    }
  NBTTagCompound accessTag = new NBTTagCompound();
  accessTag.setIntArray("rMap", rMap);
  accessTag.setIntArray("iMap", iMap);
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("accessMap", accessTag);
  sendDataToClient(tag);
  }

private void handleAccessMapTag(NBTTagCompound tag)
  {
  if(tag.hasKey("accessMap"))
    {
    NBTTagCompound accessTag = tag.getCompoundTag("accessMap");
    int[] rMap = accessTag.getIntArray("rMap");
    int[] rMap2 = accessTag.getIntArray("iMap");
    RelativeSide rSide;
    RelativeSide iSide;
    for(int i = 0; i <rMap.length && i<rMap2.length; i++)
      {
      rSide = RelativeSide.values()[rMap[i]];
      iSide = RelativeSide.values()[rMap2[i]];
      sideMap.put(rSide, iSide);
      }
    }
  if(tag.hasKey("accessChange"))
    {
    NBTTagCompound slotTag = tag.getCompoundTag("accessChange");
    RelativeSide base = RelativeSide.values()[slotTag.getInteger("baseSide")];
    RelativeSide access = RelativeSide.values()[slotTag.getInteger("accessSide")];
    sideMap.put(base, access);
    if(!player.worldObj.isRemote)
      {
      worksite.inventory.remapSideAccess(base, access);      
      }
    }
  }

private void synchAccessMap()
  {
  InventorySided inventory = worksite.inventory;
  NBTTagCompound tag;
  NBTTagCompound slotTag;
  RelativeSide rSide2, rSide3;
  for(RelativeSide rSide : inventory.rType.getValidSides())
    {
    rSide2 = inventory.getRemappedSide(rSide);
    rSide3 = sideMap.get(rSide);
    if(rSide2!=rSide3)
      {
      sideMap.put(rSide, rSide2);  
      
      tag = new NBTTagCompound();
      slotTag = new NBTTagCompound();
      slotTag.setInteger("baseSide", rSide.ordinal());
      slotTag.setInteger("accessSide", rSide2.ordinal());
      tag.setTag("accessChange", slotTag);
      sendDataToClient(tag);    
      }    
    } 
  }

public void sendSlotChange(RelativeSide base, RelativeSide access)
  {  
  NBTTagCompound tag;
  NBTTagCompound slotTag;
  tag = new NBTTagCompound();
  slotTag = new NBTTagCompound();
  slotTag.setInteger("baseSide", base.ordinal());
  slotTag.setInteger("accessSide", access.ordinal());
  tag.setTag("accessChange", slotTag);
  sendDataToServer(tag);    
  }

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  synchAccessMap();
  NBTTagCompound tag = null;
  /**
   * DETECT CHANGES TO NAME AND TARGET AND SEND TO CLIENT
   */
  String name = worksite.getMailboxName();
  if(!StringTools.doStringsMatch(name, mailboxName))
    {
    if(tag==null){tag = new NBTTagCompound();}
    mailboxName = worksite.getMailboxName();
    if(mailboxName==null)
      {
      tag.setBoolean("clearMailbox", true);
      }
    else
      {
      tag.setString("mailboxName", mailboxName);
      }
    }
  name = worksite.getTargetName();
  if(!StringTools.doStringsMatch(name, targetName))
    {
    if(tag==null){tag = new NBTTagCompound();}
    targetName = worksite.getTargetName();
    if(targetName==null)
      {
      tag.setBoolean("clearTarget", true);      
      }
    else
      {
      tag.setString("targetName", mailboxName);      
      }
    }
  /**
   * detect changes to auto export and private box setting
   */
  if(autoExport!=worksite.isAutoExport())
    {
    if(tag==null){tag = new NBTTagCompound();}
    autoExport = worksite.isAutoExport();
    tag.setBoolean("autoExport", autoExport);
    }
  if(privateBox!=worksite.isPrivateBox())
    {
    if(tag==null){tag = new NBTTagCompound();}
    privateBox = worksite.isPrivateBox();
    tag.setBoolean("privateBox", privateBox);
    }
  /**
   * detect changes to public or private names list
   */
  MailboxData data = AWGameData.INSTANCE.getData(MailboxData.name, player.worldObj, MailboxData.class);
  if(!publicBoxNames.equals(data.getPublicBoxNames()))
    {
    if(tag==null){tag = new NBTTagCompound();}
    publicBoxNames.clear();
    publicBoxNames.addAll(data.getPublicBoxNames());
    NBTTagList nameList = new NBTTagList();
    for(String boxName : publicBoxNames)
      {
      nameList.appendTag(new NBTTagString(boxName));
      }    
    tag.setTag("publicBoxNames", nameList);
    }
  if(!privateBoxNames.equals(data.getPrivateBoxNames(worksite.getOwnerName())))
    {
    if(tag==null){tag = new NBTTagCompound();}
    privateBoxNames.clear();
    privateBoxNames.addAll(data.getPrivateBoxNames(worksite.getOwnerName()));
    NBTTagList nameList = new NBTTagList();
    for(String boxName : privateBoxNames)
      {
      nameList.appendTag(new NBTTagString(boxName));
      }    
    tag.setTag("privateBoxNames", nameList);
    }
  /**
   * if tag is not null (something has changed), send it to client
   */
  if(tag!=null)
    {
    sendDataToClient(tag);
    }
  }

/**
 * client-side input method
 */
public void handleNameAdd(String name)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setString("addMailbox", name);
  sendDataToServer(tag);
  }

/**
 * client-side input method
 */
public void handleNameDelete(String name)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setString("deleteMailbox", name);
  sendDataToServer(tag);
  }

/**
 * client-side input method
 */
public void handleNameSelection(String name)
  {
  NBTTagCompound tag = new NBTTagCompound();
  if(name==null)
    {
    tag.setBoolean("clearMailbox", true);    
    }
  else
    {
    tag.setString("mailboxName", name);    
    }
  mailboxName = name;
  sendDataToServer(tag);
  }

/**
 * client-side input method
 */
public void handleTargetSelection(String name)
  {
  NBTTagCompound tag = new NBTTagCompound();
  if(name==null)
    {
    tag.setBoolean("clearTarget", true);    
    }
  else
    {
    tag.setString("targetName", name);    
    }
  tag.setString("targetName", name);
  targetName = name;
  sendDataToServer(tag);
  }

/**
 * client-side input method
 * @param newVal
 */
public void handlePrivateBoxToggle(boolean newVal)
  {
  targetName = null;
  mailboxName = null;
  privateBox = newVal;
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("privateBox", privateBox);
  sendDataToServer(tag);
  }

public void handleAutoExportToggle(boolean newVal)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("autoExport", newVal);
  sendDataToServer(tag);
  }

@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  if (theSlot != null && theSlot.getHasStack())
    {
    ItemStack slotStack = theSlot.getStack();
    slotStackCopy = slotStack.copy();  
    if (slotStack.stackSize == 0)
      {
      theSlot.putStack((ItemStack)null);
      }
    else
      {
      theSlot.onSlotChanged();
      }
    if (slotStack.stackSize == slotStackCopy.stackSize)
      {
      return null;
      }
    theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
    }
  return slotStackCopy;  
  }

}
