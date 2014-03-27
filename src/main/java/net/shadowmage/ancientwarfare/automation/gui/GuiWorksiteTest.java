package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.inventory.Slot;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteTest;
import net.shadowmage.ancientwarfare.automation.tile.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.block.RelativeSide;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.SideSlotMap;
import net.shadowmage.ancientwarfare.core.inventory.InventorySided.ViewableSlot;

public class GuiWorksiteTest extends GuiContainerBase
{

public GuiWorksiteTest(ContainerBase par1Container)
  {
  super(par1Container, 178, ((ContainerWorksiteTest)par1Container).guiHeight, defaultBackground);
  }

@Override
public void initElements()
  {
  TileWorksiteBase worksite = ((ContainerWorksiteTest)inventorySlots).worksite;
  int lowestY = 8;
  Label label;
  for(RelativeSide side : RelativeSide.values())
    {
    SideSlotMap slotMap = worksite.inventory.getSlotMapForSide(side);
    if(slotMap==null){continue;}
    label = new Label(slotMap.guiX, slotMap.guiY, StatCollector.translateToLocal(slotMap.label));
    addGuiElement(label);
    }
  label = new Label(8, ((ContainerWorksiteTest)inventorySlots).playerSlotsLabelHeight, StatCollector.translateToLocal("guistrings.inventory.player"));
  addGuiElement(label);
  }

@Override
public void setupElements()
  {
  
  
  }

}
