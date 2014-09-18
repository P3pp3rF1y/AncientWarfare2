package net.shadowmage.ancientwarfare.npc.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.container.ContainerTradeOrder;
import net.shadowmage.ancientwarfare.npc.trade.POTrade;
import net.shadowmage.ancientwarfare.npc.trade.POTradeList;
import net.shadowmage.ancientwarfare.npc.trade.POTradePoint;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData.POTradeDepositEntry;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRestockData.POTradeWithdrawEntry;
import net.shadowmage.ancientwarfare.npc.trade.POTradeRoute;

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

  tradesArea.addGuiElement(new Label(8+3*18+1, startHeight+20, "="));
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
  routeArea.clearElements();
  POTradeRoute route = container.orders.getRoute();  
  int totalHeight = 8;  
  for(int i = 0; i < route.size(); i++)
    {
    totalHeight = addRoutePoint(route.get(i), i, totalHeight);
    }
  routeArea.setAreaSize(totalHeight);
  }

private int addRoutePoint(final POTradePoint point, final int index, int startHeight)
  {
  BlockPosition pos = point.getPosition();
  Label blockName = new Label(8, startHeight, "Unknown Block");
  Label posLabel = new Label(8, startHeight+12, pos.toString());
  if(player.worldObj.blockExists(pos.x, pos.y, pos.z))
    {
    blockName.setText(player.worldObj.getBlock(pos.x, pos.y, pos.z).getUnlocalizedName());
    }
  routeArea.addGuiElement(blockName);
  routeArea.addGuiElement(posLabel);
  
  Button up = new Button(120, startHeight, 55, 12, "guistrings.up")
    {
    @Override
    protected void onPressed()
      {
      POTradeRoute route = container.orders.getRoute();
      route.decrementRoutePoint(index);
      refreshGui();
      }    
    };
  routeArea.addGuiElement(up);
  
  Button down = new Button(120, startHeight+12+12, 55, 12, "guistrings.down")
    {
    @Override
    protected void onPressed()
      {
      POTradeRoute route = container.orders.getRoute();
      route.incrementRoutePoint(index);
      refreshGui();
      } 
    };
  routeArea.addGuiElement(down);
  
  Button delete = new Button(120, startHeight+12, 55, 12, "guistrings.delete")
    {
    @Override
    protected void onPressed()
      {
      POTradeRoute route = container.orders.getRoute();
      route.deleteRoutePoint(index);
      refreshGui();
      }
    };
  routeArea.addGuiElement(delete);
  
  Checkbox upkeep = new Checkbox(120+55+4, startHeight, 12, 12, "guistrings.upkeep")
    {
    @Override
    public void onToggled()
      {
      POTradeRoute route = container.orders.getRoute();
      route.setUpkeep(index, checked());      
      }
    };
  routeArea.addGuiElement(upkeep);
  
  Label delayLabel = new Label(120+55+4, startHeight+12+1, StatCollector.translateToLocal("guistrings.delay")+":");
  routeArea.addGuiElement(delayLabel);
  
  NumberInput delayInput = new NumberInput(120+55+4, startHeight+24, 55, point.getDelay(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      POTradeRoute route = container.orders.getRoute();
      route.setPointDelay(index, (int)value);
      }
    };
  delayInput.setIntegerValue();
  routeArea.addGuiElement(delayInput);
    
  startHeight += 12 + 12 + 12;
  routeArea.addGuiElement(new Line(0, startHeight+2, xSize, startHeight+2, 1, 0x000000ff));
  startHeight += 5;
  return startHeight;
  }

private void setupRestockMode()
  {
  final POTradeRestockData restock = container.orders.getRestockData();
  restockArea.clearElements();
  int totalHeight=8;

  /********************************** DEPOSIT LIST **********************************************/
  restockArea.addGuiElement(new Label(8, totalHeight, "guistrings.deposit"));
  totalHeight+=12;
  restockArea.addGuiElement(new Label(8, totalHeight, restock.getDepositPoint()==null? "guistrings.none" : restock.getDepositPoint().toString()));
  totalHeight+=12;
  restockArea.addGuiElement(new Button(9, totalHeight, 55, 12, Direction.getDirectionFor(restock.getDepositSide()).getTranslationKey())
    {
    @Override
    protected void onPressed()
      {
      // TODO Auto-generated method stub
      super.onPressed();
      }
    });
  totalHeight+=16;
  
  List<POTradeDepositEntry> depositList = restock.getDepositList();
  for(int i = 0; i < depositList.size(); i++)
    {
    totalHeight = addDepositEntry(depositList.get(i), i, totalHeight);
    }
  
  Button newDepositButton = new Button(8, totalHeight, 120, 12, "guistrings.new_deposit")
    {
    @Override
    protected void onPressed()
      {
      restock.addDepositEntry();
      refreshGui();
      }
    };
  restockArea.addGuiElement(newDepositButton);
  totalHeight+=14;
  
  restockArea.addGuiElement(new Line(0, totalHeight, xSize, totalHeight, 2, 0x000000ff));
  totalHeight+=4;
  
  /********************************** WITHDRAW LIST **********************************************/
  
  restockArea.addGuiElement(new Label(8, totalHeight, "guistrings.withdraw"));
  totalHeight+=12;
  restockArea.addGuiElement(new Label(8, totalHeight, restock.getWithdrawPoint()==null? "guistrings.none" : restock.getWithdrawPoint().toString()));
  totalHeight+=12;
  restockArea.addGuiElement(new Button(9, totalHeight, 55, 12, Direction.getDirectionFor(restock.getWithdrawSide()).getTranslationKey())
    {
    @Override
    protected void onPressed()
      {
      // TODO Auto-generated method stub
      super.onPressed();
      }
    });
  totalHeight+=16;
  
  List<POTradeWithdrawEntry> withdrawList = restock.getWithdrawList();
  for(int i = 0; i < withdrawList.size(); i++)
    {
    totalHeight = addWithdrawEntry(withdrawList.get(i), i, totalHeight);
    }
  
  Button newWithdrawButton = new Button(8, totalHeight, 120, 12, "guistrings.new_withdraw")
    {
    @Override
    protected void onPressed()
      {
      restock.addWithdrawEntry();
      refreshGui();
      }
    };
  restockArea.addGuiElement(newWithdrawButton);
  totalHeight+=14;
  
  restockArea.setAreaSize(totalHeight);  
  }

private int addDepositEntry(final POTradeDepositEntry entry, final int index, int startHeight)
  {
  ItemSlot slot = new ItemSlot(8, startHeight, entry.getFilter(), this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      entry.setFilter(stack);
      setItem(stack);
      }
    };
  restockArea.addGuiElement(slot);
  
  Button typeButton = new Button(8+18+4, startHeight+3, 120, 12, entry.getType().toString())
    {
    @Override
    protected void onPressed()
      {
      entry.toggleType();
      setText(entry.getType().toString());
      }
    };
  restockArea.addGuiElement(typeButton);
  
  Button deleteButton = new Button(8+18+4+4+120, startHeight+3, 55, 12, "guistrings.delete")
    {
    @Override
    protected void onPressed()
      {
      container.orders.getRestockData().removeDepositEntry(index);
      refreshGui();
      }
    };
  restockArea.addGuiElement(deleteButton);
  
  startHeight+=18;
//  restockArea.addGuiElement(new Line(0, startHeight+2, xSize, startHeight+2, 1, 0x000000ff));
  startHeight+=5;
  return startHeight;
  }

private int addWithdrawEntry(final POTradeWithdrawEntry entry, final int index, int startHeight)
  {
  ItemSlot slot = new ItemSlot(8, startHeight, entry.getFilter(), this)
    {
    @Override
    public void onSlotClicked(ItemStack stack)
      {
      entry.setFilter(stack);
      setItem(stack);
      }
    };
  restockArea.addGuiElement(slot);
  
  Button typeButton = new Button(8+18+4, startHeight+3, 120, 12, entry.getType().toString())
    {
    @Override
    protected void onPressed()
      {
      entry.toggleType();
      setText(entry.getType().toString());
      }
    };
  restockArea.addGuiElement(typeButton);
  
  Button deleteButton = new Button(8+18+4+4+120, startHeight+3, 55, 12, "guistrings.delete")
    {
    @Override
    protected void onPressed()
      {
      container.orders.getRestockData().removeWithdrawEntry(index);
      refreshGui();
      }
    };
  restockArea.addGuiElement(deleteButton);
  
  startHeight+=18;
//  restockArea.addGuiElement(new Line(0, startHeight+2, xSize, startHeight+2, 1, 0x000000ff));
  startHeight+=5;
  return startHeight;
  }

}
