package net.shadowmage.ancientwarfare.structure.gui;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EntityList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;

public class GuiSpawnerPlacer extends GuiContainerBase
{

CompositeScrolled typeSelectionArea;
CompositeScrolled attributesArea;

public GuiSpawnerPlacer(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"));
  /**
   * TODO add listener to close GUI
   */
  addGuiElement(button);
  
  typeSelectionArea = new CompositeScrolled(0, 30, 256, 105);
  addGuiElement(typeSelectionArea);
  
  attributesArea = new CompositeScrolled(0, 30+105, 256, 105);
  addGuiElement(attributesArea);  
  }

@Override
public void setupElements()
  {
  int totalHeight = 3;
  Map<String, Class> mp = EntityList.stringToClassMapping;
  
  Label label;
  for(String name : mp.keySet())
    {
    label = new Label(8, totalHeight, name);
    typeSelectionArea.addGuiElement(label);
    labelToClass.put(label, mp.get(name));
    totalHeight+=12;
    }
  typeSelectionArea.setAreaSize(totalHeight);
  }

private HashMap<Label, Class> labelToClass = new HashMap<Label, Class>();

}
