package net.shadowmage.ancientwarfare.automation.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

public class GuiWarehouseStorage extends GuiContainerBase
{

CompositeScrolled area;

ContainerWarehouseStorage container;

List<WarehouseItemFilter> itemFilters = new ArrayList<WarehouseItemFilter>();

public GuiWarehouseStorage(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  this.container = (ContainerWarehouseStorage)par1Container;
  this.ySize = container.guiHeight;
  itemFilters.addAll(container.tile.getFilters());
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 0, xSize, container.areaSize);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  itemFilters.clear();
  itemFilters.addAll(container.tile.getFilters());
  List<WarehouseItemFilter> filters = itemFilters;
  
  int totalHeight = 8;
  
  ItemSlot slot;
  Label label;  
  Button button;  
  String name;
  
  for(WarehouseItemFilter filter : filters)
    {    
    slot = new FilterItemSlot(8, totalHeight, filter, this);
    area.addGuiElement(slot);
    
    name = filter.getFilterItem()==null? "" : filter.getFilterItem().getDisplayName();
    
    label = new Label(20+8, totalHeight+4, filter.getFilterQuantity()+" x "+name);
    area.addGuiElement(label);
    
    button = new FilterRemoveButton(xSize-16-12, totalHeight+3, filter);
    area.addGuiElement(button);
    
    totalHeight+=18;
    }
  
  if(filters.size()<10)
    {
    button = new Button(8, totalHeight, 95, 12, StatCollector.translateToLocal("guistrings.automation.new_filter"))
      {
      @Override
      protected void onPressed()
        {
        WarehouseItemFilter filter = new WarehouseItemFilter();
        filter.setFilterQuantity(0);
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

private void sendFiltersToServer()
  {
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

private class FilterItemSlot extends ItemSlot
{
WarehouseItemFilter filter;
public FilterItemSlot(int topLeftX, int topLeftY, WarehouseItemFilter filter, ITooltipRenderer render)
  {
  super(topLeftX, topLeftY, filter.getFilterItem(), render);
  this.filter = filter;
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
