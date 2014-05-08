package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
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

public class ContainerWarehouseControl extends ContainerBase
{

public WorkSiteWarehouse warehouse;
public List<IWarehouseStorageTile> storageTiles;

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
  refreshGui();
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
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  super.onContainerClosed(par1EntityPlayer);
  warehouse.removeViewer(this);
  }

public void onWarehouseInventoryUpdated()
  {
  /**
   * called by warehouse when its input inventory is recounted
   */
  }

}
