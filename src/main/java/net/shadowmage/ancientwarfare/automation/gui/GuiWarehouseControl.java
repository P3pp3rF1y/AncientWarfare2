package net.shadowmage.ancientwarfare.automation.gui;

import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWarehouseControl;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools.ItemStackHashWrap;

public class GuiWarehouseControl extends GuiContainerBase
{

CompositeScrolled area;
ContainerWarehouseControl container;
boolean itemViewMode = true;

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
  Checkbox modeBox = new Checkbox(256, 8, 16,16, "itemViewMode")
    {
    @Override
    public void onToggled()
      {
      itemViewMode = checked();
      refreshGui();
      }
    };
  modeBox.setChecked(itemViewMode);
  addGuiElement(modeBox);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  if(itemViewMode)
    {
    addInventoryViewElements();
    }
  else
    {
    addFilterViewElements();
    } 
  }

private void addInventoryViewElements()
  {
  ItemSlot slot;
  int qty;
  ItemStack stack;
  int x = 0, y= 0;
  int totalSize = 8;
  for(ItemStackHashWrap wrap : container.itemMap.keySet())
    {
    qty = container.itemMap.get(wrap);
    stack = wrap.createItemStack();
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

private void addFilterViewElements()
  {
  Label label;
  Line line;  
  ItemSlot slot;
  String name = "";  
   

  name = StatCollector.translateToLocal("guistrings.automation.filter_name");
  label = new Label(8, 8, name);
  area.addGuiElement(label);
  
  name = StatCollector.translateToLocal("guistrings.automation.quantity");
  label = new Label(180-fontRendererObj.getStringWidth(name), 8, name);
  area.addGuiElement(label);
  
  name = StatCollector.translateToLocal("guistrings.automation.ignore_damage");
  label = new Label(220, 8, name);
  label.setRenderCentered();
  area.addGuiElement(label);
  
  name = StatCollector.translateToLocal("guistrings.automation.ignore_nbt");
  label = new Label(280, 8, name);
  label.setRenderCentered();
  area.addGuiElement(label);
  
  line = new Line(3, 18,  xSize-8,18, 2, 0x000000ff);
  area.addGuiElement(line);  

  int totalHeight = 22;
  Iterator<IWarehouseStorageTile> it = container.storageTiles.iterator();
  IWarehouseStorageTile tile;
  while(it.hasNext() && (tile = it.next())!=null)
    {    
    label = new Label(8, totalHeight, tile.getInventoryName());
    area.addGuiElement(label);
    totalHeight+=12;
    
    for(WarehouseItemFilter filter : tile.getFilters())
      {            
      slot = new FilterSlot(8, totalHeight, filter.getFilterItem(), tile, filter);      
      area.addGuiElement(slot);
      
      name = filter.getFilterItem()==null? "Empty Filter" : filter.getFilterItem().getDisplayName();
      label = new Label(8+20, totalHeight+4, name);
      area.addGuiElement(label);
      
      name = String.valueOf(filter.getItemCount());
      label = new Label(180-fontRendererObj.getStringWidth(name), totalHeight+4, name);
      area.addGuiElement(label);
      
      name = filter.isIgnoreDamage()? "X" : "";
      label = new Label(220, totalHeight+4, name);
      label.setRenderCentered();
      area.addGuiElement(label);
      
      name = filter.isIgnoreNBT()? "X" : "";
      label = new Label(280, totalHeight+4, name);
      label.setRenderCentered();
      area.addGuiElement(label);
      
      totalHeight+=18;
      }
    
    if(it.hasNext())
      {
      totalHeight+=2;    
      line = new Line(3, totalHeight, xSize-8, totalHeight, 1, 0x000000ff);
      area.addGuiElement(line);
      totalHeight+=4;    
      }    
    }
  area.setAreaSize(totalHeight+8);
  }

private class FilterSlot extends ItemSlot
{
IWarehouseStorageTile tile;
WarehouseItemFilter filter;
public FilterSlot(int topLeftX, int topLeftY, ItemStack item, IWarehouseStorageTile tile, WarehouseItemFilter filter)
  {
  super(topLeftX, topLeftY, item, GuiWarehouseControl.this);
  this.tile = tile;
  this.filter = filter;
  }

@Override
public void onSlotClicked(ItemStack stack)
  {
  TileEntity te = (TileEntity)tile;
  BlockPosition pos = new BlockPosition(te.xCoord, te.yCoord, te.zCoord);
  AWLog.logDebug("filter slot clicked..."+te+" filter: "+filter);
  
  if(filter.getFilterItem()!=null)
    {
    NBTTagCompound tag = new NBTTagCompound();
    NBTTagCompound reqTag = new NBTTagCompound();
    reqTag.setTag("reqPos", pos.writeToNBT(new NBTTagCompound()));
    reqTag.setTag("reqItem", filter.getFilterItem().writeToNBT(new NBTTagCompound()));  
    reqTag.setBoolean("dmg", filter.isIgnoreDamage());
    reqTag.setBoolean("nbt", filter.isIgnoreDamage());
    tag.setTag("request", reqTag);
    sendDataToContainer(tag);    
    }
  }
}
}
