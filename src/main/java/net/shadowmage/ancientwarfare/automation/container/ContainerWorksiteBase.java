package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBasic;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWorksiteBase extends ContainerBase
{

public final TileWorksiteBasic worksite;
public final InventorySided inventory;
public int guiHeight, topLabel, frontLabel, bottomLabel, rearLabel, leftLabel, rightLabel, playerLabel;

public ContainerWorksiteBase(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (TileWorksiteBasic)player.worldObj.getTileEntity(x, y, z);
  inventory = worksite.inventory;
  }

protected int addSlots(int xPosStart, int yPosStart, int firstSlotIndex, int numberOfSlots)
  {
  ItemSlotFilter filter;
  SlotFiltered slot;
  int x1, y1, xPos, yPos;
  int maxY = 0;
  for(int i = 0, slotNum = firstSlotIndex; i < numberOfSlots; i++, slotNum++)
    {
    filter = inventory.getFilterForSlot(slotNum);
    x1 = i%9;
    y1 = i/9;
    xPos = xPosStart + x1*18;
    yPos = yPosStart + y1*18;
    if(yPos+18>maxY)
      {
      maxY=yPos+18;
      }
    slot = new SlotFiltered(inventory, slotNum, xPos, yPos, filter);
    addSlotToContainer(slot);
    }
  return maxY;
  }

/**
 * @return should always return null for normal implementation, not sure wtf the rest of the code is about
 */
@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  int slots = worksite.getSizeInventory();
  Slot slot = (Slot)this.inventorySlots.get(slotClickedIndex);
  if(slot==null || !slot.getHasStack()){return null;}
  ItemStack stackFromSlot = slot.getStack();
  if(slotClickedIndex < slots)
    {
    this.mergeItemStack(stackFromSlot, slots, slots+36, false);
    }
  else
    {
    this.mergeItemStack(stackFromSlot, 0, slots, true);
    }
  if(stackFromSlot.stackSize == 0)
    {
    slot.putStack((ItemStack)null);
    }
  else
    {
    slot.onSlotChanged();
    }
  return null;  
  }


}
