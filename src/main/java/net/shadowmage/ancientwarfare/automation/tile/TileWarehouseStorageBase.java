package net.shadowmage.ancientwarfare.automation.tile;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public abstract class TileWarehouseStorageBase extends TileEntity implements IInteractableTile, IWarehouseStorageTile, IControlledTile
{

BlockPosition controllerPosition = null;
private boolean init;
int storageAdditionSize;
List<WarehouseItemFilter> filters = new ArrayList<WarehouseItemFilter>();
List<ContainerWarehouseStorage> viewers = new ArrayList<ContainerWarehouseStorage>();

/**
 * implementing sub-classes must create their inventory in their constructor, or things will NPE
 * on load/save
 */
public TileWarehouseStorageBase()
  {
  
  }

public void addViewer(ContainerWarehouseStorage container)
  {
  viewers.add(container);
  }

public void removeViewer(ContainerWarehouseStorage container)
  {
  viewers.remove(container);
  }

public void updateViewers()
  {
  for(ContainerWarehouseStorage viewer : viewers)
    {
    viewer.refreshGui();
    }
  }

@Override
public void onWarehouseInventoryUpdated(WorkSiteWarehouse warehouse)
  {
  updateFilterCounts(true);
  }

private WorkSiteWarehouse getWarehouse()
  {
  if(controllerPosition!=null && worldObj.blockExists(controllerPosition.x, controllerPosition.y, controllerPosition.z))
    {
    TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    if(te instanceof WorkSiteWarehouse)
      {
      return (WorkSiteWarehouse) te;
      }
    }
  return null;
  }

private void updateFilterCounts(boolean addBlockEvents)
  {
  WarehouseItemFilter filter;
  WorkSiteWarehouse warehouse = getWarehouse();
  if(warehouse==null)
    {
    for(int i = 0; i <this.filters.size(); i++)
      {
      filter = filters.get(i);
      filter.setFilterQuantity(0);
      if(addBlockEvents)
        {
        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), i, 0);        
        }
      }
    return;
    }
  int qty;
  for(int i = 0; i <this.filters.size(); i++)
    {
    filter = filters.get(i);
    if(filter.getFilterItem()==null){continue;}
    qty = warehouse.getCountOf(filter.getFilterItem());
    if(qty!=filter.getFilterQuantity())
      {
      filter.setFilterQuantity(qty);
      if(addBlockEvents)
        {
        worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), i, qty);
        }
      }
    }
  }

@Override
public String toString()
  {
  return "Storage tile location: "+xCoord+","+yCoord+","+zCoord;
  }

@Override
public int getStorageAdditionSize()
  {
  return storageAdditionSize;
  }

@Override
public void validate()
  {
  super.validate();
  init = false;
  }

@Override
public void invalidate()
  {  
  super.invalidate();
  this.init = false;
  if(controllerPosition!=null && worldObj.blockExists(controllerPosition.x, controllerPosition.y, controllerPosition.z))
    {
    TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    if(te instanceof WorkSiteWarehouse)
      {
      WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
      BlockPosition min = warehouse.getWorkBoundsMin();
      BlockPosition max = warehouse.getWorkBoundsMax();
      if(xCoord>=min.x && xCoord<=max.x && yCoord>=min.y && yCoord<=max.y && zCoord>=min.z && zCoord<=max.z)
        {
        warehouse.removeStorageBlock(this);
        }
      }
    }
  this.viewers.clear();
  controllerPosition = null;
  }

@Override
public void setControllerPosition(BlockPosition position)
  {
  this.controllerPosition = position;
  this.init = this.controllerPosition!=null;
  }

@Override
public void updateEntity()
  {
  if(!init)
    {
    init = true;
    AWLog.logDebug("scanning for controller...");
    for(TileEntity te : (List<TileEntity>)WorldTools.getTileEntitiesInArea(worldObj, xCoord-16, yCoord-4, zCoord-16, xCoord+16, yCoord+4, zCoord+16))
      {
      if(te instanceof WorkSiteWarehouse)
        {
        WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
        BlockPosition min = warehouse.getWorkBoundsMin();
        BlockPosition max = warehouse.getWorkBoundsMax();
        if(xCoord>=min.x && xCoord<=max.x && yCoord>=min.y && yCoord<=max.y && zCoord>=min.z && zCoord<=max.z)
          {
          warehouse.addStorageBlock(this);
          controllerPosition = new BlockPosition(warehouse.xCoord, warehouse.yCoord, warehouse.zCoord);
          break;
          }
        }
      } 
    }
  }

@Override
public void setFilters(List<WarehouseItemFilter> filters)
  {
  this.filters.clear();
  this.filters.addAll(filters);
  updateFilterCounts(false);
  if(!worldObj.isRemote)
    {
    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
  }

@Override
public List<WarehouseItemFilter> getFilters()
  {
  return filters;
  }

/*****************************************NETWORK HANDLING METHODS*******************************************/
@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);  
  filters.clear();
  if(tag.hasKey("filterList"))
    {
    WarehouseItemFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), filters);    
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  tag.setTag("filterList", WarehouseItemFilter.writeFilterList(filters));
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {  
  NBTTagCompound tag = pkt.func_148857_g();
  if(tag.hasKey("controllerPosition"))
    {
    controllerPosition = new BlockPosition(pkt.func_148857_g().getCompoundTag("controllerPosition"));
    }
  filters.clear();
  if(tag.hasKey("filterList"))
    {
    WarehouseItemFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), filters);    
    }
  updateViewers();
  }

@Override
public Packet getDescriptionPacket()
  {  
  NBTTagCompound tag = new NBTTagCompound();
  if(controllerPosition!=null)
    {
    tag.setTag("controllerPosition", controllerPosition.writeToNBT(new NBTTagCompound()));
    }
  if(!filters.isEmpty())
    {
    tag.setTag("filterList", WarehouseItemFilter.writeFilterList(filters));    
    }
  S35PacketUpdateTileEntity pkt = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, tag);
  return pkt;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE, xCoord, yCoord, zCoord);
    }
  return true;
  }

@Override
public boolean receiveClientEvent(int a, int b)
  {
  if(!worldObj.isRemote)
    {
    return true;
    }
  AWLog.logDebug("receiving client event: "+a+"::"+b+" client: "+worldObj.isRemote);
  if(a>=0 && a<filters.size())
    {
    WarehouseItemFilter filter = filters.get(a);
    filter.setFilterQuantity(b);
    }
  return false;
  }

}
