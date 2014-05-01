package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseInput;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public class ContainerWarehouseInput extends ContainerBase
{
public TileWarehouseInput storageTile;

public ContainerWarehouseInput(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  storageTile = (TileWarehouseInput) player.worldObj.getTileEntity(x, y, z);
  int x1, y1;
  for(int i = 0; i < storageTile.getSizeInventory(); i++)
    {
    x1 = (i%9) * 18 + 8;
    y1 = (i/9) * 18 + 8;
    addSlotToContainer(new Slot(storageTile, i, x1, y1));
    }  
  int guiHeight = 8 + 8 + 18*(storageTile.getSizeInventory()/9);
  addPlayerSlots(player, 8, guiHeight, 4);    
  }

}
