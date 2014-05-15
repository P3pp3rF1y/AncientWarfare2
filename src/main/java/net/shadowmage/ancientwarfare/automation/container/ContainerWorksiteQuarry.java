package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.InventorySided;
import net.shadowmage.ancientwarfare.core.inventory.ItemSlotFilter;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWorksiteQuarry extends ContainerWorksiteBase
{

public int guiHeight, topLabel, playerLabel;

public ContainerWorksiteQuarry(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  InventorySided inventory = worksite.inventory;
    
  int x1, y1, xPos, yPos, slotNum;  
  SlotFiltered slot;
  ItemSlotFilter filter;
  
  int layerY = 8;
  int labelGap = 12;
  topLabel = layerY;
  layerY+=labelGap;
  for(int i = 0; i<27; i++)
    {
    filter = inventory.getFilterForSlot(i);
    slotNum = i;
    x1 = i%9;
    y1 = i/9;
    xPos = x1*18 + 8;
    yPos = y1*18 + layerY;
    slot = new SlotFiltered(inventory, slotNum, xPos, yPos, filter);
    addSlotToContainer(slot);
    }
  layerY += 3*18 + 4; 
  playerLabel = layerY;
  layerY+=labelGap;  
  guiHeight = addPlayerSlots(player, 8, layerY, 4)+8;  
  }

}
