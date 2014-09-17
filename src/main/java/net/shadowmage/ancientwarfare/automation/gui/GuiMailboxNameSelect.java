package net.shadowmage.ancientwarfare.automation.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.automation.container.ContainerMailbox;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

import org.lwjgl.input.Mouse;

public class GuiMailboxNameSelect extends GuiContainerBase
{

GuiMailboxInventory parent;
ContainerMailbox container;
boolean name;//true if setting mailbox name, false for setting target name
boolean privateBox;

Text nameInputArea;
CompositeScrolled nameSelectArea;
Button addNameButton;
Button deleteNameButton;
Button selectButton;

public GuiMailboxNameSelect(GuiMailboxInventory parent, boolean name)
  {
  super((ContainerBase) parent.inventorySlots, 256, 240, defaultBackground);
  this.parent = parent;
  this.container = parent.container;
  this.name = name;
  this.privateBox = parent.container.privateBox;  
  }

@Override
public void initElements()
  {
  nameInputArea = new Text(8, 8, 120, name? container.mailboxName : container.targetName, this);
  addGuiElement(nameInputArea);
  
  addNameButton = new Button(8, 22, 55, 12, "guistrings.automation.add_mailbox")
    {
    @Override
    protected void onPressed()
      {
      String name = nameInputArea.getText();
      if(!name.isEmpty())
        {
        container.handleNameAdd(name);
        }
      }
    };
  addGuiElement(addNameButton);
  
  selectButton = new Button(256-8-55, 8, 55, 12, "guistrings.automation.select_mailbox")
    {
    @Override
    protected void onPressed()
      {
      if(name)
        {
        container.handleNameSelection(nameInputArea.getText());
        }
      else
        {
        container.handleTargetSelection(nameInputArea.getText());
        }
      closeGui();
      }
    };
  addGuiElement(selectButton);
    
  deleteNameButton = new Button(8+55, 22, 55, 12, "guistrings.automation.delete_mailbox")
    {
    @Override
    protected void onPressed()
      {
      String name = nameInputArea.getText();
      if(!name.isEmpty())
        {
        container.handleNameDelete(name);
        }
      }
    };
  addGuiElement(deleteNameButton);
  
  nameSelectArea = new CompositeScrolled(0, 20+4+12, 256, 240-8-12-4-12);
  addGuiElement(nameSelectArea);
  }

@Override
public void setupElements()
  {
  nameSelectArea.clearElements();
  
  List<String> names = privateBox ? container.privateBoxNames : container.publicBoxNames;
  Button button;
  int totalHeight = 8;
  for(String name : names)
    {
    if(name.equals(container.mailboxName) || name.equals(container.targetName)){continue;}
    button = new Button(8, totalHeight, 240-24-14, 12, name)
      {
      @Override
      protected void onPressed()
        {
        nameInputArea.setText(text);
        }
      };
    nameSelectArea.addGuiElement(button);
    totalHeight+=12;
    }
  nameSelectArea.setAreaSize(totalHeight);
  }

@Override
protected boolean onGuiCloseRequested()
  {  
  container.addSlots();
  container.setGui(parent);
  int x = Mouse.getX();
  int y = Mouse.getY();
  Minecraft.getMinecraft().displayGuiScreen(parent);
  Mouse.setCursorPosition(x, y);
  return false;
  }

}
