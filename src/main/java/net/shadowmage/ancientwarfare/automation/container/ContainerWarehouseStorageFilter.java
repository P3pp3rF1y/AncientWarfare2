package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWarehouseStorageFilter extends ContainerBase
{

public List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();
public IWarehouseStorageTile storageTile;

public ContainerWarehouseStorageFilter(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  storageTile = (IWarehouseStorageTile) player.worldObj.getTileEntity(x, y, z);
  itemFilters.addAll(storageTile.getFilters());
  
  addPlayerSlots(player, 8, 156, 4);
  }

@Override
public void sendInitData()
  {
  NBTTagCompound tag = new NBTTagCompound(); 
  NBTTagList filterList = new NBTTagList();
  for(WarehouseItemFilter filter : itemFilters)
    {
    filterList.appendTag(filter.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("filterList", filterList);
  sendDataToClient(tag);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("filterList"))
    {
    itemFilters.clear();
    NBTTagList filterList = tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND);
    WarehouseItemFilter filter;
    for(int i = 0; i < filterList.tagCount();i++)
      {
      filter = new WarehouseItemFilter();
      filter.readFromNBT(filterList.getCompoundTagAt(i));
      itemFilters.add(filter);
      }
    storageTile.setFilterList(itemFilters);
    TileEntity te = (TileEntity)storageTile;
    player.worldObj.markBlockForUpdate(te.xCoord, te.yCoord, te.zCoord);
    }  
  refreshGui();
  }

@Override
public void detectAndSendChanges()
  {  
  super.detectAndSendChanges();
  }

public void sendDataToServer()
  {
  NBTTagCompound tag = new NBTTagCompound(); 
  NBTTagList filterList = new NBTTagList();
  for(WarehouseItemFilter filter : itemFilters)
    {
    filterList.appendTag(filter.writeToNBT(new NBTTagCompound()));
    }
  tag.setTag("filterList", filterList);
  sendDataToServer(tag);
  }

}
