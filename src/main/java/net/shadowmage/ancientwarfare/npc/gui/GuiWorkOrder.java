package net.shadowmage.ancientwarfare.npc.gui;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.npc.container.ContainerWorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder.WorkEntry;

public class GuiWorkOrder extends GuiContainerBase
{

//TODO display work priority type via button
//TODO add number-input for changing work-length

boolean hasChanged = false;
CompositeScrolled area;

ContainerWorkOrder container;
public GuiWorkOrder(ContainerBase container)
  {
  super(container, 256, 240, defaultBackground);
  this.container = (ContainerWorkOrder)container; 
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, xSize, ySize-40);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  List<WorkEntry> entries = container.wo.getEntries();
  ItemStack blockStack;
  ItemSlot slot;
  Label label;
  Button button;
  NumberInput input;
  int totalHeight = 8;
  int index = 0;
  for(WorkEntry entry : entries)
    {    
    blockStack = new ItemStack(Item.getItemFromBlock(entry.getBlock()));
    slot = new ItemSlot(8,totalHeight,blockStack, this);
    area.addGuiElement(slot);
    
    label = new Label (8+20, totalHeight+4, String.valueOf(entry.getPosition()));
    area.addGuiElement(label);
    
    button = new IndexedButton(160, totalHeight+3, 12, 12, "+", index)
      {
      @Override
      protected void onPressed()
        {
        container.wo.incrementPosition(index);
        hasChanged = true;
        refreshGui();
        }
      };
    area.addGuiElement(button);
      
    button = new IndexedButton(160+12, totalHeight+3, 12, 12, "-", index)
      {
      @Override
      protected void onPressed()
        {
        container.wo.decrementPosition(index);
        hasChanged = true;
        refreshGui();
        }
      };
    area.addGuiElement(button);
      
    button = new IndexedButton(160+12+12, totalHeight+3, 35, 12, StatCollector.translateToLocal("guistrings.npc.work_order.delete"), index)
      {
      @Override
      protected void onPressed()
        {
        container.wo.removePosition(index);
        hasChanged = true;
        refreshGui();
        }
      };    
    area.addGuiElement(button);
    
    totalHeight+=18;
    index++;
    }
  area.setAreaSize(totalHeight);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  if(hasChanged)
    {
    NBTTagCompound outer = new NBTTagCompound();
    outer.setTag("wo", container.wo.writeToNBT(new NBTTagCompound()));
    sendDataToContainer(outer);
    }
  return super.onGuiCloseRequested();
  }

private class IndexedButton extends Button
{

int index;

public IndexedButton(int topLeftX, int topLeftY, int width, int height, String text, int index)
  {
  super(topLeftX, topLeftY, width, height, text);
  this.index = index;
  }


}

}
