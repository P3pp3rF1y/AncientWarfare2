package net.shadowmage.ancientwarfare.automation.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeItemSlots;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.inventory.ItemQuantityMap.ItemHashEntry;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortOrder;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ComparatorItemStack.SortType;

public class GuiWarehouseControl extends GuiContainerBase
{

CompositeScrolled area;
ContainerWarehouseControl container;
Button sortChange;
Text input;
Label sortTypeLabel;
Checkbox sortOrderBox;
SortType sortType = SortType.NAME;
SortOrder sortOrder = SortOrder.DESCENDING;
ComparatorItemStack sorter;

public GuiWarehouseControl(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  container = (ContainerWarehouseControl)par1Container;
  sorter = new ComparatorItemStack(sortType, sortOrder);
  }

@Override
public void initElements()
  {  
  sortChange = new Button(xSize-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.automation.sort_type"))
    {
    @Override
    protected void onPressed()
      {
      sortType = sortType.next();
      sortTypeLabel.setText(StatCollector.translateToLocal(sortType.toString()));
      refreshGui();
      }
    };
  addGuiElement(sortChange);
  
  sortTypeLabel = new Label(xSize-8-3*18+4, 22, StatCollector.translateToLocal(sortType.toString()));
  addGuiElement(sortTypeLabel);
  
  sortOrderBox = new Checkbox(xSize-8-3*18+4, 22+12, 16, 16, StatCollector.translateToLocal("guistrings.automation.descending"))
    {
    @Override
    public void onToggled()
      {      
      super.onToggled();
      sortOrder = checked() ? SortOrder.ASCENDING : SortOrder.DESCENDING;
      String name = sortOrder==SortOrder.ASCENDING ? "ascending" : "descending";
      label = StatCollector.translateToLocal("guistrings.automation."+name);  
      refreshGui();    
      }
    };
  addGuiElement(sortOrderBox);
  
  input = new Text(8, 66, 178-16, "", this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      AWLog.logDebug("text input detected...: "+newText);
      if(sortType==SortType.NAME_INPUT)
        {        
        AWLog.logDebug("refreshing GUI for sort..");
        refreshGui();
        }
      }
    };
  addGuiElement(input);
    
  area = new CompositeItemSlots(0, 82, 178, 3*18+16, this);
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
  List<ItemStack> displayStacks = new ArrayList<ItemStack>();
  for(ItemHashEntry entry : container.itemMap.keySet())
    {
    qty = container.itemMap.getCount(entry);
    stack = entry.getItemStack();
    stack.stackSize = qty;
    displayStacks.add(stack);
    }
  
  sortItems(displayStacks);  
  
  for(ItemStack displayStack : displayStacks)
    {
    slot = new ItemSlot(8+x*18, 8+y*18, displayStack, this)
      {
      @Override
      public void onSlotClicked(ItemStack stack)
        {
        container.handleClientRequestSpecific(getStack());
        }
      };
    area.addGuiElement(slot);
    x++;
    if(x>=9)
      {
      x=0;
      y++;
      totalSize+=18;
      }
    }  
  area.setAreaSize(totalSize);
  }

private void sortItems(List<ItemStack> items)
  {
  sorter.setSortType(sortType);
  sorter.setSortOrder(sortOrder);
  sorter.setTextInput(input.getText());
  Collections.sort(items, sorter);
  }

}
