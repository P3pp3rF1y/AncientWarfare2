package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.gamedata.MailboxData;
import net.shadowmage.ancientwarfare.automation.tile.TileMailbox;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.ViewableSlot;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.core.util.StringTools;

public class ContainerMailbox extends ContainerBase
{

public TileMailbox worksite;
int[] sideStartIndices;
int[] sideEndIndices;
int totalInventorySize;
public int guiHeight;

/**
 * synched stats
 */
public HashMap<RelativeSide, InventorySide> sideMap = new HashMap<RelativeSide, InventorySide>();
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
  InventorySide accessedSide;
  for(RelativeSide side : RelativeSide.values())
    {  
    accessedSide = worksite.inventory.getAccessSideFor(side);
    sideMap.put(side, accessedSide);
    } 
  sideEndIndices = new int[6];
  sideStartIndices = new int[6];
  InventorySide side;
  int index = 0;
  int length = 0;
  for(int i = 0; i <6 ;i++)
    {
    length = 0;
    side = InventorySide.values()[i];
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);    
    if(slotMap!=null)
      {      
      length = slotMap.getSlots().size();
      }    
    sideStartIndices[i] = index;
    sideEndIndices[i] = index + length;
    index+=length;
    }
  totalInventorySize = worksite.inventory.getSizeInventory();
  

  int y1 = addWorksiteInventorySlots(8);
  guiHeight = addPlayerSlots(player, 8, y1+12, 4) - 16;  
   
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

protected int addWorksiteInventorySlots(int topY)
  {  
  int lowestY = topY;
  for(InventorySide side : InventorySide.values())
    {
    if(side==InventorySide.NONE){continue;}
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    for(ViewableSlot slot : slotMap.getSlots())
      {
      addSlotToContainer(new SlotFiltered(worksite.inventory, slot.slotNumber, slotMap.guiX + slot.viewX, slotMap.slotY+slot.viewY, worksite.inventory.getFilterForSlot(slot.slotNumber)));
      if(slotMap.slotY+slot.viewY>lowestY)
        {
        lowestY = slotMap.slotY+slot.viewY;
        }
      }    
    }  
  return lowestY + 18 + 4;
  }

@Override
public void sendInitData()
  {
  InventorySide accessedSide;  
  NBTTagList tagList = new NBTTagList();
  NBTTagCompound inner; 
  for(RelativeSide side : RelativeSide.values())
    {
    inner = new NBTTagCompound();
    inner.setInteger("baseSide", side.ordinal());
    accessedSide = sideMap.get(side);
    inner.setInteger("accessSide", accessedSide.ordinal());
    tagList.appendTag(inner);
    } 
  PacketGui pkt = new PacketGui();
  pkt.packetData.setTag("slotMap", tagList);  
  NetworkHandler.sendToPlayer((EntityPlayerMP) player, pkt);

  
  
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
  if(tag.hasKey("slotMap"))
    {
    readSlotMap(tag.getTagList("slotMap", Constants.NBT.TAG_COMPOUND));
    if(!player.worldObj.isRemote)
      {
      for(RelativeSide side : RelativeSide.values())
        {
        worksite.inventory.setSideMapping(side, sideMap.get(side));
        }
      }
    }
  else if(tag.hasKey("slotChange"))
    {
    int b, a;
    RelativeSide base;
    InventorySide access; b = tag.getInteger("baseSide");
    a = tag.getInteger("accessSide");
    base = RelativeSide.values()[b];
    access = InventorySide.values()[a];
    sideMap.put(base, access);
    worksite.inventory.setSideMapping(base, access);
    } 
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
    AWLog.logDebug("received public box list set of: "+publicBoxNames);
    }
  if(tag.hasKey("privateBoxNames"))
    {
    privateBoxNames.clear();
    NBTTagList nameList = tag.getTagList("privateBoxNames", Constants.NBT.TAG_STRING);
    for(int i = 0; i < nameList.tagCount(); i++)
      {
      privateBoxNames.add(nameList.getStringTagAt(i));
      }
    AWLog.logDebug("received private box list set of: "+privateBoxNames);
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

@Override
public void detectAndSendChanges()
  {
  super.detectAndSendChanges();
  /**
   * DETECT CHANGES TO INVENTORY SIDE MAPPING
   */
  InventorySide access;
  for(RelativeSide side : RelativeSide.values())
    {
    access = worksite.inventory.getAccessSideFor(side);
    if(access!=sideMap.get(side))
      {
      sendSlotChange(side, access);
      sideMap.put(side, access);
      }
    } 

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

public void sendSlotChange(RelativeSide base, InventorySide access)
  {  
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("slotChange", true);
  tag.setInteger("baseSide", base.ordinal());
  tag.setInteger("accessSide", access.ordinal());
  if(player.worldObj.isRemote)
    {
    sendDataToServer(tag);
    }
  else
    {
    sendDataToClient(tag);
    }
  }

public void sendSettingsToServer()
  {
  InventorySide accessedSide;
  
  NBTTagList tagList = new NBTTagList();
  NBTTagCompound inner;    
  
  for(RelativeSide side : RelativeSide.values())
    {
    inner = new NBTTagCompound();
    inner.setInteger("baseSide", side.ordinal());
    accessedSide = sideMap.get(side);
    inner.setInteger("accessSide", accessedSide.ordinal());
    tagList.appendTag(inner);
    } 
  PacketGui pkt = new PacketGui();
  pkt.packetData.setTag("slotMap", tagList);  
  NetworkHandler.sendToServer(pkt);
  }

protected void readSlotMap(NBTTagList list)
  {
  NBTTagCompound tag;
  RelativeSide base;
  InventorySide access;
  int b, a;
  for(int i = 0; i < list.tagCount(); i++)
    {
    tag = list.getCompoundTagAt(i);
    b = tag.getInteger("baseSide");
    a = tag.getInteger("accessSide");
    base = RelativeSide.values()[b];
    access = InventorySide.values()[a];
    sideMap.put(base, access);  
    }
  this.refreshGui();
  }

@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  SlotFiltered slot;
  if (theSlot != null && theSlot.getHasStack())
    {
    ItemStack slotStack = theSlot.getStack();
    slotStackCopy = slotStack.copy();    
    int playerSlotStart = totalInventorySize;    
 
    if(slotClickedIndex<totalInventorySize)//clicked in inventory, merge into player inventory
      {
      if(!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else//clicked in player inventory, try to merge from bottom up
      {
      int start, end;
      for(int i = 5; i >=0; i--)
        {
        start = sideStartIndices[i];
        end = sideEndIndices[i]; 
        if(start==end){continue;}
        slot = (SlotFiltered) inventorySlots.get(start);
        if(slot.isItemValid(slotStack))
          {
          this.mergeItemStack(slotStack, start, end, false);          
          }       
        if(slotStack.stackSize==0)
          {
          break;
          }
        }
      }
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
