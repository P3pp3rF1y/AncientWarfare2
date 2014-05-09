package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.WorkSiteWarehouse;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ItemQuantityMap;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ItemStackHashWrap;

public class ContainerWarehouseControl extends ContainerBase
{

public WorkSiteWarehouse warehouse;
public List<IWarehouseStorageTile> storageTiles;
public ItemQuantityMap itemMap = new ItemQuantityMap();
private ItemQuantityMap warehouseItemMap = new ItemQuantityMap();
boolean shouldUpdate = true;

public ContainerWarehouseControl(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  warehouse = (WorkSiteWarehouse) player.worldObj.getTileEntity(x, y, z);
  storageTiles = new ArrayList<IWarehouseStorageTile>();
  storageTiles.addAll(warehouse.getStorageTiles());
  warehouse.addViewer(this);  
  int y2 = 8+9+4;
  int x1, y1;
  for(int k = 0; k <9; k++)
    {
    x1 = (k%3)*18 + 8 + 9*18 + 8;
    y1 = (k/3)*18 + y2;
    addSlotToContainer(new Slot(warehouse.inventory, k, x1, y1));
    }
  
  addPlayerSlots(player, 8, 8, 4);
  }

@Override
public void sendInitData()
  {  
  super.sendInitData();
  synchItemMaps();
  }

public void sendPositionList()
  {    
  BlockPosition pos;
  TileEntity te;
  NBTTagCompound tag = new NBTTagCompound();
  NBTTagList positionList = new NBTTagList();
  for(IWarehouseStorageTile tile : storageTiles)
    {
    te = (TileEntity)tile;
    pos = new BlockPosition(te.xCoord, te.yCoord, te.zCoord);
    positionList.appendTag(pos.writeToNBT(new NBTTagCompound()));
    }  
  tag.setTag("positionList", positionList);
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("positionList"))
    {
    storageTiles.clear();
    IWarehouseStorageTile tile;
    BlockPosition pos;
    NBTTagList positionList = tag.getTagList("positionList", Constants.NBT.TAG_COMPOUND);
    for(int i = 0; i < positionList.tagCount(); i++)
      {
      pos = new BlockPosition(positionList.getCompoundTagAt(i));
      tile = (IWarehouseStorageTile) player.worldObj.getTileEntity(pos.x, pos.y, pos.z);
      storageTiles.add(tile);
      }
    }
  if(tag.hasKey("request"))
    {
    NBTTagCompound reqTag = tag.getCompoundTag("request");
    BlockPosition pos = new BlockPosition(reqTag.getCompoundTag("reqPos"));
    ItemStack item = ItemStack.loadItemStackFromNBT(reqTag.getCompoundTag("reqItem"));
    warehouse.requestItem(pos, item, !reqTag.getBoolean("dmg"), !reqTag.getBoolean("nbt"));
    }
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
  if(!storageTiles.equals(warehouse.getStorageTiles()))
    {
    storageTiles.clear();
    storageTiles.addAll(warehouse.getStorageTiles());
    sendPositionList();
    }
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
  ItemStackHashWrap wrap;
  for(int i = 0; i < changeList.tagCount(); i++)
    {
    tag = changeList.getCompoundTagAt(i);
    wrap = readWrapFromNBT(tag);
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
  warehouseItemMap.clear();    
  for(IWarehouseStorageTile tile : warehouse.getStorageTiles())
    {
    tile.addInventoryContentsToMap(warehouseItemMap);
    }
  
  int qty;
  NBTTagList changeList = new NBTTagList();
  NBTTagCompound tag;
  for(ItemStackHashWrap wrap : this.itemMap.keySet())
    {
    qty = this.itemMap.get(wrap);
    if(qty!=warehouseItemMap.get(wrap))
      {
      qty = warehouseItemMap.get(wrap);
      tag = writeWrapToNBT(wrap);
      tag.setInteger("qty", qty);
      changeList.appendTag(tag);
      this.itemMap.put(wrap, qty);
      }
    }  
  for(ItemStackHashWrap wrap : warehouseItemMap.keySet())
    {
    if(!itemMap.contains(wrap))
      {
      qty = warehouseItemMap.get(wrap);
      tag = writeWrapToNBT(wrap);
      tag.setInteger("qty", qty);
      changeList.appendTag(tag);
      this.itemMap.put(wrap, qty);
      }
    }
  if(changeList.tagCount()>0)
    {
    AWLog.logDebug("Warehouse item map contains:\n"+warehouseItemMap);
    tag = new NBTTagCompound();
    tag.setTag("changeList", changeList);
    sendDataToClient(tag);    
    }
  t2 = System.nanoTime();
  t3 = t2-t1;
  float f1 = (float)((double)t3/1000000d);
  AWLog.logDebug("inventory synch time: "+t3+"ns ("+f1+"ms)");
  }

private NBTTagCompound writeWrapToNBT(ItemStackHashWrap wrap)
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setInteger("id", Item.getIdFromItem(wrap.getItem()));
  tag.setInteger("dmg", wrap.getDamage());
  NBTTagCompound itemTag = wrap.getTag();
  if(itemTag!=null)
    {
    tag.setTag("itemTag", itemTag);
    }  
  return tag;
  }

private ItemStackHashWrap readWrapFromNBT(NBTTagCompound tag)
  {
  int id = tag.getInteger("id");
  int dmg = tag.getInteger("dmg");
  NBTTagCompound itemTag = tag.hasKey("itemTag") ? tag.getCompoundTag("itemTag") : null;
  Item item = Item.getItemById(id);
  ItemStack stack = new ItemStack(item, 1, dmg);
  stack.stackTagCompound = itemTag;
  return new ItemStackHashWrap(stack);
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

}
