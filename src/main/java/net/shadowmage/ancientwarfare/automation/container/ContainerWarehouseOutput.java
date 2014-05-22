package net.shadowmage.ancientwarfare.automation.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWarehouseOutput;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWarehouseOutput extends ContainerBase
{

public TileWarehouseOutput tile;

public ContainerWarehouseOutput(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  tile = (TileWarehouseOutput) player.worldObj.getTileEntity(x, y, z);
  
  WarehouseSlotFilter filter = new WarehouseSlotFilter();  
  for(int i = 0; i < 9; i++)
    {
    addSlotToContainer(new SlotFiltered(tile, i, (i%3)*18+8+3*18, (i/3)*18+8, filter));
    }
  
  addPlayerSlots(player, 8, 8+8+3*18, 4);
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("filterList"))
    {
    List<WarehouseItemFilter> filters = WarehouseItemFilter.readFilterList(tag.getTagList("filterList", Constants.NBT.TAG_COMPOUND), new ArrayList<WarehouseItemFilter>());    
    tile.setFilters(filters);
    tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
    }
  super.handlePacketData(tag);
  }

private class WarehouseSlotFilter extends ItemSlotFilter
{
@Override
public boolean isItemValid(ItemStack item)
  {
  return tile.isItemValidForSlot(0, item);
  }
}

}
