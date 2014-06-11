package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcTrade;

public class GuiNpcTrade extends GuiNpcBase
{

Button inventoryButton;
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
  prevButton = new Button(8,8,55,12, StatCollector.translateToLocal("guistrings.previous"))
    {
    @Override
    protected void onPressed()
      {      
      tradeContainer.prevTrade();
      refreshGui();
      }
    };
  
  nextButton = new Button(xSize-8-55, 8,55,12,StatCollector.translateToLocal("guistrings.next"))
    {
    @Override
    protected void onPressed()
      {
      tradeContainer.nextTrade();
      refreshGui();
      }
    }; 
  
  inventoryButton = new Button(8+55,8,52,12, "foo.inv")
    {
    @Override
    protected void onPressed()
      {
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.npc.getEntityId(), 0, 0);
      }
    };
  }

@Override
public void setupElements()
  {
  clearElements();
  addGuiElement(nextButton);
  addGuiElement(prevButton);
  if(player.capabilities.isCreativeMode || container.npc.getNpcType().equals("trader"))
    {
    addGuiElement(inventoryButton);    
    }
  int wantedSlotsY = 8+12+12;
  addGuiElement(new Label(8, 21, StatCollector.translateToLocal("guistrings.wanted")));
  String label = StatCollector.translateToLocal("guistrings.for");
  addGuiElement(new Label(xSize-8-18-18-fontRendererObj.getStringWidth(label) ,21, label));
  if(tradeContainer.currentTrade!=null)
    {
    ItemSlot slot;
    slot = new ItemSlot(8+7*18, wantedSlotsY+18, tradeContainer.currentTrade.getResult(), this);
    addGuiElement(slot);
    
    int index = 0;
    int xPos, yPos;
    for(ItemStack stack : tradeContainer.currentTrade.getInput())
      {
      xPos = (index%3)*18 + 8 + 18;
      yPos = (index/3)*18 + wantedSlotsY;
      slot = new ItemSlot(xPos, yPos, stack, this);
      addGuiElement(slot);
      index++;
      }    
    }
  }

}
