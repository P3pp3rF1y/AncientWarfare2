package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcFactionTradeView;

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
  area = new CompositeScrolled(0, 24, xSize, ySize-24);
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
  addGuiElement(area);
  if(player.capabilities.isCreativeMode)
    {
    addGuiElement(inventoryButton);
    addGuiElement(setupButton);
    }
  // TODO Auto-generated method stub
  }

}
