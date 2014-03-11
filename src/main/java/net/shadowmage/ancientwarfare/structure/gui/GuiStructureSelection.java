package net.shadowmage.ancientwarfare.structure.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
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

ComparatorStructureTemplateClient sorter;

public GuiStructureSelection(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  sorter = new ComparatorStructureTemplateClient();
  sorter.setFilterText("");
  /**
   * TODO set initial selection name from player.currentItem
   */
  }

@Override
public void initElements()
  {
  
  addGuiElement(new Button(256-55-8, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      
      /**
       * TODO close GUI
       * send packet to server-side container to set selected name onto item
       */
      }
    });  
  
  selection = new Label(8,8, "");
  addGuiElement(selection);
  
  selectionArea = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(selectionArea);
  
  filterInput = new Text(8, 24, 240-16, "", this)
    {
    @Override
    protected void handleKeyInput(int keyCode, char ch)
      {      
      super.handleKeyInput(keyCode, ch);
      refreshGui();
      }
    };
  addGuiElement(filterInput);  
  }

@Override
public void setupElements()
  {
  selectionArea.clearElements();
  templateMap.clear();
  setSelectionName((currentSelection==null? StatCollector.translateToLocal("guistrings.none") : currentSelection.name));  
  
  Collection<StructureTemplateClient> templatesC = StructureTemplateManager.instance().getClientStructures();
  List<StructureTemplateClient> templates = new ArrayList<StructureTemplateClient>();
  templates.addAll(templatesC);
  sorter.setFilterText(filterInput.getText());  
  Collections.sort(templates, sorter);
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {   
        setSelection(templateMap.get((Label)widget));
        }
      return true;
      }
    };
  
  Label label = null;
  int totalHeight = 3;
  for(StructureTemplateClient template : templates)
    {
    label = new Label(8, totalHeight, template.name);
    label.addNewListener(listener);
    selectionArea.addGuiElement(label);
    templateMap.put(label, template);
    totalHeight+=12;
    }
  selectionArea.setAreaSize(totalHeight);
  }

private void setSelection(StructureTemplateClient template)
  {
  this.currentSelection = template;
  this.setSelectionName(template==null? StatCollector.translateToLocal("guistrings.none") : template.name);
  }

public void setSelectionName(String name)
  {
  String text = StatCollector.translateToLocal("guistrings.current_selection")+": "+name;
  selection.setText(text);
  }

}
