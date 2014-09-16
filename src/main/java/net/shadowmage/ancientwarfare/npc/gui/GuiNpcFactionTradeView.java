package net.shadowmage.ancientwarfare.npc.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeView;
import net.shadowmage.ancientwarfare.npc.trade.FactionTrade;

public class GuiNpcFactionTradeView extends GuiContainerBase
{

Button inventoryButton;
Button setupButton;
CompositeScrolled area;

public final ContainerNpcFactionTradeView container;
public GuiNpcFactionTradeView(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcFactionTradeView) container;  
  }

@Override
public void initElements()
  {
  int areaYSize = player.capabilities.isCreativeMode ? ySize - 24 - 16 - 4 - 4*18 : ySize - 16 - 4 - 4*18;
  area = new CompositeScrolled(0, player.capabilities.isCreativeMode? 24 : 0, xSize, areaYSize);
  inventoryButton = new Button(8, 8, (256-16)/2, 12, StatCollector.translateToLocal("guistrings.inventory"))
    {
    @Override
    protected void onPressed()
      {
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.trader.getEntityId(), 0, 0);
      }
    };
  setupButton = new Button(8+((256-16)/2), 8, (256-16)/2, 12, StatCollector.translateToLocal("guistrings.trade_setup"))
    {
    @Override
    protected void onPressed()
      {
      NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_FACTION_TRADE_SETUP, container.trader.getEntityId(), 0, 0);
      }
    };
  }

@Override
public void setupElements()
  {
  clearElements();
  addGuiElement(new Label(8+9*18+8+8, 240-4-8-4*18, StatCollector.translateToLocal("guistrings.input")));
  addGuiElement(area);
  if(player.capabilities.isCreativeMode)
    {
    addGuiElement(inventoryButton);
    addGuiElement(setupButton);
    }
  addTrades();
  }

private void addTrades()
  {
  area.clearElements();
  
  List<FactionTrade> trades = new ArrayList<FactionTrade>();
  container.tradeList.getTrades(trades);
    
  int totalHeight = 8;  
  for(int i = 0; i < trades.size(); i++)
    {    
    totalHeight = addTrade(trades.get(i), i, totalHeight);
    }
  
  area.setAreaSize(totalHeight);
  }

private int addTrade(final FactionTrade trade, final int tradeNum, int startHeight)
  {
  if(trade.getCurrentAvailable()<=0){return startHeight;}
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
  
  Button tradeButton = new Button(8+6*18+9+8, startHeight+17, 70, 20, StatCollector.translateToLocal("guistrings.trade"))
    {
    @Override
    protected void onPressed()
      {
      trade.performTrade(player, container.tradeInput);//TODO create input slot trade inventory
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("doTrade", tradeNum);
      sendDataToContainer(tag);
      refreshGui();
      }
    };
  area.addGuiElement(tradeButton);
  
  Label available = new Label(8+6*18+9+8, startHeight, StatCollector.translateToLocal("guistrings.trades_available")+": "+String.valueOf(trade.getCurrentAvailable()));
  area.addGuiElement(available);
    
  startHeight += 18*3;//input/output grid size
  area.addGuiElement(new Line(0, startHeight+1, xSize, startHeight+1, 1, 0x000000ff));
  startHeight += 5;//separator line and padding
  return startHeight;
  }

private void addTradeInputSlot(final FactionTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getInput()[slotNum];
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this);
  area.addGuiElement(slot);
  }

private void addTradeOutputSlot(final FactionTrade trade, int x, int y, final int slotNum)
  {
  ItemStack stack = trade.getOutput()[slotNum];
  stack = stack==null? null : stack.copy();
  final ItemSlot slot = new ItemSlot(x, y, stack, this);
  area.addGuiElement(slot);
  }

}
