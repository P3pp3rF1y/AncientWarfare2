package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;

public class GuiNpcInventory extends GuiNpcBase
{

Button repackButton;
Text nameInput;
ContainerNpcInventory container;
String name;

int buttonX = 8+18+18+18+18+4;
public GuiNpcInventory(ContainerBase container)
  {
  super(container);
  this.xSize = 178;
  this.ySize = ((ContainerNpcInventory)container).guiHeight;
  this.container = (ContainerNpcInventory) container;
  name = this.container.npc.getCustomNameTag();
  }

@Override
public void updateScreen()
  {
  if(!name.equals(container.npc.getCustomNameTag()))
    {
    refreshGui();
    }
  super.updateScreen();
  }

@Override
public void initElements()
  {
  Label label = new Label(70, 9, StatCollector.translateToLocal("guistrings.npc.npc_name"));
  addGuiElement(label);
  
  nameInput = new Text(70, 20, 100, container.npc.getCustomNameTag(), this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      container.handleNpcNameUpdate(newText);
      }
    };
  addGuiElement(nameInput);
  
  repackButton = new Button(buttonX, 36, 75, 12, StatCollector.translateToLocal("guistrings.npc.repack"))
    {
    @Override
    protected void onPressed()
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setBoolean("repack", true);
      sendDataToContainer(tag);
      closeGui();
      }
    };
  addGuiElement(repackButton);
  
  Button button = new Button(buttonX, 48, 75, 12, StatCollector.translateToLocal("guistrings.npc.set_home"))
    {
    @Override
    protected void onPressed()
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setBoolean("setHome", true);
      sendDataToContainer(tag);
      }
    };
  addGuiElement(button);
  
  button = new Button(buttonX, 60, 75, 12, StatCollector.translateToLocal("guistrings.npc.clear_home"))
    {
    @Override
    protected void onPressed()
      {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setBoolean("clearHome", true);
      sendDataToContainer(tag);
      }
    };
  addGuiElement(button);
  
  if(container.npc.hasAltGui())
    {
    button = new Button(buttonX, 72, 75, 12, StatCollector.translateToLocal("guistrings.npc.alt_gui"))
      {
      @Override
      protected void onPressed()
        {
        container.npc.openAltGui(player);
        }
      };
    addGuiElement(button);  
    }
  }

@Override
public void setupElements()
  {
  nameInput.setText(container.npc.getCustomNameTag());
  }

}
