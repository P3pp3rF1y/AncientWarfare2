package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SlotItemFilter;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.ViewableSlot;
import net.shadowmage.ancientwarfare.core.inventory.SlotFiltered;

public class ContainerWorksiteBase extends ContainerBase
{

public TileWorksiteBase worksite;
public int guiHeight;//used by GUI to dynamically alter gui height depending upon size of worksite inventory
public int playerSlotsLabelHeight;

int[] sideStartIndices;
int[] sideEndIndices;
int totalInventorySize;

public ContainerWorksiteBase(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);
  worksite = (TileWorksiteBase)player.worldObj.getTileEntity(x, y, z);
  playerSlotsLabelHeight = addWorksiteInventorySlots(8);
  guiHeight = this.addPlayerSlots(player, 8, playerSlotsLabelHeight+12, 4);//+12 is for offset for label
  if(worksite.hasAltSetupGui())
    {
    guiHeight+=12;
    }
  sideEndIndices = new int[6];
  sideStartIndices = new int[6];
  InventorySide side;
  int index = 0;
  int length = 0;
  for(int i = 0; i <6 ;i++)
    {
    length = 0;
    side = InventorySide.values()[i];
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);    
    if(slotMap!=null)
      {      
      length = slotMap.getSlots().size();
      }    
    sideStartIndices[i] = index;
    sideEndIndices[i] = index + length;
    AWLog.logDebug("mapping inventory side: "+i+" start: "+sideStartIndices[i]+" end:"+sideEndIndices[i]);
    index+=length;
    }
  totalInventorySize = worksite.getSizeInventory();
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

@Override
public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int slotClickedIndex)
  {
  ItemStack slotStackCopy = null;
  Slot theSlot = (Slot)this.inventorySlots.get(slotClickedIndex);
  SlotItemFilter filter;
  SlotFiltered slot;
  if (theSlot != null && theSlot.getHasStack())
    {
    ItemStack slotStack = theSlot.getStack();
    slotStackCopy = slotStack.copy();
    
    int playerSlotStart = totalInventorySize;    
 
    if(slotClickedIndex<totalInventorySize)//clicked in inventory, merge into player inventory
      {
      if(!this.mergeItemStack(slotStack, playerSlotStart, playerSlotStart+36, false))//merge into player inventory
        {
        return null;
        }
      }
    else//clicked in player inventory, try to merge from bottom up
      {
      int start, end;
      for(int i = 5; i >=0; i--)
        {
        start = sideStartIndices[i];
        end = sideEndIndices[i]; 
        if(start==end){continue;}
        slot = (SlotFiltered) inventorySlots.get(start);
        if(slot.isItemValid(slotStack))
          {
          this.mergeItemStack(slotStack, start, end, false);          
          }       
        if(slotStack.stackSize==0)
          {
          break;
          }
        }
      }
    if (slotStack.stackSize == 0)
      {
      theSlot.putStack((ItemStack)null);
      }
    else
      {
      theSlot.onSlotChanged();
      }
    if (slotStack.stackSize == slotStackCopy.stackSize)
      {
      return null;
      }
    theSlot.onPickupFromSlot(par1EntityPlayer, slotStack);
    }
  return slotStackCopy;  
  }

}
