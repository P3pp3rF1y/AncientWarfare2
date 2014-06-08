package net.shadowmage.ancientwarfare.automation.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseOutput;
import net.shadowmage.ancientwarfare.automation.tile.warehouse.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class GuiWarehouseOutput extends GuiContainerBase
{

CompositeScrolled area;
ContainerWarehouseOutput container;
List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();

public GuiWarehouseOutput(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  this.container = (ContainerWarehouseOutput)par1Container;  
  for(WarehouseItemFilter filter : container.tile.getFilters())
    {
    this.itemFilters.add(filter.copy());
    }
  }

@Override
public void updateScreen()
  {
  if(itemFilters.size()!=container.tile.getFilters().size())
    {
    refreshGui();
    }
  else
    {
    WarehouseItemFilter filter1, filter2;
    boolean shouldUpdate = false;
    for(int i = 0; i<itemFilters.size(); i++)
      {
      filter1 = itemFilters.get(i);
      filter2 = container.tile.getFilters().get(i);
      if(filter1.getFilterQuantity()!=filter2.getFilterQuantity() || !InventoryTools.doItemStacksMatch(filter1.getFilterItem(), filter2.getFilterItem()))
        {
        shouldUpdate = true;
        break;
        }
      } 
    if(shouldUpdate)
      {
      itemFilters.clear();
      itemFilters.addAll(container.tile.getFilters());
      }
    }
  super.updateScreen();
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 0, xSize, 88);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  List<WarehouseItemFilter> filters = itemFilters;
  
  int totalHeight = 8;
  
  ItemSlot slot;
  NumberInput input;  
  Button button;  
  
  for(WarehouseItemFilter filter : filters)
    {    
    slot = new FilterItemSlot(8, totalHeight, filter, this);
    area.addGuiElement(slot);
    
    input = new FilterQuantityInput(8+30, totalHeight+3, filter);    
    input.setIntegerValue();
    area.addGuiElement(input);
    
    button = new FilterRemoveButton(xSize-16-12, totalHeight+3, filter);
    area.addGuiElement(button);
    
    totalHeight+=18;
    }
  
  if(filters.size()<9)
    {
    button = new Button(8, totalHeight, 95, 12, StatCollector.translateToLocal("guistrings.automation.new_filter"))
      {
      @Override
      protected void onPressed()
        {
        WarehouseItemFilter filter = new WarehouseItemFilter();
        filter.setFilterQuantity(64);
        itemFilters.add(filter);
        sendFiltersToServer();
        refreshGui();
        }
      };
    area.addGuiElement(button);
    totalHeight+=12;
    }
  
  area.setAreaSize(totalHeight);
  }

protected void sendFiltersToServer()//should be called whenever filters change, so that the base container/etc can be updated
  {
  container.tile.setFilters(itemFilters);
  NBTTagList filterTagList = WarehouseItemFilter.writeFilterList(itemFilters);
  NBTTagCompound tag = new NBTTagCompound();
  tag.setTag("filterList", filterTagList);
  sendDataToContainer(tag);
  }

private class FilterRemoveButton extends Button
{
WarehouseItemFilter filter;
public FilterRemoveButton(int topLeftX, int topLeftY, WarehouseItemFilter filter)
  {
  super(topLeftX, topLeftY, 12, 12, "-");
  this.filter = filter;
  }

@Override
protected void onPressed()
  {
  itemFilters.remove(filter);
  sendFiltersToServer();
  refreshGui();
  }
}

private class FilterQuantityInput extends NumberInput
{
WarehouseItemFilter filter;
public FilterQuantityInput(int topLeftX, int topLeftY, WarehouseItemFilter filter)
  {
  super(topLeftX, topLeftY, 40, filter.getFilterQuantity(), GuiWarehouseOutput.this);
  this.filter = filter;
  }

@Override
public void onValueUpdated(float value)
  {
  int val = (int)value;
  this.filter.setFilterQuantity(val);
  refreshGui();
  sendFiltersToServer();
  }

}

private class FilterItemSlot extends ItemSlot
{
WarehouseItemFilter filter;
public FilterItemSlot(int topLeftX, int topLeftY, WarehouseItemFilter filter, ITooltipRenderer render)
  {
  super(topLeftX, topLeftY, filter.getFilterItem(), render);
  this.filter = filter;
  this.setRenderItemQuantity(false);
  }

@Override
public void onSlotClicked(ItemStack stack)
  {
  ItemStack in = stack==null? null : stack.copy();
  this.setItem(in);  
  if(in!=null)
    {
    in.stackSize = 1;
    }
  filter.setFilterQuantity(0);
  filter.setFilterItem(in==null? null : in.copy());
  sendFiltersToServer();
  }
}

}
