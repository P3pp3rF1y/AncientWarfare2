package net.shadowmage.ancientwarfare.structure.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangle;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelection;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class GuiStructureSelection extends GuiContainerBase
{

Text filterInput;
StructureTemplateClient currentSelection;
CompositeScrolled selectionArea;
Label selection;

HashMap<Label, StructureTemplateClient> templateMap = new HashMap<Label, StructureTemplateClient>();

ComparatorStructureTemplateClient sorter;

TexturedRectangle rect;

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
      if(currentSelection!=null)
        {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("structName", currentSelection.name);
        sendDataToContainer(tag);
        closeGui();
        }
      }
    });  
  
  selection = new Label(8,8, "");
  addGuiElement(selection);
  
  selectionArea = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(selectionArea);
  
  filterInput = new Text(8, 24, 240-16, "", this)
    {
    //kind of dirty...should possibly implement a real onCharEntered callback for when input actually changes
    @Override
    protected void handleKeyInput(int keyCode, char ch)
      {      
      super.handleKeyInput(keyCode, ch);
      refreshGui();
      }
    };
  addGuiElement(filterInput);
  

  rect = new TexturedRectangle(256, 0, 320, 240, (ResourceLocation)null, 320, 240, 0, 0, 320, 240);
  addGuiElement(rect);
  
  ContainerStructureSelection cont = (ContainerStructureSelection)inventorySlots;
  StructureTemplateClient t = StructureTemplateManagerClient.instance().getClientTemplate(cont.structureName);
  this.setSelection(t);  

  }

@Override
public void setupElements()
  {
  selectionArea.clearElements();
  templateMap.clear();
  setSelectionName((currentSelection==null? StatCollector.translateToLocal("guistrings.none") : currentSelection.name));  
  
  Collection<StructureTemplateClient> templatesC = StructureTemplateManagerClient.instance().getClientStructures();
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
  
  if(template!=null)
    {
    ResourceLocation l = StructureTemplateManagerClient.instance().getImageFor(template.name);
    rect.setTexture(l);
    }
  else
    {
    rect.setTexture(null);
    }
  }

public void setSelectionName(String name)
  {
  String text = StatCollector.translateToLocal("guistrings.current_selection")+": "+name;
  selection.setText(text);
  }

}
