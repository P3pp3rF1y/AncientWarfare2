package net.shadowmage.ancientwarfare.npc.gui;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;

public class GuiNpcPlayerOwnedTrade extends GuiContainerBase
{

CompositeScrolled area;
public final ContainerNpcPlayerOwnedTrade container;
boolean owner;
public GuiNpcPlayerOwnedTrade(ContainerBase container)
  {
  super(container, 256, 240, defaultBackground);
  this.container = (ContainerNpcPlayerOwnedTrade) container; 
  }

@Override
public void initElements()
  {
  int areaSize = ySize-8-4-8-4*18;
  int areaY = 0;
  if(player.getCommandSenderName().equals(container.trader.getOwnerName()))
    {
    areaSize -= 12 + 8 + 4;
    areaY = 12+8+4;
    owner = true;
    }
  area = new CompositeScrolled(0, areaY, xSize, areaSize);  
  }

@Override
public void setupElements()
  {
  clearElements();
  if(owner)
    {
    Button inventory = new Button(8, 8, 240, 12, "guistrings.inventory")
      {
      @Override
      protected void onPressed()
        {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.trader.getEntityId(), 0, 0);
        }
      };
    addGuiElement(inventory);
    }
  addGuiElement(new Label(8+9*18+8+8, 240-4-8-4*18, "guistrings.input"));
  addGuiElement(area);
  if(container.storage!=null)
    {
    addTrades();    
    }
  else
    {
    //TODO add label/message stating that trades must be configured for this NPC
    }
  }

private void addTrades()
  {
  ArrayList<POTrade> trades = new ArrayList<POTrade>();  
  container.tradeList.getTrades(trades);
  int totalHeight = 8;
  
  POTrade trade;
  for(int i = 0; i < trades.size(); i++)
    {
    trade = trades.get(i);
    if(trade.isAvailable(container.storage))
      {
      totalHeight = addTrade(trade, i, totalHeight);      
      }
    }
  area.setAreaSize(totalHeight);
  }

private int addTrade(final POTrade trade, final int tradeIndex, int startHeight)
  {
  int gridX, gridY, slotX, slotY;  
  gridX=0;
  gridY=0;  
  for(int i = 0; i < 9; i++)
    {
    slotX = gridX*18 + 8;
    slotY = gridY*18 + startHeight;
    addTradeInputSlot(trade, slotX, slotY, i);
    slotX += 3*18 + 9;
    addTradeOutputSlot(trade, slotX, slotY, i);
    gridX++;
    if(gridX>=3)
      {
      gridX=0;
      gridY++;
      }
    if(gridY>=3){break;}
    }

  area.addGuiElement(new Label(8+3*18+1, startHeight+20, "="));
  
  Button tradeButton = new Button(8+6*18+9+8, startHeight+17, 70, 20, "guistrings.trade")
    {
    @Override
    protected void onPressed()
      {
      trade.perfromTrade(player, container.tradeInput, container.storage);
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("doTrade", tradeIndex);
      sendDataToContainer(tag);
      refreshGui();
      }
    };
  area.addGuiElement(tradeButton);
  
  startHeight += 18*3;
  area.addGuiElement(new Line(0, startHeight+1, xSize, startHeight+1, 1, 0x000000ff));
  startHeight += 5;
  return startHeight;
  }

private void addTradeInputSlot(final POTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getInputStack(slotNum);
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this);
  area.addGuiElement(slot);
  }

private void addTradeOutputSlot(final POTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getOutputStack(slotNum);
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this);
  area.addGuiElement(slot);
  }


}
