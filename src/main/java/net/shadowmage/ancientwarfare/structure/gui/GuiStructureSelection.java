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
  
  Label label = new Label(8,8, StatCollector.translateToLocal("guistrings.current_selection"));
  addGuiElement(label);
  
  selection = new Label(8, 20, "");
  addGuiElement(selection);
  
  filterInput = new Text(8, 18+12, 240-16, "", this)
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
  
  selectionArea = new CompositeScrolled(0, 138, 256, 240-138);
  addGuiElement(selectionArea);
  
  rect = new TexturedRectangle(43, 42, 170, 96, (ResourceLocation)null, 512, 288, 0, 0, 512, 288);
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
  
  TemplateButton button;    
  int totalHeight = 8;
  
  for(StructureTemplateClient template : templates)
    {
    button = new TemplateButton(8, totalHeight, template);
    selectionArea.addGuiElement(button);
    totalHeight+=12;
    }
  
  selectionArea.setAreaSize(totalHeight+8);
  }


private class TemplateButton extends Button
{
StructureTemplateClient template;
public TemplateButton(int topLeftX, int topLeftY, StructureTemplateClient template)
  {
  super(topLeftX, topLeftY, 232, 12, template.name);
  this.template = template;
  }

@Override
protected void onPressed()
  {
  setSelection(template);
  }
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
  selection.setText(name);
  }

}
