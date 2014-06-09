package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWarehouseStorage extends ContainerBase
{

public IWarehouseStorageTile tile;
public int guiHeight;
public int areaSize;
int playerSlotsSize;
int playerSlotsY;

public List<WarehouseStorageFilter> filters = new ArrayList<WarehouseStorageFilter>();

public ContainerWarehouseStorage(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (IWarehouseStorageTile) player.worldObj.getTileEntity(x, y, z);
  tile.addViewer(this);
  
  areaSize = 5*18 + 16;
  playerSlotsY = areaSize+8;
  playerSlotsSize = 8+4+4*18;
  guiHeight = playerSlotsY+playerSlotsSize;
  
  filters.addAll(tile.getFilters());
  addPlayerSlots(player, 8, playerSlotsY, 4);  
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("filterList", WarehouseStorageFilter.writeFilterList(filters));
  sendDataToClient(tag);
  }

public void sendFiltersToServer()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("filterList", WarehouseStorageFilter.writeFilterList(filters));  
  sendDataToServer(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("filterList"))
    {
    List<WarehouseStorageFilter> filters = WarehouseStorageFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseStorageFilter>());
    if(player.worldObj.isRemote)
      {
      this.filters.clear();
      this.filters.addAll(filters); 
      refreshGui();
      }
    else
      {
      tile.setFilters(filters);      
      }
    }
  super.handlePacketData(tag);
  }

public void onFilterListUpdated()
  {
  this.filters.clear();
  this.filters.addAll(tile.getFilters());
  sendInitData();
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  tile.removeViewer(this);
  super.onContainerClosed(par1EntityPlayer);
  }

}
