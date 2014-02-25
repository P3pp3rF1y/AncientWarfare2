package shadowmage.meim.client.gui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiElement;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollBarSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiTextInputLine;
import shadowmage.ancient_framework.client.gui.elements.IFileSelectCallback;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.meim.common.config.MEIMConfig;

public class GuiFileSelect extends GuiContainerAdvanced
{

GuiContainerAdvanced parent;
IFileSelectCallback fileCall;

/**
 * cached displayable list of directory names in the current directory (displayed before files)
 */
List<String> currentFiles = new ArrayList<String>();

/**
 * the display name of the current parent directory (used to click to go back)
 */
String parentPathName = "";

/**
 * the origin pathname when the gui was opened
 */
String basePath = "";

/**
 * current path, used to update all others relative to this
 */
String currentPath = "";

/**
 * entry string, used for manaul selection of a file (saving a file, etc)
 */
String manalSelection = "";

String currentSelection = "";

boolean updateContents = true;
boolean allowNewFiles = false;

GuiScrollBarSimple bar;// = new GuiScrollBar();

public int displaySize = 16;

public GuiFileSelect(GuiContainerAdvanced parent, IFileSelectCallback fileCall, String basePath, boolean allowNewFiles)
  {
  super(((ContainerBase)parent.inventorySlots));
  this.parent = parent;
  this.basePath = basePath;
  this.currentPath = this.basePath;
  this.parentPathName = new File(basePath).getParent();
  this.allowNewFiles = allowNewFiles;  
  this.fileCall = fileCall;
  }

@Override
public int getXSize()
  {
  return 256;
  }

@Override
public int getYSize()
  {
  return 240;
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  this.drawPath();
  this.drawCurrentSelection();
  this.drawFileNames(); 
  }

public void drawCurrentSelection()
  {
  this.drawString(fontRenderer, currentSelection, guiLeft+10, guiTop+8, 0xffffffff);
  }

public void drawPath()
  {
  String path = this.currentPath;
  if(path.length()>20)
    {
    path = "..."+ path.substring(path.length()-20);
    }
  this.drawString(fontRenderer, path, guiLeft+10, guiTop+18, 0xffffffff);
  }

public void drawFileNames()
  {
  int topIndex = bar.getTopIndexForSet(this.currentFiles.size(), displaySize);
  for(int i = 0; i < this.displaySize; i++)
    {
    int index = i + topIndex;
    if(index<this.currentFiles.size())
      {
      this.drawString(fontRenderer, this.currentFiles.get(index), guiLeft+10, guiTop+74 + i*10, 0xffffffff);
      }
    }
  }

@Override
public void updateScreenContents()
  {
  if(this.updateContents)
    {
    MEIMConfig.logDebug("updating directory");
    this.updateDirectoryContents();
    this.bar.updateHandleHeight(this.currentFiles.size(), displaySize);
    this.bar.handleTop = 0;
    this.updateContents = false;
    }
  
  }

public void updateDirectoryContents()
  {
  this.currentFiles.clear();
  File baseFile = new File(this.currentPath);
  if(!baseFile.exists())
    {
    MEIMConfig.logDebug("non-existant path: "+this.currentPath);
    return;
    }  
  if(!baseFile.isDirectory())
    {
    MEIMConfig.logDebug("path is not a directory: "+this.currentPath);
    return;
    }  
  this.parentPathName = baseFile.getParent();
  File[] dirFiles = baseFile.listFiles();
  for(File f : dirFiles)
    {
    if(f.isDirectory() || f.isFile())
      {
      this.currentFiles.add(f.getName());
      }    
    }
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  switch(element.getElementNumber())
  {
  case 0://cancel
  this.mc.displayGuiScreen(parent);
  break;
  case 1://accept
  this.acceptSelection();
  break;
  case 2://back a level
  this.moveBack();
  this.updateContents = true;
  break;
  case 3:
  break;
  case 4://manual input box enter key pressed
  this.handleManualNameInput(this.input.getText());
  break;
  default:
  break;
  }
  }

public void moveBack()
  {
  this.currentPath = this.parentPathName;
  this.parentPathName = new File(this.currentPath).getParent();
  this.getElementByNumber(1).enabled = false;
  this.currentSelection = "";
  }

public void acceptSelection()
  {
  File f = new File(this.currentPath, this.currentSelection);
  if(f.isFile() || (!f.exists() && this.allowNewFiles))
    {  
    this.fileCall.handleFileSelection(f);
    }
  this.mc.displayGuiScreen(parent);
  }

@Override
public void setupControls()
  {
  int vSize = 12;
  
  this.addGuiButton(0, 45, vSize, "Cancel");
  this.addGuiButton(1, 45, vSize, "Accept");
  this.addGuiButton(2, 45, vSize, "Back");//up one level
  this.bar = this.addScrollBarSimple(3, 20, 168, 100, 20);
  this.input = this.addTextField(4, 180, 12, 30, "");
  
  this.bar.handleTop = 0;  
  this.getElementByNumber(1).enabled = false;
  }

GuiTextInputLine input;

@Override
public void updateControls()
  {
  int border = 10;
  int vSize = 12;
  int buffer =2;  
  
  int row1 = border;
  int row2 = row1+buffer+vSize;
  int row3 = row2+buffer+vSize;
  int row4 = row3+buffer+vSize;  
  int row5 = row4+buffer+vSize;
  this.getElementByNumber(0).updateRenderPos(256-45-border, row1);
  this.getElementByNumber(1).updateRenderPos(256-45-border, row2);
  this.getElementByNumber(2).updateRenderPos(border, row3);
  this.getElementByNumber(3).updateRenderPos(256-30, row5);
  this.getElementByNumber(4).updateRenderPos(border, row4);
  }

@Override
public void handleMouseInput()
  {
  super.handleMouseInput();
  if(mouseX>=guiLeft+10 && mouseX <guiLeft+200 && mouseY>= guiTop+74 && mouseY< guiTop+74+(18*10))
    {
    if(Mouse.getEventButtonState() && Mouse.getEventButton()==0)//left clicked
      {
      MEIMConfig.logDebug("mouse clicked");
      int y = mouseY - guiTop-74;
      int index = y/10;
      index += bar.getTopIndexForSet(currentFiles.size(), displaySize);
      this.handleNameClick(index);
      }
    }
  }

public void handleNameClick(int index)
  {
  if(index<this.currentFiles.size())
    {
    String name = this.currentFiles.get(index);
    File f = new File(currentPath,name);
    MEIMConfig.logDebug("handle selection: "+this.currentPath+" :: "+name);
    MEIMConfig.logDebug("f path: "+f.getAbsolutePath());
    GuiElement el = this.getElementByNumber(1);//accept button
    el.enabled = false;
    if(f.isDirectory())
      {
      MEIMConfig.logDebug("setting dir to: "+f.getAbsolutePath());
      this.parentPathName = this.currentPath;
      this.currentPath = f.getPath();
      this.currentSelection = "";
      this.updateContents = true;
      this.currentSelection = "";
      }    
    else if(f.isFile())
      {
      this.currentSelection = f.getName();
      el.enabled = true;
      this.input.setText(this.currentSelection);
      }
    }
  }

public void handleManualNameInput(String name)
  {
  File f = new File(currentPath, name);
  
  GuiElement el = this.getElementByNumber(1);//accept button
  el.enabled = false;
  if(f.isDirectory())
    {
    this.currentPath = f.getAbsolutePath();
    this.currentSelection = "";    
    }  
  else if(f.isFile())
    {
    el.enabled = true;
    this.currentSelection = name;
    }
  else if(!f.exists() && this.allowNewFiles)
    {
    el.enabled = true;
    this.currentSelection = name;
    return;
    }
  }

}
