package net.shadowmage.ancientwarfare.structure.gui;

import java.util.Collection;
import java.util.HashMap;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;

public class GuiStructureSelection extends GuiContainerBase
{

Text filterInput;
StructureTemplateClient currentSelection;
CompositeScrolled selectionArea;
Label selection;

HashMap<Label, StructureTemplateClient> templateMap = new HashMap<Label, StructureTemplateClient>();

public GuiStructureSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  addGuiElement(new Button(256-55-8, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")));
  
  selection = new Label(8,8, "");
  addGuiElement(selection);
  
  selectionArea = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(selectionArea);
  
  filterInput = new Text(8, 24, 240-16, "", this);
  addGuiElement(filterInput);  
  }

@Override
public void setupElements()
  {
  selectionArea.clearElements();
  templateMap.clear();
  setSelectionName((currentSelection==null? StatCollector.translateToLocal("guistrings.none") : currentSelection.name));  
  
  Collection<StructureTemplateClient> templates = StructureTemplateManager.instance().getClientStructures();
  
  /**
   * TODO sort templates by filterInput
   */
  
  /**
   * TODO add listener to labels for selection
   */
  
  Label label = null;
  int totalHeight = 3;
  for(StructureTemplateClient template : templates)
    {
    label = new Label(8, totalHeight, template.name);
    selectionArea.addGuiElement(label);
    templateMap.put(label, template);
    totalHeight+=12;
    }
  selectionArea.setAreaSize(totalHeight);
  }

public void setSelectionName(String name)
  {
  String text = StatCollector.translateToLocal("guistrings.current_selection")+": "+name;
  selection.setText(text);
  }

}
