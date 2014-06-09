package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;

public class ContainerWarehouseControl extends ContainerBase
{

public TileWarehouseBase warehouse;
public ItemQuantityMap itemMap = new ItemQuantityMap();
public ItemQuantityMap cache = new ItemQuantityMap();
boolean shouldUpdate = true;

public ContainerWarehouseControl(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  warehouse = (TileWarehouseBase) player.worldObj.getTileEntity(x, y, z);
  addPlayerSlots(player, 8, 156, 4);
  warehouse.addViewer(this);
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  warehouse.removeViewer(this);
  super.onContainerClosed(par1EntityPlayer);
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

  cache.clear();
  warehouse.getItems(cache);
  ItemQuantityMap warehouseItemMap = cache;
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

public void onWarehouseInventoryUpdated()
  {  
  shouldUpdate = true;
  }

}
