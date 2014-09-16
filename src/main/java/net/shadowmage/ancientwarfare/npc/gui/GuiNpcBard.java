package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBard;

public class GuiNpcBard extends GuiContainerBase
{

Text tuneInput;
NumberInput lengthInput;
NumberInput chanceInput;
NumberInput delayInput;

int buttonX = 8;
int inputX = 80;

ContainerNpcBard container;

public GuiNpcBard(ContainerBase container)
  {
  super(container);
  this.container = (ContainerNpcBard)container;
  this.xSize = 128+80;
  this.ySize = 4*12 + 16;
  }

@Override
public void initElements()
  {      
  int totalHeight;
  Label label;
  
  int inputWidth=40;
  
  label = new Label(buttonX, (totalHeight=8), StatCollector.translateToLocal("guistrings.npc.bard_tune"));
  addGuiElement(label);
  tuneInput = new Text(inputX, totalHeight, 120, container.bardTune, this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("tune", newText);
      sendDataToContainer(tag);
      }
    };
  addGuiElement(tuneInput);
  
  label = new Label(buttonX, totalHeight+=12, StatCollector.translateToLocal("guistrings.npc.bard_play_time"));
  addGuiElement(label);
  lengthInput = new NumberInput(inputX, totalHeight, inputWidth, container.bardPlayLength, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("length", (int)value);
      sendDataToContainer(tag);
      }
    };
  lengthInput.setIntegerValue();
  addGuiElement(lengthInput);
  
  label = new Label(buttonX, totalHeight+=12, StatCollector.translateToLocal("guistrings.npc.bard_play_chance"));
  addGuiElement(label);
  chanceInput = new NumberInput(inputX, totalHeight, inputWidth, container.bardPlayChance, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("chance", (int)value);
      sendDataToContainer(tag);
      }
    };
  chanceInput.setIntegerValue();
  addGuiElement(chanceInput);
  
  label = new Label(buttonX, totalHeight+=12, StatCollector.translateToLocal("guistrings.npc.bard_play_delay"));
  addGuiElement(label);
  delayInput = new NumberInput(inputX, totalHeight, inputWidth, container.bardPlayRecheckDelay, this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setInteger("delay", (int)value);
      sendDataToContainer(tag);
      }
    };
  delayInput.setIntegerValue();
  addGuiElement(delayInput);
  }

@Override
public void setupElements()
  {  
  tuneInput.setText(container.bardTune);
  lengthInput.setValue(container.bardPlayLength);
  chanceInput.setValue(container.bardPlayChance);
  delayInput.setValue(container.bardPlayRecheckDelay);  
  }

@Override
protected boolean onGuiCloseRequested()
  {  
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.npc.getEntityId(), 0, 0);
  return false;
  }

}
