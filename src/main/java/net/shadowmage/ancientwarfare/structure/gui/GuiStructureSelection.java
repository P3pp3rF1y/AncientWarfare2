package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;

public class GuiStructureSelection extends GuiContainerBase
{

StructureTemplateClient currentSelection;
CompositeScrolled selectionArea;
Label selection;

public GuiStructureSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  Button done = new Button(256-55-8, 8, 55, 12, "Done");
  addGuiElement(selectionArea);
  
  selection = new Label(8,8, "");
  addGuiElement(selection);
  
  selectionArea = new CompositeScrolled(0, 30, 256, 210);
  addGuiElement(selectionArea);
  
  }

@Override
public void setupElements()
  {
  String text = StatCollector.translateToLocal("guistrings.current_selection")+": "+ (currentSelection==null? StatCollector.translateToLocal("guistrings.none"):currentSelection.name);
  selection.setText(text);
  }

}
