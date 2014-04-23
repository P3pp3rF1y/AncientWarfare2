package net.shadowmage.ancientwarfare.automation.container;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.ViewableSlot;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWorksiteBase extends ContainerBase
{

public TileWorksiteBase worksite;
public int guiHeight;//used by GUI to dynamically alter gui height depending upon size of worksite inventory
public int playerSlotsLabelHeight;

public ContainerWorksiteBase(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (TileWorksiteBase)player.worldObj.getTileEntity(x, y, z);
  playerSlotsLabelHeight = addWorksiteInventorySlots(8);
  guiHeight = this.addPlayerSlots(player, 8, playerSlotsLabelHeight+12, 4);//+12 is for offset for label
  if(worksite.hasAltSetupGui()!=null)
    {
    guiHeight+=12;
    }
  }

protected int addWorksiteInventorySlots(int topY)
  {  
  int lowestY = topY;
  for(InventorySide side : InventorySide.values())
    {
    if(side==InventorySide.NONE){continue;}
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    for(ViewableSlot slot : slotMap.getSlots())
      {
      addSlotToContainer(new SlotFiltered(worksite.inventory, slot.slotNumber, slotMap.guiX + slot.viewX, slotMap.slotY+slot.viewY, worksite.inventory.getFilterForSlot(slot.slotNumber)));
      if(slotMap.slotY+slot.viewY>lowestY)
        {
        lowestY = slotMap.slotY+slot.viewY;
        }
      }    
    }  
  return lowestY + 18 + 4;
  }

@Override
public void handlePacketData(NBTTagCompound tag)
  {
  if(tag.hasKey("openAltGui"))
    {
    worksite.openAltGui(player);
    }
  }

}
