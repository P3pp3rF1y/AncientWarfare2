package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;

public class GuiNpcInventory extends GuiNpcBase
{

Text nameInput;
ContainerNpcInventory container;
String name;
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
  nameInput = new Text(60, 20, 100, container.npc.getCustomNameTag(), this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      container.handleNpcNameUpdate(newText);
      }
    };
  addGuiElement(nameInput);
  }

@Override
public void setupElements()
  {
  nameInput.setText(container.npc.getCustomNameTag());
  }

}
