package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
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
  
  label = new Label(8, totalHeight+1 ,"foo.ownerName");
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
  
  label = new Label(8, totalHeight+1 ,"foo.customTex");
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
  
  label = new Label(8, totalHeight+1 ,"foo.healthOverride");
  addGuiElement(label);
  
  maxHealthOverrideInput = new NumberInput(100, totalHeight, 256-16-100, 0, this)
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
  
  label = new Label(8, totalHeight+1 ,"foo.dmgOverride");
  addGuiElement(label);
  
  attackDamageOverrideInput = new NumberInput(100, totalHeight, 256-16-100, 0, this)
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
  
  label = new Label(8, totalHeight+1 ,"foo.armOverride");
  addGuiElement(label);
  
  armorValueOverrideInput = new NumberInput(100, totalHeight, 256-16-100, 0, this)
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
  
  wanderCheckbox = new Checkbox(8, totalHeight, 16, 16, "foo.wander")
    {
    @Override
    public void onToggled()
      {
      container.wander = checked();
      }
    };
  addGuiElement(wanderCheckbox);
  totalHeight+=12;
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
   * force opening of normal gui (whatever that may be for the npc) when advanced controls is closed
   */
  if(hasChanged)
    {
    container.sendChangesToServer();
    }
  container.npc.openGUI(player);
  return false;
  }

}
