package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.ViewableSlot;

public class ContainerWorksiteTest extends ContainerBase
{

public TileWorksiteBase worksite;
public int guiHeight;//used by GUI to dynamically alter gui height depending upon size of worksite inventory
public int playerSlotsLabelHeight;

public ContainerWorksiteTest(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (TileWorksiteBase)player.worldObj.getTileEntity(x, y, z);
  playerSlotsLabelHeight = addWorksiteInventorySlots(8);
  guiHeight = this.addPlayerSlots(player, 8, playerSlotsLabelHeight+12, 4);//+12 is for offset for label
  }

protected int addWorksiteInventorySlots(int topY)
  {  
  int lowestY = topY;
  for(RelativeSide side : RelativeSide.values())
    {
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    for(ViewableSlot slot : slotMap.getSlots())
      {
      addSlotToContainer(new Slot(worksite.inventory, slot.slotNumber, slotMap.guiX + slot.viewX, slotMap.guiY+slot.viewY +12));
      if(slotMap.guiY+slot.viewY + 12 >lowestY)
        {
        lowestY = slotMap.guiY+slot.viewY + 12;
        }
      }    
    }  
  return lowestY + 18 + 4;
  }

}
