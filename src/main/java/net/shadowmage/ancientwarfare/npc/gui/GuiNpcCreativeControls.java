package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcCreativeControls;

public class GuiNpcCreativeControls extends GuiContainerBase
{

Text ownerNameInput;
Text customTexInput;
NumberInput attackDamageOverrideInput;
NumberInput armorValueOverrideInput;
NumberInput maxHealthOverrideInput;
Checkbox wanderCheckbox;

boolean hasChanged = false;

ContainerNpcCreativeControls container;
public GuiNpcCreativeControls(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcCreativeControls)container;
  }

@Override
public void initElements()
  {
  int totalHeight = 8;
  Label label;
  
  label = new Label(8, totalHeight+1 , StatCollector.translateToLocal("guistrings.npc.owner_name"));
  addGuiElement(label);
  
  ownerNameInput = new Text(100, totalHeight, 256-16-100, "", this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      container.ownerName = newText;
      hasChanged=true;
      }
    };
  addGuiElement(ownerNameInput);
  totalHeight+=12;
  
  label = new Label(8, totalHeight+1 , StatCollector.translateToLocal("guistrings.npc.custom_texture"));
  addGuiElement(label);
  
  customTexInput = new Text(100, totalHeight, 256-16-100, "", this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      container.customTexRef = newText;
      hasChanged=true;
      }
    };
  addGuiElement(customTexInput);
  totalHeight+=12;  
  
  label = new Label(8, totalHeight+1 , StatCollector.translateToLocal("guistrings.npc.health_override"));
  addGuiElement(label);
  
  maxHealthOverrideInput = new NumberInput(120, totalHeight, 60, 0, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.maxHealth = (int)value;
      hasChanged=true;
      }
    };
  addGuiElement(maxHealthOverrideInput);
  maxHealthOverrideInput.setIntegerValue().setAllowNegative();
  totalHeight+=12;  
  
  label = new Label(8, totalHeight+1 , StatCollector.translateToLocal("guistrings.npc.damage_override"));
  addGuiElement(label);
  
  attackDamageOverrideInput = new NumberInput(120, totalHeight, 60, 0, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.attackDamage = (int)value;
      hasChanged=true;
      }
    };
  addGuiElement(attackDamageOverrideInput);
  attackDamageOverrideInput.setIntegerValue().setAllowNegative();
  totalHeight+=12;  
  
  label = new Label(8, totalHeight+1 , StatCollector.translateToLocal("guistrings.npc.armor_override"));
  addGuiElement(label);
  
  armorValueOverrideInput = new NumberInput(120, totalHeight, 60, 0, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.armorValue = (int)value;
      hasChanged=true;
      }
    };
  addGuiElement(armorValueOverrideInput);
  armorValueOverrideInput.setIntegerValue().setAllowNegative();
  totalHeight+=12;    
  
  wanderCheckbox = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.npc.allow_wander"))
    {
    @Override
    public void onToggled()
      {
      container.wander = checked();
      hasChanged=true;
      }
    };
  addGuiElement(wanderCheckbox);
  totalHeight+=16;
  
  this.ySize=totalHeight+8;  
  }

@Override
public void setupElements()
  {
  ownerNameInput.setText(container.ownerName);
  customTexInput.setText(container.customTexRef);
  attackDamageOverrideInput.setValue(container.attackDamage);
  armorValueOverrideInput.setValue(container.armorValue);
  maxHealthOverrideInput.setValue(container.maxHealth);
  wanderCheckbox.setChecked(container.wander);
  }

@Override
protected boolean onGuiCloseRequested()
  {
  /**
   * if changes were made while gui was open, send these to server
   */
  if(hasChanged)
    {
    container.sendChangesToServer();
    }

  /**
   * force opening of normal gui (whatever that may be for the npc) when advanced controls is closed
   */
  container.npc.openGUI(player);
  return false;
  }

}
