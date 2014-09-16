package net.shadowmage.ancientwarfare.npc.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;
import net.shadowmage.ancientwarfare.npc.trade.FactionTradeList;

public class GuiNpcFactionTradeSetup extends GuiContainerBase
{

CompositeScrolled area;
FactionTradeList tradeList;

public final ContainerNpcFactionTradeSetup container;
public GuiNpcFactionTradeSetup(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcFactionTradeSetup) container;
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 0, xSize, ySize - 16 - 4 - 4*18);
  addGuiElement(area);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  tradeList = container.tradeList;
  if(tradeList==null){return;}
    
  List<FactionTrade> trades = new ArrayList<FactionTrade>();
  tradeList.getTrades(trades);
  
  int totalHeight = 8;
  for(int i = 0; i < trades.size(); i++)
    {
    totalHeight = addTrade(trades.get(i), totalHeight);
    }
  
  Button newTradeButton = new Button(8, totalHeight, xSize-8-16, 12, "guistrings.new_trade")
    {
    @Override
    protected void onPressed()
      {
      tradeList.addNewTrade();
      container.tradesChanged=true;
      refreshGui();
      }
    };
  area.addGuiElement(newTradeButton);
  totalHeight+=12;
  
  area.setAreaSize(totalHeight);
  }

private int addTrade(final FactionTrade trade, int startHeight)
  {
  int gridX, gridY, slotX, slotY;  
  gridX=0;
  gridY=0;  
  for(int i = 0; i < 9; i++)
    {
    slotX = gridX*18 + 8;
    slotY = gridY*18 + startHeight;
    addTradeInputSlot(trade, slotX, slotY, i);
    slotX += 4*18;
    addTradeOutputSlot(trade, slotX, slotY, i);
    gridX++;
    if(gridX>=3)
      {
      gridX=0;
      gridY++;
      }
    if(gridY>=3){break;}
    }
  
  startHeight += 18*3;//input/output grid size
  area.addGuiElement(new Line(0, startHeight+1, xSize, startHeight+1, 1, 0x000000ff));
  startHeight += 5;//separator line and padding
  return startHeight;
  }

private void addTradeInputSlot(final FactionTrade trade, int x, int y, final int slotNum)
  {
  final ItemSlot slot = new ItemSlot(x, y, trade.getInput()[slotNum], this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      setItem(stack);
      trade.getInput()[slotNum]=stack;
      container.tradesChanged=true;
      }
    };
  area.addGuiElement(slot);
  }

private void addTradeOutputSlot(final FactionTrade trade, int x, int y, final int slotNum)
  {
  final ItemSlot slot = new ItemSlot(x, y, trade.getOutput()[slotNum], this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      setItem(stack);
      trade.getOutput()[slotNum]=stack;
      container.tradesChanged=true;
      }
    };
  area.addGuiElement(slot);
  }

}
