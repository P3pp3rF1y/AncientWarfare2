package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedRectangle;
import net.shadowmage.ancientwarfare.structure.container.ContainerDraftingStation;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManagerClient;

public class GuiDraftingStation extends GuiContainerBase
{

//right-side column @ X=176
//max X size == 352?
ContainerDraftingStation container;
CompositeScrolled resourceListArea;
TexturedRectangle rect;
Button selectButton;
Button stopButton;
Button startButton;
Label selectionLabel;

public GuiDraftingStation(ContainerBase par1Container)
  {
  super(par1Container, 400, 240, defaultBackground);
  container = (ContainerDraftingStation)par1Container;
  }

@Override
public void initElements()  
  {  
  rect = new TexturedRectangle(227-8, 8, 170, 96, (ResourceLocation)null, 512, 288, 0, 0, 512, 288);
  addGuiElement(rect);
  
  resourceListArea = new CompositeScrolled(this, 176, 96+8, 400-176, 240-96-8);
  addGuiElement(resourceListArea);
  
  selectButton = new Button(8, 8, 95, 12, StatCollector.translateToLocal("guistrings.structure.select_structure"))
    {
    @Override
    protected void onPressed()
      {
      container.removeSlots();
      Minecraft.getMinecraft().displayGuiScreen(new GuiStructureSelectionDraftingStation(GuiDraftingStation.this));
      }
    };
  addGuiElement(selectButton);
  
  selectionLabel = new Label(8, 20, container.structureName==null ? StatCollector.translateToLocal("guistrings.structure.no_selection") : container.structureName);
  addGuiElement(selectionLabel);
  
  stopButton = new Button(8, 32, 55, 12, StatCollector.translateToLocal("guistrings.stop"))
    {
    @Override
    protected void onPressed()
      {
      container.handleStopInput();
      }
    };  
  
  startButton = new Button(8, 32, 55, 12, StatCollector.translateToLocal("guistrings.start"))
    {
    @Override
    protected void onPressed()
      {
      container.handleStartInput();
      }
    };  
  
  Label label = new Label(8, 94-16-18-12, StatCollector.translateToLocal("guistrings.output"));
  addGuiElement(label);
  
  label = new Label(8, 94-16, StatCollector.translateToLocal("guistrings.input"));
  addGuiElement(label);
  }

@Override
public void setupElements()
  {
  removeGuiElement(startButton);
  removeGuiElement(stopButton);
  container.setGui(this);
  resourceListArea.clearElements();
  ItemSlot slot;
  int totalHeight = 8;
  for(ItemStack stack : container.neededResources)
    {
    slot = new ItemSlot(8, totalHeight, stack, this);    
    slot.setRenderLabel(true);
    resourceListArea.addGuiElement(slot);    
    totalHeight+=18;
    }  
  resourceListArea.setAreaSize(totalHeight+8);
  
  String name = container.structureName;
  if(name==null)
    {
    rect.setTexture(null);
    selectionLabel.setText(StatCollector.translateToLocal("guistrings.structure.no_selection"));
    }
  else
    {
    rect.setTexture(StructureTemplateManagerClient.instance().getImageFor(name));
    selectionLabel.setText(name);
    }
  if(container.isStarted)
    {
    addGuiElement(stopButton);
    }
  else if(container.structureName!=null)
    {
    addGuiElement(startButton);
    }
  }


}
