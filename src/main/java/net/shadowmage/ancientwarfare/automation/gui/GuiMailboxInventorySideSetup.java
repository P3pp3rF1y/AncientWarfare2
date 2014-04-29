package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.inventory.InventorySide;

import org.lwjgl.input.Mouse;

public class GuiMailboxInventorySideSetup extends GuiContainerBase
{

GuiMailboxInventory parent;
ContainerMailbox container;

public GuiMailboxInventorySideSetup(GuiMailboxInventory parent)
  {
  super((ContainerBase) parent.inventorySlots, 240, 100, defaultBackground);
  container = (ContainerMailbox)parent.inventorySlots;
  this.parent = parent;
  }

@Override
public void initElements()
  {

  }

@Override
public void setupElements()
  {
  this.clearElements();
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
protected boolean onGuiCloseRequested()
  {  
  container.addSlots();
  int x = Mouse.getX();
  int y = Mouse.getY();
  Minecraft.getMinecraft().displayGuiScreen(parent);
  Mouse.setCursorPosition(x, y);
  return false;
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
  container.sendSlotChange(side, selection);
  }

}

}
