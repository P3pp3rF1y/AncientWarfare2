package net.shadowmage.ancientwarfare.structure.gui;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.minecraft.world.biome.BiomeGenBase;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;

public class GuiStructureBiomeSelection extends GuiContainerBase
{

GuiStructureScanner parent;

CompositeScrolled area;
Checkbox whiteList;

private HashMap<GuiElement, String> elementToBiomeName = new HashMap<GuiElement, String>();

public GuiStructureBiomeSelection(GuiStructureScanner parent)
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
  
  whiteList = new Checkbox(8, 20, 16, 16, StatCollector.translateToLocal("guistrings.biome_whitelist")+"?")
    {
    @Override
    public void onToggled()
      {
      parent.validator.setBiomeWhiteList(checked());
      }
    };
  addGuiElement(whiteList);
  whiteList.setChecked(parent.validator.isBiomeWhiteList());
  
  area = new CompositeScrolled(this, 0, 40, 256, 200);
  this.addGuiElement(area);
  
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      Minecraft.getMinecraft().displayGuiScreen(parent);
      }
    };
  addGuiElement(button);
  
  int totalHeight = 3;
  
  Checkbox box;
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {    
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Checkbox box1 = (Checkbox)widget;
        String name = elementToBiomeName.get(box1);
        Set<String> biomeNames = parent.validator.getBiomeList();
        if(box1.checked())
          {
          biomeNames.add(name);
          }
        else
          {
          biomeNames.remove(name);
          }
        parent.validator.setBiomeList(biomeNames);
        }      
      return true;
      }
    };
  
  Set<String> biomeNames = parent.validator.getBiomeList();
    
  for(BiomeGenBase biome: BiomeGenBase.getBiomeGenArray())
    {
    if(biome==null){continue;}
    box = new Checkbox(8, totalHeight, 16, 16, AWStructureStatics.getBiomeName(biome));
    area.addGuiElement(box);
    elementToBiomeName.put(box, AWStructureStatics.getBiomeName(biome));
    totalHeight += 16;
    if(biomeNames.contains(AWStructureStatics.getBiomeName(biome)))
      {
      box.setChecked(true);
      }
    box.addNewListener(listener);
    }  
  area.setAreaSize(totalHeight);
  }


@Override
public void setupElements()
  {
  whiteList.setChecked(parent.validator.isBiomeWhiteList());
  }

}
