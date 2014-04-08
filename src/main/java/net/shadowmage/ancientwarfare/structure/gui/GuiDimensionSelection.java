package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiDimensionSelection extends GuiContainerBase
{

GuiStructureScanner parent;

CompositeScrolled area;
Checkbox whiteList;

public GuiDimensionSelection(GuiStructureScanner parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 240, defaultBackground);
  this.parent = parent;  
  this.shouldCloseOnVanillaKeys = false;
  }

@Override
public void initElements()
  {
  Label label = new Label(8,8, StatCollector.translateToLocal("guistrings.select_biomes")+":");
  addGuiElement(label);
  
  area = new CompositeScrolled(0, 40, 256, 200);
  this.addGuiElement(area);
  
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      setToValidator();
      Minecraft.getMinecraft().displayGuiScreen(parent);
      }
    };
  addGuiElement(button);
  
  whiteList = new Checkbox(8, 20, 16, 16, StatCollector.translateToLocal("guistrings.dimension_whitelist")+"?");
  addGuiElement(whiteList);
  
  
  }

@Override
public void setupElements()
  {

  }

private void setToValidator()
  {
  
  }

}
