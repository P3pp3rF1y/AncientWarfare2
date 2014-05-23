package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.container.ContainerUpkeepOrder;

public class GuiUpkeepOrder extends GuiContainerBase
{

boolean hasChanged = false;

ContainerUpkeepOrder container;
public GuiUpkeepOrder(ContainerBase container)
  {
  super(container, 256, 8+18+8, defaultBackground);
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
    slot = new ItemSlot(8,8,blockStack,this);
    addGuiElement(slot);
    
    label = new Label(8+18, 8 + 4, String.valueOf(pos));
    addGuiElement(label);
    
    button = new Button(8+18+60,8+3, 55,12, "guistrings.npc.remove_upkeep_point")
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
      
    button = new Button(8+18+60+55, 8+3, 55, 12, "guistrings.npc.side_change")
      {
      @Override
      protected void onPressed()
        {
        container.upkeepOrder.changeBlockSide();
        hasChanged = true;
        refreshGui();
        }
      };
    addGuiElement(button);      
    }
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
