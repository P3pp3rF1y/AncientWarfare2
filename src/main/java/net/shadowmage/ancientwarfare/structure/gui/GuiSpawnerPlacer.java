package net.shadowmage.ancientwarfare.structure.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerPlacer;

public class GuiSpawnerPlacer extends GuiContainerBase
{

Label currentSelectionName;
CompositeScrolled typeSelectionArea;
CompositeScrolled attributesArea;

private HashMap<Label, Class> labelToClass = new HashMap<Label, Class>();

public GuiSpawnerPlacer(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  }

@Override
public void initElements()
  {
  Button button = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      ((ContainerSpawnerPlacer)inventorySlots).sendDataToServer();
      Minecraft.getMinecraft().thePlayer.closeScreen();
      Minecraft.getMinecraft().displayGuiScreen(null);
      }
    };  
  addGuiElement(button);
  
  ContainerSpawnerPlacer cnt = (ContainerSpawnerPlacer)inventorySlots;
  
  currentSelectionName = new Label(8, 8, "");
  updateSelectionName(cnt.entityId);
  addGuiElement(currentSelectionName);
  
  typeSelectionArea = new CompositeScrolled(0, 30, 256, 105);
  addGuiElement(typeSelectionArea);
  
  attributesArea = new CompositeScrolled(0, 30+105, 256, 105);
  addGuiElement(attributesArea);  
  }

@Override
public void setupElements()
  {
  typeSelectionArea.clearElements();
  attributesArea.clearElements();
  labelToClass.clear();
  
  int totalHeight = 3;
  Map<String, Class> mp = EntityList.stringToClassMapping;
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Class clz = labelToClass.get((Label)widget);
        String name = (String) EntityList.classToStringMapping.get(clz);
        updateSelectionName(name);
        ((ContainerSpawnerPlacer)inventorySlots).entityId = name;        
        }      
      return true;
      }
    };
  
  Label label;
  for(String name : mp.keySet())
    {
    if(AWStructureStatics.excludedSpawnerEntities.contains(name))
      {
      continue;//skip excluded entities
      }
    label = new Label(8, totalHeight, name);
    label.addNewListener(listener);
    typeSelectionArea.addGuiElement(label);
    labelToClass.put(label, mp.get(name));
    totalHeight+=12;
    }
  typeSelectionArea.setAreaSize(totalHeight);
  
  ContainerSpawnerPlacer cnt = (ContainerSpawnerPlacer)inventorySlots;
  updateSelectionName(cnt.entityId);
  
  totalHeight = 3;
  
  NumberInput input; 
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.delay"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.delay, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).delay = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;  
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.min_spawn_delay"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.minSpawnDelay, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).minSpawnDelay = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_spawn_delay"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.maxSpawnDelay, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).maxSpawnDelay = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.spawn_count"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.spawnCount, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).spawnCount = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_nearby_entities"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.maxNearbyEntities, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).maxNearbyEntities = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.required_player_range"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.requiredPlayerRange, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).requiredPlayerRange = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.spawn_range"));
  attributesArea.addGuiElement(label);
  input = new NumberInput(120, totalHeight, 112, cnt.spawnRange, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      ((ContainerSpawnerPlacer)inventorySlots).spawnRange = (short)value;
      }
    };
  input.setIntegerValue();
  attributesArea.addGuiElement(input);
  totalHeight +=12;
  
  attributesArea.setAreaSize(totalHeight);
  }

private void updateSelectionName(String name)
  {
  currentSelectionName.setText(StatCollector.translateToLocal("guistrings.current_selection") + ": "+name);
  }


}
