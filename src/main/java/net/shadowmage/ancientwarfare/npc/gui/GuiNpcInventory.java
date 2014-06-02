package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcInventory;

public class GuiNpcInventory extends GuiNpcBase
{

Button repackButton;
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
  
  repackButton = new Button(110, 60, 55, 12, "foo.repack")
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
  
  Button button = new Button(110, 72, 55, 12, "foo.sethome")
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
  
  button = new Button(110, 84, 55, 12, "foo.clearhome")
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
  }

@Override
public void setupElements()
  {
  nameInput.setText(container.npc.getCustomNameTag());
  }

}
