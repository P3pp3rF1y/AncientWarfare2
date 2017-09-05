package net.shadowmage.ancientwarfare.automation.tile.warehouse;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseInterfaceFilter;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBasic;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class TileWarehouseOutput extends TileEntity implements IControlledTile, IInventory, IInteractableTile
{

BlockPosition controllerPosition = null;
private boolean init;
private InventoryBasic inventory;

private List<WarehouseInterfaceFilter> filters = new ArrayList<>();

public TileWarehouseOutput()
  {
  inventory = new InventoryBasic(9)
    {
    @Override
    public void markDirty()
      {
      TileWarehouseOutput.this.markDirty();
      }
    }; 
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
      if(x>=min.x && x<=max.x && y>=min.y && y<=max.y && z>=min.z && z<=max.z)
        {
        warehouse.removeOutputBlock(this);
        }
      }
    }
  controllerPosition = null;
  }

@Override
public void setControllerPosition(BlockPosition position)
  {
  this.controllerPosition = position;
  this.init = this.controllerPosition!=null;
  }

@Override
public void markDirty()
  {  
  super.markDirty();
  if(this.controllerPosition!=null)
    {
    TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
    if(te instanceof WorkSiteWarehouse)
      {
      ((WorkSiteWarehouse) te).onOutputInventoryUpdated(this);
      }
    }
  }

@Override
public void updateEntity()
  {
  if(!init)
    {
    init = true;
    for(TileEntity te : (List<TileEntity>)WorldTools.getTileEntitiesInArea(worldObj, x-16, y-4, z-16, x+16, y+4, z+16))
      {
      if(te instanceof WorkSiteWarehouse)
        {
        WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
        BlockPosition min = warehouse.getWorkBoundsMin();
        BlockPosition max = warehouse.getWorkBoundsMax();
        if(x>=min.x && x<=max.x && y>=min.y && y<=max.y && z>=min.z && z<=max.z)
          {
          warehouse.addOutputBlock(this);
          controllerPosition = new BlockPosition(warehouse.x, warehouse.y, warehouse.z);
          break;
          }
        }
      } 
    }
  }

@Override
public Packet getDescriptionPacket()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("filterList", WarehouseInterfaceFilter.writeFilterList(filters));
  S35PacketUpdateTileEntity pkt = new S35PacketUpdateTileEntity(x, y, z, 0, tag);
  return pkt;
  }

@Override
public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
  {
  filters.clear();
  NBTTagCompound tag = pkt.func_148857_g();
  if(tag.hasKey("filterList"))
    {
    filters = WarehouseInterfaceFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), filters);    
    }
  super.onDataPacket(net, pkt);
  }

@Override
public void readFromNBT(NBTTagCompound tag)
  {
  super.readFromNBT(tag);
  filters.clear();
  inventory.readFromNBT(tag.getCompoundTag("inventory"));
  if(tag.hasKey("filterList"))
    {
    filters = WarehouseInterfaceFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), filters);    
    }
  }

@Override
public void writeToNBT(NBTTagCompound tag)
  {
  super.writeToNBT(tag);
  NBTTagCompound tag1 = new NBTTagCompound();
  inventory.writeToNBT(tag1);
  tag.setTag("inventory", tag1);
  if(!filters.isEmpty())
    {
    tag.setTag("filterList", WarehouseInterfaceFilter.writeFilterList(filters));
    }
  }

public List<WarehouseInterfaceFilter> getFilters()
  {
  return filters;
  }

public void setFilters(List<WarehouseInterfaceFilter> filters)
  {
  this.filters.clear();
  this.filters.addAll(filters);
  if(!this.worldObj.isRemote)
    {
    this.worldObj.markBlockForUpdate(x, y, z);
    if(controllerPosition!=null && worldObj.blockExists(controllerPosition.x, controllerPosition.y, controllerPosition.z))
      {
      TileEntity te = worldObj.getTileEntity(controllerPosition.x, controllerPosition.y, controllerPosition.z);
      if(te instanceof WorkSiteWarehouse)
        {
        WorkSiteWarehouse warehouse = (WorkSiteWarehouse)te;
        warehouse.onOutputInventoryUpdated(this);
        }
      }
    }
  }

@Override
public int getSizeInventory()
  {
  return inventory.getSizeInventory();
  }

@Override
public ItemStack getStackInSlot(int var1)
  {
  return inventory.getStackInSlot(var1);
  }

@Override
public ItemStack decrStackSize(int var1, int var2)
  {
  return inventory.decrStackSize(var1, var2);
  }

@Override
public ItemStack getStackInSlotOnClosing(int var1)
  {
  return inventory.getStackInSlotOnClosing(var1);
  }

@Override
public void setInventorySlotContents(int var1, ItemStack var2)
  {
  inventory.setInventorySlotContents(var1, var2);
  }

@Override
public String getInventoryName()
  {
  return inventory.getInventoryName();
  }

@Override
public boolean hasCustomInventoryName()
  {
  return false;
  }

@Override
public int getInventoryStackLimit()
  {
  return 64;
  }

@Override
public boolean isUseableByPlayer(EntityPlayer var1)
  {
  return true;
  }

@Override
public void openInventory()
  {
  
  }

@Override
public void closeInventory()
  {
  
  }

@Override
public boolean isItemValidForSlot(int var1, ItemStack var2)
  {
  for(WarehouseInterfaceFilter filter : filters)
    {
    if(filter.isItemValid(var2))
      {
      return true;
      }
    }
  return false;
  }

@Override
public boolean onBlockClicked(EntityPlayer player)
  {
  if(!player.worldObj.isRemote)
    {
    NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_OUTPUT, x, y, z);
    }
  return true;
  }

}
