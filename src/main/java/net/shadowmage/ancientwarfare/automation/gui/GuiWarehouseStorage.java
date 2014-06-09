package net.shadowmage.ancientwarfare.automation.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseInterfaceFilter;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.WarehouseStorageFilter;
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

public GuiWarehouseStorage(ContainerBase par1Container)
  {
  super(par1Container, 178, 240, defaultBackground);
  this.container = (ContainerWarehouseStorage)par1Container;
  this.ySize = container.guiHeight;
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
  List<WarehouseStorageFilter> filters = container.filters;
  
  int totalHeight = 8;
  
  ItemSlot slot;
  Label label;  
  Button button;  
  String name;
  
  for(WarehouseStorageFilter filter : filters)
    {    
    slot = new FilterItemSlot(8, totalHeight, filter, this);
    area.addGuiElement(slot);
    
    name = filter.getFilterItem()==null? "" : filter.getFilterItem().getDisplayName();
    
    label = new Label(20+8, totalHeight+4, name);
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
        WarehouseStorageFilter filter = new WarehouseStorageFilter(null);
        container.filters.add(filter);
        container.sendFiltersToServer();
        refreshGui();
        }
      };
    area.addGuiElement(button);
    totalHeight+=12;
    }  
  area.setAreaSize(totalHeight);
  }

private class FilterRemoveButton extends Button
{
WarehouseStorageFilter filter;
public FilterRemoveButton(int topLeftX, int topLeftY, WarehouseStorageFilter filter)
  {
  super(topLeftX, topLeftY, 12, 12, "-");
  this.filter = filter;
  }
@Override
protected void onPressed()
  {
  container.filters.remove(filter);
  container.sendFiltersToServer();
  refreshGui();
  }
}

private class FilterItemSlot extends ItemSlot
{
WarehouseStorageFilter filter;
public FilterItemSlot(int topLeftX, int topLeftY, WarehouseStorageFilter filter, ITooltipRenderer render)
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
  filter.setFilterItem(in==null? null : in.copy());
  container.sendFiltersToServer();
  }
}

}
