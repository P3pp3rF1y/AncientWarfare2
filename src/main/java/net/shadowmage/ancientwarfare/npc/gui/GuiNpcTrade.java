package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcTrade;

public class GuiNpcTrade extends GuiNpcBase
{

Button nextButton, prevButton;
ContainerNpcTrade tradeContainer;
public GuiNpcTrade(ContainerBase container)
  {
  super(container);
  this.xSize=178;  
  this.ySize=236;
  this.tradeContainer = (ContainerNpcTrade)container;
  }


@Override
public void initElements()
  {
  prevButton = new Button(8,8,55,12,"foo.prev")
    {
    @Override
    protected void onPressed()
      {      
      tradeContainer.prevTrade();
      refreshGui();
      }
    };
  
  nextButton = new Button(xSize-8-55, 8,55,12,"foo.next")
    {
    @Override
    protected void onPressed()
      {
      tradeContainer.nextTrade();
      refreshGui();
      }
    };
  }

@Override
public void setupElements()
  {
  clearElements();
  addGuiElement(nextButton);
  addGuiElement(prevButton);
  if(tradeContainer.currentTrade!=null)
    {
    ItemSlot slot;
    slot = new ItemSlot(8+7*18, 8+12+8+18, tradeContainer.currentTrade.getResult(), this);
    addGuiElement(slot);
    
    int index = 0;
    int xPos, yPos;
    for(ItemStack stack : tradeContainer.currentTrade.getInput())
      {
      xPos = (index%3)*18 + 8 + 18;
      yPos = (index/3)*18 + 8 + 12 + 8;
      slot = new ItemSlot(xPos, yPos, stack, this);
      addGuiElement(slot);
      index++;
      }    
    }
  }

}
