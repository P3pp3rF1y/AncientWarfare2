package net.shadowmage.ancientwarfare.structure.gui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationProperty;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

public class GuiStructureValidationSettings extends GuiContainerBase
{

GuiStructureScanner parent;

CompositeScrolled area;
Label typeLabel;

Set<Button> typeButtons = new HashSet<Button>();
HashMap<Button, StructureValidationType> buttonToValidationType = new HashMap<Button, StructureValidationType>();


public GuiStructureValidationSettings(GuiStructureScanner parent)
  {
  super((ContainerBase) parent.inventorySlots, 256, 240, defaultBackground);
  this.parent = parent;  
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 30, 256, 210);
  this.addGuiElement(area);
  
  Listener listener = new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        onTypeButtonPressed((Button)widget);
        }
      return true;
      }
    };
  
  Button button = new Button(8, 8, 78, 16, StructureValidationType.GROUND.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.GROUND);
  typeButtons.add(button);
  
  button = new Button(86, 8, 78, 16, StructureValidationType.UNDERGROUND.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.UNDERGROUND);
  typeButtons.add(button);
  
  button = new Button(164, 8, 78, 16, StructureValidationType.SKY.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.SKY);
  typeButtons.add(button);
  
  button = new Button(8, 24, 78, 16, StructureValidationType.WATER.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.WATER);
  typeButtons.add(button);
  
  button = new Button(86, 24, 78, 16, StructureValidationType.UNDERWATER.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.UNDERWATER);
  typeButtons.add(button);
  
  button = new Button(164, 24, 78, 16, StructureValidationType.ISLAND.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.ISLAND);
  typeButtons.add(button);
  
  button = new Button(8, 40, 78, 16, StructureValidationType.HARBOR.getName());
  button.addNewListener(listener);
  buttonToValidationType.put(button, StructureValidationType.HARBOR);
  typeButtons.add(button);
  
  typeLabel = new Label(8, 8, "");
  addGuiElement(typeLabel);
  
  button = new Button(256-8-55, 8, 55, 12, "Done");
  button.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        }
      return true;
      }    
    });
  addGuiElement(button);
  }

private void onTypeButtonPressed(Button button)
  {
  StructureValidationType type = buttonToValidationType.get(button);
  if(type==null){return;}//should never happen

  this.updateValidationSettings();
  StructureValidator newValidator = type.getValidator();
  newValidator.inheritPropertiesFrom(parent.validator);
  parent.validationType = type;
  parent.validator = newValidator;
  this.refreshGui();
  }

private HashMap<GuiElement, String> elementToPropertyName = new HashMap<GuiElement, String>();

@Override
public void setupElements()
  {
  typeLabel.setText("Current Type: "+parent.validationType.getName());
 
  int totalHeight = 0;
  area.clearElements();
  elementToPropertyName.clear();
  for(Button b : typeButtons)
    {
    area.addGuiElement(b);
    }
  totalHeight += 16*3 + 4 + 8;//type buttons height+buffer

  Label label = null;
  String propName;
  Checkbox box;
  NumberInput input;
  for(StructureValidationProperty property : parent.validator.getProperties())
    { 
    propName = property.getRegName();        
    if(    propName.equals(StructureValidator.PROP_BIOME_LIST)
        || propName.equals(StructureValidator.PROP_BIOME_WHITE_LIST)
        || propName.equals(StructureValidator.PROP_DIMENSION_LIST)
        || propName.equals(StructureValidator.PROP_DIMENSION_WHITE_LIST)
        || propName.equals(StructureValidator.PROP_BLOCK_LIST))
      {
      continue;//skip the properties handled by blocks, biome, or dimensions setup guis
      }
    label = new Label(8, totalHeight, StatCollector.translateToLocal("structure.validation."+property.getRegName()));
    area.addGuiElement(label);
    
    switch(property.getDataType())
    {
    case StructureValidationProperty.DATA_TYPE_INT:
      {
      input = new NumberInput(200, totalHeight-1, 32, property.getDataInt(), this);
      elementToPropertyName.put(input, propName); 
      input.setIntegerValue();
      area.addGuiElement(input);
      }
    break;
    case StructureValidationProperty.DATA_TYPE_BOOLEAN:
      {
      box = new Checkbox(200, totalHeight-3, 16, 16, "");
      box.setChecked(property.getDataBoolean());
      elementToPropertyName.put(box, propName);
      area.addGuiElement(box);
      }
    break;
    }
        
    totalHeight+=16;
    }
  area.setAreaSize(totalHeight);
  }

/**
 * finalizes current properties from elements
 * by setting those properties
 */
protected void updateValidationSettings()
  {
  StructureValidationProperty property;
  String propName;
  for(GuiElement element : elementToPropertyName.keySet())
    {
    propName = elementToPropertyName.get(element);
    property = parent.validator.getProperty(propName);
    if(property==null){continue;}//should never happen..but meh
    switch(property.getDataType())
    {
    case StructureValidationProperty.DATA_TYPE_INT:
      {
      NumberInput input = (NumberInput)element;
      property.setValue(input.getIntegerValue());
      }
    break;
    case StructureValidationProperty.DATA_TYPE_BOOLEAN:
      {
      Checkbox box = (Checkbox)element;
      property.setValue(box.checked());
      }
    break;
    }
    }
  }

/**
 * should be called from 'done' button to flush current settings to parent.validator
 */
protected void closeGui()
  {
  updateValidationSettings();
  }

@Override
public void onGuiClosed()
  {
  super.onGuiClosed();
//  Minecraft.getMinecraft().displayGuiScreen(parent);
  }

}
