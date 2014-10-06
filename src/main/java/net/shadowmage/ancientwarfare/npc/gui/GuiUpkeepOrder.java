package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.container.ContainerUpkeepOrder;

public class GuiUpkeepOrder extends GuiContainerBase
{

boolean hasChanged = false;

ContainerUpkeepOrder container;
public GuiUpkeepOrder(ContainerBase container)
  {
  super(container, 8+60+55+55+60+8, 8+12+10+8, defaultBackground);
  this.container = (ContainerUpkeepOrder)container;
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {  
  clearElements();
  BlockPosition pos = container.upkeepOrder.getUpkeepPosition();
  ItemSlot slot;
  Button button;
  Label label;
  
  if(pos!=null && container.upkeepOrder.getBlock()!=null)
    {
    ItemStack blockStack = new ItemStack(Item.getItemFromBlock(container.upkeepOrder.getBlock()));
    slot = new ItemSlot(8,10,blockStack,this);
    addGuiElement(slot);
    
    label = new Label(8+18+4, 8, String.valueOf(pos));
    addGuiElement(label);
    
    button = new Button(8+18+10, 8+10, 55,12, "guistrings.npc.remove_upkeep_point")
      {
      @Override
      protected void onPressed()
        {
        container.upkeepOrder.removeUpkeepPoint();
        hasChanged = true;
        refreshGui();
        }
      };

    addGuiElement(button);
      
    button = new Button(8+18+55+20, 8+10, 55, 12, getSideName(container.upkeepOrder.getUpkeepBlockSide()))
      {
      @Override
      protected void onPressed()
        {
        container.upkeepOrder.changeBlockSide();
        setText(getSideName(container.upkeepOrder.getUpkeepBlockSide()));
        hasChanged = true;
        refreshGui();
        }
      };
    addGuiElement(button);   
    
    label = new Label(8+18+55+55+30,8, "guistrings.npc.upkeep_time");
    addGuiElement(label);
    
    NumberInput input = new NumberInput(8+18+55+55+30, 8+10, 60, (float)container.upkeepOrder.getUpkeepAmount()/1200.f, this)
      {
      @Override
      public void onValueUpdated(float value)
        {
        float val = value * 1200.f;
        container.upkeepOrder.setUpkeepAmount((int)val);   
        hasChanged = true;      
        }
      };
    addGuiElement(input);
    }
  else
    {
    label = new Label(8,8, "guistrings.npc.assign_upkeep_point");
    addGuiElement(label);
    }
  }

private String getSideName(int side)
  {
  switch(side)
  {
  case 0:
  return "guistrings.inventory.direction.down";
  case 1:
  return "guistrings.inventory.direction.up";
  case 2:
  return "guistrings.inventory.direction.north";
  case 3:
  return "guistrings.inventory.direction.south";
  case 4:
  return "guistrings.inventory.direction.west";
  case 5:
  return "guistrings.inventory.direction.east";
  }
  return "";
  }

@Override
protected boolean onGuiCloseRequested()
  {
  if(hasChanged)
    {
    NBTTagCompound outer = new NBTTagCompound();
    outer.setTag("upkeepOrder", container.upkeepOrder.writeToNBT(new NBTTagCompound()));
    sendDataToContainer(outer);
    }  
  return super.onGuiCloseRequested();
  }

}
