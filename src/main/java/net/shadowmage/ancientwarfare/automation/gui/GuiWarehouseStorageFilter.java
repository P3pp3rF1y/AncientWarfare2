package net.shadowmage.ancientwarfare.automation.gui;

import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseStorageFilter;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWarehouseStorageFilter extends GuiContainerBase
{

ContainerWarehouseStorageFilter container;
CompositeScrolled area;

public GuiWarehouseStorageFilter(ContainerBase container1)
  {
  super(container1, 178, 240, defaultBackground);
  this.container = (ContainerWarehouseStorageFilter) container1;
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 0, 178, 150);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  filterMap.clear();
  
  Label label = new Label(6, 8, StatCollector.translateToLocal("guistrings.automation.item"));
  area.addGuiElement(label);
    
  label = new Label(28+32+9, 8, StatCollector.translateToLocal("guistrings.automation.ignore_damage"));
  label.setRenderCentered();
  area.addGuiElement(label);
  
  label = new Label(28+32+18+32+9, 8, StatCollector.translateToLocal("guistrings.automation.ignore_nbt"));
  label.setRenderCentered();
  area.addGuiElement(label);
  
  int totalHeight = 20;
  ItemSlot slot;
  Checkbox box;
  for(WarehouseItemFilter filter : container.itemFilters)
    {
    slot = new ItemSlot(8, totalHeight, filter.getFilterItem(), this)
      {
      @Override
      public void onSlotClicked(ItemStack stack)
        {
        if(stack!=null)
          {
          ItemStack copy = stack.copy();
          copy.stackSize = 1;
          setItem(copy);         
          filterMap.get(this).setFilterItem(copy); 
          }
        else
          {
          setItem(null);
          filterMap.get(this).setFilterItem(null);
          }
        }
      };
    area.addGuiElement(slot);
    filterMap.put(slot, filter);
       
    box = new Checkbox(28 + 32, totalHeight-1, 18, 18, "")
      {
      @Override
      public void onToggled()
        {
        filterMap.get(this).setIgnoreDamage(checked());
        }
      };
    box.setChecked(filter.isIgnoreDamage());
    area.addGuiElement(box);
    filterMap.put(box, filter);
    
    box = new Checkbox(28 + 32 + 18 + 32, totalHeight-1, 18, 18, "")
      {
      @Override
      public void onToggled()
        {
        filterMap.get(this).setIgnoreNBT(checked());
        }
      };
    box.setChecked(filter.isIgnoreNBT());
    area.addGuiElement(box);
    filterMap.put(box, filter);
    
    Button button = new Button(128+25, totalHeight+2, 12, 12, "-")
      {
      @Override
      protected void onPressed()
        {
        container.itemFilters.remove(filterMap.get(this));
        refreshGui();
        }
      };
    area.addGuiElement(button);
    filterMap.put(button, filter);
        
    totalHeight+=20;
    }
  
  Button button = new Button(8, totalHeight, 152, 12, StatCollector.translateToLocal("guistrings.automation.new_filter"))
    {
    @Override
    protected void onPressed()
      {
      container.itemFilters.add(new WarehouseItemFilter());
      refreshGui();
      }
    };
  area.addGuiElement(button);
  totalHeight+=12;
  
  area.setAreaSize(totalHeight+8);
  }

private HashMap<GuiElement, WarehouseItemFilter> filterMap = new HashMap<GuiElement, WarehouseItemFilter>();

@Override
protected boolean onGuiCloseRequested()
  {
  container.sendDataToServer();
  TileEntity te = (TileEntity)container.storageTile;
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WAREHOUSE_STORAGE, te.xCoord, te.yCoord, te.zCoord);
  return false;
  }

}
