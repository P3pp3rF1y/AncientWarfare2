package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.npc.container.ContainerTradeOrder;

public class GuiTradeOrder extends GuiContainerBase
{

ContainerTradeOrder container;
CompositeScrolled tradesArea, routeArea, restockArea;
Button tradeButton, routeButton, restockButton;

private Button currentMode;

public GuiTradeOrder(ContainerBase container)
  {
  super(container, 320, 240, defaultBackground);
  this.container = (ContainerTradeOrder)container;
  }

@Override
public void initElements()
  {
  tradesArea = new CompositeScrolled(0, 16, xSize, ySize-16);
  routeArea = new CompositeScrolled(0, 16, xSize, ySize-16);
  restockArea = new CompositeScrolled(0, 16, xSize, ySize-16);
  tradeButton = new Button(8, 8, 40, 12, "guistrings.npc.trades");
  routeButton = new Button(48, 8, 40, 12, "guistrings.npc.route");
  restockButton = new Button(88, 8, 40, 12, "guistrings.npc.restock");

  tradeButton.setEnabled(false);
  routeButton.setEnabled(true);
  restockButton.setEnabled(true);
  
  setupTradeMode();
  setupRouteMode();
  setupRestockMode();
  
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
  }

private void setRouteMode()
  {
  addGuiElement(routeArea);
  tradeButton.setEnabled(true);
  routeButton.setEnabled(false);
  restockButton.setEnabled(true);  
  }

private void setRestockMode()
  {
  addGuiElement(restockArea);
  tradeButton.setEnabled(true);
  routeButton.setEnabled(true);
  restockButton.setEnabled(false);  
  }

private void setupTradeMode()
  {
  
  }

private void setupRouteMode()
  {
  
  }

private void setupRestockMode()
  {
  
  }

}
