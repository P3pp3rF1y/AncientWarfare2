package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteWarehouse;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap.ItemHashEntry;

public class ContainerWarehouseControl extends ContainerBase
{

public WorkSiteWarehouse warehouse;
public ItemQuantityMap itemMap = new ItemQuantityMap();
boolean shouldUpdate = true;

public ContainerWarehouseControl(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  warehouse = (WorkSiteWarehouse) player.worldObj.getTileEntity(x, y, z);
  warehouse.addViewer(this);  
  int y2 = 8, x2=8+2*18;
  int x1, y1;
  for(int k = 0; k <9; k++)
    {
    x1 = (k%3)*18 + x2;
    y1 = (k/3)*18 + y2;
    addSlotToContainer(new Slot(warehouse.inventory, k, x1, y1));
    }
  
  addPlayerSlots(player, 8, 156, 4);
  }

@Override
public void sendInitData()
  {  
//  NBTTagCompound wtag = warehouse.inventoryMap.writeToNBT(new NBTTagCompound());
//  itemMap.putAll(warehouse.inventoryMap);
//  NBTTagCompound tag = new NBTTagCompound();
//  tag.setTag("itemList", wtag);
//  sendDataToServer(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("requestSpecific"))
    {    
    NBTTagCompound reqTag = tag.getCompoundTag("requestSpecific");
    ItemStack item = ItemStack.loadItemStackFromNBT(reqTag.getCompoundTag("reqItem"));
    AWLog.logDebug("processing specific request..."+item);
    warehouse.requestItem(item);
    }
  if(tag.hasKey("changeList"))
    {
    handleChangeList(tag.getTagList("changeList", Constants.NBT.TAG_COMPOUND));
    }
  if(tag.hasKey("itemList"))
    {
    itemMap.readFromNBT(tag.getCompoundTag("itemList"));
    }
  refreshGui();
  }

public void handleClientRequestSpecific(ItemStack stack)
  {    
  AWLog.logDebug("sending specific request for: "+stack);
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("reqItem", stack.writeToNBT(new NBTTagCompound()));  
  NBTTagCompound pktTag = new NBTTagCompound();
  pktTag.setTag("requestSpecific", tag);
  sendDataToServer(pktTag);
  }

@Override
public void detectAndSendChanges()
  {  
  super.detectAndSendChanges();  
  if(shouldUpdate)
    {
    synchItemMaps();    
    shouldUpdate = false;
    }
  }

private void handleChangeList(NBTTagList changeList)
  {
  NBTTagCompound tag;
  int qty;
  ItemHashEntry wrap = null;
  for(int i = 0; i < changeList.tagCount(); i++)
    {
    tag = changeList.getCompoundTagAt(i);
    wrap = ItemHashEntry.readFromNBT(tag);
    qty = tag.getInteger("qty");
    if(qty==0)
      {
      itemMap.remove(wrap);
      }
    else
      {
      itemMap.put(wrap, qty);      
      }
    }
  AWLog.logDebug("Client item map now contains:\n"+itemMap);
  }

private void synchItemMaps()
  {
  long t1, t2, t3;
  t1 = System.nanoTime();
  /**
   * 
   * need to loop through this.itemMap and compare quantities to warehouse.itemMap
   *    add any changes to change-list
   * need to loop through warehouse.itemMap and find new entries
   *    add any new entries to change-list    
   */

  ItemQuantityMap warehouseItemMap = warehouse.inventoryMap;
  int qty;
  NBTTagList changeList = new NBTTagList();
  NBTTagCompound tag;
  for(ItemHashEntry wrap : this.itemMap.keySet())
    {
    qty = this.itemMap.getCount(wrap);
    if(qty!=warehouseItemMap.getCount(wrap))
      {
      qty = warehouseItemMap.getCount(wrap);
      tag = wrap.writeToNBT(new NBTTagCompound());
      tag.setInteger("qty", qty);
      changeList.appendTag(tag);
      this.itemMap.put(wrap, qty);
      }
    }  
  for(ItemHashEntry entry : warehouseItemMap.keySet())
    {
    if(!itemMap.contains(entry))
      {
      qty = warehouseItemMap.getCount(entry);
      tag = ItemHashEntry.writeToNBT(entry, new NBTTagCompound());
      tag.setInteger("qty", qty);
      changeList.appendTag(tag);
      this.itemMap.put(entry, qty);
      }
    }
  AWLog.logDebug("Warehouse item map contains:\n"+warehouseItemMap);
  if(changeList.tagCount()>0)
    {
    tag = new NBTTagCompound();
    tag.setTag("changeList", changeList);
    sendDataToClient(tag);    
    }
  t2 = System.nanoTime();
  t3 = t2-t1;
  float f1 = (float)((double)t3/1000000d);
  AWLog.logDebug("inventory synch time: "+t3+"ns ("+f1+"ms)");
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  super.onContainerClosed(par1EntityPlayer);
  warehouse.removeViewer(this);
  }

public void onWarehouseInventoryUpdated()
  {  
  shouldUpdate = true;
  }

/**
 * @return should always return null for normal implementation, not sure wtf the rest of the code is about
 */
@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  int slots = 9;
  Slot slot = (Slot)this.inventorySlots.get(slotClickedIndex);
  if(slot==null || !slot.getHasStack()){return null;}
  ItemStack stackFromSlot = slot.getStack();
  if(slotClickedIndex < slots)
    {
    this.mergeItemStack(stackFromSlot, slots, slots+36, false);
    }
  else
    {
    this.mergeItemStack(stackFromSlot, 0, slots, true);
    }
  if(stackFromSlot.stackSize == 0)
    {
    slot.putStack((ItemStack)null);
    }
  else
    {
    slot.onSlotChanged();
    }
  return null;  
  }

}
