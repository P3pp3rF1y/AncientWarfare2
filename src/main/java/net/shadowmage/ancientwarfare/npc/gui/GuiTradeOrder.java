package net.shadowmage.ancientwarfare.npc.gui;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.npc.container.ContainerTradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;

public class GuiTradeOrder extends GuiContainerBase
{

ContainerTradeOrder container;
CompositeScrolled tradesArea, routeArea, restockArea;
Button tradeButton, routeButton, restockButton;

private Button currentMode;

public GuiTradeOrder(ContainerBase container)
  {
  super(container, 256, 240, defaultBackground);
  this.container = (ContainerTradeOrder)container;
  }

@Override
protected boolean onGuiCloseRequested()
  {
  NBTTagCompound outer = new NBTTagCompound();
  outer.setTag("tradeOrder", container.orders.writeToNBT(new NBTTagCompound()));
  sendDataToContainer(outer);
  return super.onGuiCloseRequested();
  }

@Override
public void initElements()
  {
  tradesArea = new CompositeScrolled(0, 24, xSize, ySize-24-4-4-8-4*18);
  routeArea = new CompositeScrolled(0, 24, xSize, ySize-24-4-4-8-4*18);
  restockArea = new CompositeScrolled(0, 24, xSize, ySize-24-4-4-8-4*18);
  tradeButton = new Button(8, 8, 75, 12, "guistrings.npc.trades")
    {
    @Override
    protected void onPressed()
      {
      currentMode=tradeButton;
      refreshGui();
      }
    };
    
  routeButton = new Button(75+8, 8, 75, 12, "guistrings.npc.route")
    {
    @Override
    protected void onPressed()
      {
      currentMode=routeButton;
      refreshGui();
      }
    };
    
  restockButton = new Button(150+8, 8, 75, 12, "guistrings.npc.restock")
    {
    @Override
    protected void onPressed()
      {
      currentMode=restockButton;
      refreshGui();
      }
    };

  tradeButton.setEnabled(false);
  routeButton.setEnabled(true);
  restockButton.setEnabled(true);
      
  currentMode=tradeButton;
  setTradeMode();
  }

@Override
public void setupElements()
  {
  clearElements();
  addGuiElement(tradeButton);
  addGuiElement(routeButton);
  addGuiElement(restockButton);
  if(currentMode==tradeButton){setTradeMode();}
  else if(currentMode==routeButton){setRouteMode();}
  else if(currentMode==restockButton){setRestockMode();}
  }

private void setTradeMode()
  {
  addGuiElement(tradesArea);
  tradeButton.setEnabled(false);
  routeButton.setEnabled(true);
  restockButton.setEnabled(true);
  setupTradeMode();
  }

private void setRouteMode()
  {
  addGuiElement(routeArea);
  tradeButton.setEnabled(true);
  routeButton.setEnabled(false);
  restockButton.setEnabled(true);  
  setupRouteMode();
  }

private void setRestockMode()
  {
  addGuiElement(restockArea);
  tradeButton.setEnabled(true);
  routeButton.setEnabled(true);
  restockButton.setEnabled(false);
  setupRestockMode();
  }

private void setupTradeMode()
  {  
  tradesArea.clearElements();
  final POTradeList tradeList = container.orders.getTradeList();  
  ArrayList<POTrade> trades = new ArrayList<POTrade>();  
  tradeList.getTrades(trades);
    
  int totalHeight = 8;
  
  for(int i =0; i < trades.size(); i++)
    {
    totalHeight = addTrade(trades.get(i), i, totalHeight);
    }
  
  Button newTradeButton = new Button(8, totalHeight, xSize-20, 12, "guistrings.new_trade")
    {
    @Override
    protected void onPressed()
      {
      tradeList.addNewTrade();
      refreshGui();
      }
    };
  tradesArea.addGuiElement(newTradeButton);
  
  totalHeight+=12;
  tradesArea.setAreaSize(totalHeight);
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
  
  addTradeControls(trade, startHeight, tradeIndex);
  
  startHeight += 18*3;
  tradesArea.addGuiElement(new Line(0, startHeight+1, xSize, startHeight+1, 1, 0x000000ff));
  startHeight += 5;
  return startHeight;
  }

private void addTradeControls(final POTrade trade, int startHeight, final int tradeNum)
  {
  final POTradeList tradeList = container.orders.getTradeList();
  startHeight -= 1;//offset by 1 to lineup better with the item slot boxes, as they align to the inner slot rather than border
  int infoX = 6*18 + 8 + 9 + 4;
  Button upButton = new Button(infoX, startHeight, 55, 12, "guistrings.up")
    {
    @Override
    protected void onPressed()
      {
      tradeList.decrementTrade(tradeNum);
      refreshGui();
      }
    };
  tradesArea.addGuiElement(upButton);
  
  Button downButton = new Button(infoX, startHeight + 3*18 - 12, 55, 12, "guistrings.down")
    {
    @Override
    protected void onPressed()
      {
      tradeList.incrementTrade(tradeNum);
      refreshGui();
      }
    };
  tradesArea.addGuiElement(downButton);
  
  Button delete = new Button(infoX, startHeight + 21, 55, 12, "guistrings.delete")
    {
    @Override
    protected void onPressed()
      {
      tradeList.deleteTrade(tradeNum);
      refreshGui();
      }
    };
  tradesArea.addGuiElement(delete);
  }

private void addTradeInputSlot(final POTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getInputStack(slotNum);
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      stack = stack==null? stack : stack.copy();
      setItem(stack);
      trade.setInputStack(slotNum, stack);
      }
    };
  tradesArea.addGuiElement(slot);
  }

private void addTradeOutputSlot(final POTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getOutputStack(slotNum);
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      stack = stack==null? stack : stack.copy();
      setItem(stack);
      trade.setOutputStack(slotNum, stack);
      }
    };
  tradesArea.addGuiElement(slot);
  }

private void setupRouteMode()
  {
  
  }

private void setupRestockMode()
  {
  
  }

}
