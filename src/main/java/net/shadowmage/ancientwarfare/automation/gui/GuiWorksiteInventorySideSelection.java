package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteInventorySideSelection extends GuiContainerBase
{

ContainerWorksiteInventorySideSelection container;

public GuiWorksiteInventorySideSelection(ContainerBase par1Container)
  {
  super(par1Container, 240, 114, defaultBackground);
  container = (ContainerWorksiteInventorySideSelection)par1Container;
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {
  this.clearElements();
  ContainerWorksiteInventorySideSelection container = (ContainerWorksiteInventorySideSelection)inventorySlots;
  int height = 8;
  Label label;
  SideButton sideButton;
  InventorySide accessed;
  for(RelativeSide side : RelativeSide.values())
    {
    label = new Label(8, height, StatCollector.translateToLocal(side.getTranslationKey()));
    addGuiElement(label);

    accessed = container.sideMap.get(side);
    
    sideButton = new SideButton(128, height, side, accessed==null? InventorySide.NONE: accessed);
    addGuiElement(sideButton);
    
    label = new Label(128+55+8, height, StatCollector.translateToLocal(Direction.getDirectionFor(RelativeSide.getAccessDirection(side, container.worksite.getBlockMetadata())).getTranslationKey()));
    addGuiElement(label);
    
    height+=14;
    }  
  }

@Override
public void onGuiClosed()
  {
  super.onGuiClosed();
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_INVENTORY, container.worksite.xCoord, container.worksite.yCoord, container.worksite.zCoord);
  }

private class SideButton extends Button
{
RelativeSide side;//base side
InventorySide selection;//accessed side

public SideButton(int topLeftX, int topLeftY, RelativeSide side, InventorySide selection)
  {
  super(topLeftX, topLeftY, 55, 12, StatCollector.translateToLocal(selection.getTranslationKey()));  
  this.side = side;
  this.selection = selection;
  }

@Override
protected void onPressed()
  {
  int ordinal = selection.ordinal();
  ordinal++;
  if(ordinal>=InventorySide.values().length)
    {
    ordinal = 0;
    }
  selection = InventorySide.values()[ordinal];
  container.sideMap.put(side, selection);
  setText(StatCollector.translateToLocal(selection.getTranslationKey()));
  container.sendSettingsToServer();
  }

}

}
