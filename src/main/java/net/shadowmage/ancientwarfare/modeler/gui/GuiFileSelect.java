package net.shadowmage.ancientwarfare.modeler.gui;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

public class GuiFileSelect extends GuiContainerBase
{

GuiContainerBase parent;

/**
 * the display name of the current parent directory (used to click to go back)
 */
String parentPathName = "";

/**
 * current path, used to update all others relative to this
 */
String currentPath = "";

boolean allowNewFiles = false;

CompositeScrolled area;
Text input;

public GuiFileSelect(GuiContainerBase parent, String basePath, boolean allowNewFiles)
  {
  super(((ContainerBase)parent.inventorySlots), 256, 240, defaultBackground);
  this.parent = parent;
  this.currentPath = basePath;
  this.parentPathName = new File(basePath).getParent();
  this.allowNewFiles = allowNewFiles;  
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(area);
  
  Button button = new Button(256-8-55-55, 8+14, 55, 12, "Accept");
  button.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        onAcceptClicked();
        }
      return true;
      }
    });
  addGuiElement(button);
  
  button = new Button(256-8-55, 8+14, 55, 12, "Cancel");
  button.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        onCancelClicked();
        }
      return true;
      }
    });
  addGuiElement(button);
  
  button = new Button(8, 8+14, 55, 12, "Back");
  button.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(GuiElement widget, ActivationEvent evt)
      {
      if(widget.isMouseOverElement(evt.mx, evt.my))
        {
        onBackClicked();
        }
      return true;
      }
    });
  addGuiElement(button);
  
  input = new Text(8, 8, 160, "", this);
  addGuiElement(input);
  }

private void handleFileClicked(String fileName)
  {  
  File file = new File(currentPath, fileName);
  if(file.isDirectory())
    {
    handleDirectorySelection(file.getAbsolutePath());
    input.setText("");
    }
  else
    {
    input.setText(fileName);
    }
  refreshGui();
  }

private void handleDirectorySelection(String newPath)
  {
  if(newPath==null)
    {
    AWLog.logDebug("passed null path..." );
    new Exception().printStackTrace();
    return;
    }  
  File file = new File(newPath);  
  this.parentPathName = file.getParent();
  this.currentPath = newPath;
  this.refreshGui();
  }

private void onAcceptClicked()
  {
  String name = input.getText();
  File selectedFile = new File(currentPath, name);
  if(!allowNewFiles && !selectedFile.exists())
    {
    //TODO throw error, pop up error gui, etc
    }
  else
    {
    onFileSelected(selectedFile);
    }
  closeWindow();  
  }

private void closeWindow()
  {
  ((ContainerBase)parent.inventorySlots).setGui(parent);
  Minecraft.getMinecraft().displayGuiScreen(parent);
  }

private void onCancelClicked()
  {
  closeWindow();  
  }

private void onBackClicked()
  {  
  handleDirectorySelection(parentPathName);  
  }

@Override
public void setupElements()
  {
  area.clearElements();
  int totalHeight = 3;
  File file = new File(currentPath);
  File[] dirFiles = file.listFiles();
  Label label;
  if(dirFiles!=null)
    {
    for(File f : dirFiles)
      {
      if(f.isDirectory() || f.isFile())
        {
        label = new Label(8, totalHeight, f.getName());
        label.addNewListener(new Listener(Listener.MOUSE_UP)
          {
          @Override
          public boolean onEvent(GuiElement widget, ActivationEvent evt)
            {
            if(widget.isMouseOverElement(evt.mx, evt.my))
              {
              handleFileClicked(((Label)widget).getText());
              }
            return true;
            }
          });
        area.addGuiElement(label);
        totalHeight +=12;
        }    
      }
    }  
  area.setAreaSize(totalHeight); 
  }

/**
 * should be overriden by implementing class to add callback mechanism
 * @param file
 */
public void onFileSelected(File file)
  {
  
  }

}
