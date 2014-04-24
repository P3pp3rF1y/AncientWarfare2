package net.shadowmage.ancientwarfare.structure.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangle;
import net.shadowmage.ancientwarfare.structure.container.ContainerStructureSelectionBase;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateClient;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class GuiStructureSelectionBase extends GuiContainerBase
{

Text filterInput;
StructureTemplateClient currentSelection;
CompositeScrolled selectionArea;
Label selection;

HashMap<Label, StructureTemplateClient> templateMap = new HashMap<Label, StructureTemplateClient>();

ComparatorStructureTemplateClient sorter;

TexturedRectangle rect;

ContainerStructureSelectionBase container;

CompositeScrolled resourceArea;

public GuiStructureSelectionBase(ContainerBase par1Container)
  {
  super(par1Container, 400, 240, defaultBackground);
  sorter = new ComparatorStructureTemplateClient();
  sorter.setFilterText("");
  container = (ContainerStructureSelectionBase)par1Container;
  }

@Override
public void initElements()
  {  
  addGuiElement(new Button(xSize-55-8, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      if(currentSelection!=null)
        {
        container.handleNameSelection(currentSelection.name);
        container.addSlots();
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
  
  resourceArea = new CompositeScrolled(256, 40, 144, 200);
  addGuiElement(resourceArea);
  
  StructureTemplateClient t = StructureTemplateManagerClient.instance().getClientTemplate(container.structureName);
  this.setSelection(t);    
  }

@Override
public void setupElements()
  {
  selectionArea.clearElements();
  templateMap.clear();
  setSelectionName((currentSelection==null? StatCollector.translateToLocal("guistrings.none") : currentSelection.name));  
  
  Collection<StructureTemplateClient> templatesC = getTemplatesForDisplay();
  List<StructureTemplateClient> templates = new ArrayList<StructureTemplateClient>();
  templates.addAll(templatesC);
  sorter.setFilterText(filterInput.getText());  
  Collections.sort(templates, sorter);
  
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

protected Collection<StructureTemplateClient> getTemplatesForDisplay()
  {
  return StructureTemplateManagerClient.instance().getClientStructures();
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
  resourceArea.clearElements();
  int totalHeight = 8;
  this.currentSelection = template;
  this.setSelectionName(template==null? StatCollector.translateToLocal("guistrings.none") : template.name);
  
  if(template!=null)
    {
    ResourceLocation l = StructureTemplateManagerClient.instance().getImageFor(template.name);
    rect.setTexture(l);
    List<ItemStack> resources = template.resourceList;
    ItemSlot item;
    for(ItemStack stack : resources)
      {
      item = new ItemSlot(8, totalHeight, stack, this);
      resourceArea.addGuiElement(item);
      totalHeight+=18;
      }    
    }
  else
    {
    rect.setTexture(null);
    }
  resourceArea.setAreaSize(totalHeight+8);
  }

public void setSelectionName(String name)
  {
  selection.setText(name);
  }
}
