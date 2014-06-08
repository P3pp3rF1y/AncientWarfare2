package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWarehouseStorage extends ContainerBase
{

public TileWarehouseStorageBase tile;
public int guiHeight;
public int areaSize;
int playerSlotsSize;
int playerSlotsY;

public ContainerWarehouseStorage(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileWarehouseStorageBase) player.worldObj.getTileEntity(x, y, z);
  tile.addViewer(this);
  
  areaSize = 5*18 + 16;
  playerSlotsY = areaSize+8;
  playerSlotsSize = 8+4+4*18;
  guiHeight = playerSlotsY+playerSlotsSize;
  
  addPlayerSlots(player, 8, playerSlotsY, 4);  
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("filterList"))
    {
    List<WarehouseItemFilter> filters = WarehouseItemFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseItemFilter>());    
    tile.setFilters(filters);
    }
  super.handlePacketData(tag);
  }

@Override
public void onContainerClosed(EntityPlayer par1EntityPlayer)
  {
  tile.removeViewer(this);
  super.onContainerClosed(par1EntityPlayer);
  }

}
