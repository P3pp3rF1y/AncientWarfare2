package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.util.ItemQuantityMap.ItemHashEntry;

public class GuiWarehouseControl extends GuiContainerBase
{

CompositeScrolled area;
ContainerWarehouseControl container;

public GuiWarehouseControl(ContainerBase par1Container)
  {
  super(par1Container, 320, 240, defaultBackground);
  container = (ContainerWarehouseControl)par1Container;  
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 88, 320, 152);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  addInventoryViewElements();
  }

private void addInventoryViewElements()
  {
  ItemSlot slot;
  int qty;
  ItemStack stack;
  int x = 0, y= 0;
  int totalSize = 8;
  for(ItemHashEntry entry : container.itemMap.keySet())
    {
    qty = container.itemMap.getCount(entry);
    stack = entry.getItemStack();
    stack.stackSize = qty;
    
    slot = new ItemSlot(8+x*18, 8+y*18, stack, this)
      {
      @Override
      public void onSlotClicked(ItemStack stack)
        {
        container.handleClientRequestSpecific(getStack());
        }
      };
    area.addGuiElement(slot);
    x++;
    if(x>=16)
      {
      x=0;
      y++;
      totalSize+=18;
      }
    }
  area.setAreaSize(totalSize);
  }

}
