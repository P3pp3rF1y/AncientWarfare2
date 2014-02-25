/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_structures.client.gui.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiCheckBoxSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiElement;
import shadowmage.ancient_framework.client.gui.elements.GuiNumberInputLine;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiString;
import shadowmage.ancient_framework.client.gui.elements.GuiTextInputLine;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_structures.common.container.ContainerStructureScanner;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidationType;
import shadowmage.ancient_structures.common.template.build.validation.StructureValidationType.ValidationProperty;

public class GuiStructureScanner extends GuiContainerAdvanced
{

String name = "";
String dimensionsString = "";
GuiTextInputLine nameBox;
GuiTextInputLine dimensionLine;
GuiCheckBoxSimple includeBox;
GuiButtonSimple biomeSelectButton;
GuiButtonSimple blockSelectButton;

GuiScrollableArea area;

HashMap<GuiElement, String> elementNameMap = new HashMap<GuiElement, String>();
HashMap<GuiButtonSimple, StructureValidationType> typeButtonMap = new HashMap<GuiButtonSimple, StructureValidationType>();

HashMap<GuiCheckBoxSimple, String> checkBoxNameMap = new HashMap<GuiCheckBoxSimple, String>();
HashMap<GuiNumberInputLine, String> numberInputNameMap = new HashMap<GuiNumberInputLine, String>();

HashMap<String, Boolean> lastKnownBooleanValues = new HashMap<String, Boolean>();
HashMap<String, Integer> lastKnownIntegerValues = new HashMap<String, Integer>();

List<String> biomeSelections = new ArrayList<String>();
List<String> blockSelections = new ArrayList<String>();

StructureValidationType currentValidationType = StructureValidationType.GROUND;

protected ContainerStructureScanner container;
/**
 * @param container
 */
public GuiStructureScanner(ContainerBase container)
  {
  super(container);
  this.container = (ContainerStructureScanner)container;
  this.shouldCloseOnVanillaKeys = false;
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
public String getGuiBackGroundTexture()
  {
  return Statics.TEXTURE_PATH+"gui/guiBackgroundLarge.png";
  }

@Override
public void renderExtraBackGround(int mouseX, int mouseY, float partialTime)
  {
  this.drawStringGui("Structure Name: ", 8, 8, 0xffffffff);  
  this.drawStringGui("Add to game immediately: ", 8, 38, 0xffffffff);  
  this.drawStringGui("Validation Settings: ", 8, 55, 0xffffffff);
  }

@Override
public void updateScreenContents()
  {
  this.name = nameBox.getText();
  this.dimensionsString = dimensionLine.getText();
  }

@Override
public void setupControls()
  {
  this.guiElements.clear();
  this.addGuiButton(0, 35, 18, "Done").updateRenderPos(256-35-10, 10); 
  this.addGuiButton(1, 45, 18, "Export").updateRenderPos(256-45-10, 30);
  this.area = new GuiScrollableArea(2, this, 8, 70, getXSize()-16, getYSize() - 78, 8);  
  this.guiElements.put(2, area);
  this.addGuiButton(3, 45, 18, "Reset").updateRenderPos(256-45-10, 50);    
  nameBox = this.addTextField(4, 8, 20, 120, 10, 30, name);  
  includeBox = (GuiCheckBoxSimple) this.addCheckBox(5, 16, 16).setChecked(true).updateRenderPos(145, 35);  
  }

private int addBooleanProp(int elementNum, String regName, String displayName, boolean defaultVal, int startHeight)
  {
  boolean val = this.lastKnownBooleanValues.containsKey(regName) ? this.lastKnownBooleanValues.get(regName) : defaultVal;
  area.addGuiElement(new GuiString(elementNum, area, 180, 10, displayName).updateRenderPos(0, startHeight));  
  GuiCheckBoxSimple checkBox = new GuiCheckBoxSimple(elementNum, area, 16, 16);
  checkBox.checked = val;
  area.elements.add(checkBox);
  checkBox.updateRenderPos(160, startHeight);
  checkBoxNameMap.put(checkBox, regName);  
  return startHeight + 18;
  }

private int addIntegerProp(int elementNum, String regName, String displayName, int defaultVal, int startHeight)
  {
  int val = this.lastKnownIntegerValues.containsKey(regName) ? this.lastKnownIntegerValues.get(regName) : defaultVal;
  area.addGuiElement(new GuiString(elementNum, area, 180, 10, displayName).updateRenderPos(0, startHeight));
  GuiNumberInputLine input = new GuiNumberInputLine(elementNum, area, 40, 12, 10, "0");
  input.setAsIntegerValue();
  input.setIntegerValue(val);
  input.updateRenderPos(160, startHeight);
  area.addGuiElement(input);
  numberInputNameMap.put(input, regName);    
  return startHeight + 18;
  }

private void stashValues()
  {
  for(GuiCheckBoxSimple box : this.checkBoxNameMap.keySet())
    {
    this.lastKnownBooleanValues.put(checkBoxNameMap.get(box), box.checked);
    }
  for(GuiNumberInputLine line : this.numberInputNameMap.keySet())
    {
    this.lastKnownIntegerValues.put(numberInputNameMap.get(line), line.getIntVal());
    }
  }

public void onBiomeSelectionCallback(List<String> biomes)
  {
  this.biomeSelections.clear();
  this.biomeSelections.addAll(biomes);
  }

public void onBlockSelectionCallback(List<String> blocks)
  {
  this.blockSelections.clear();
  this.blockSelections.addAll(blocks);
  }

@Override
public void updateControls()
  {
  this.stashValues();  
  area.elements.clear();
  typeButtonMap.clear();
  checkBoxNameMap.clear();
  numberInputNameMap.clear();
  int totalHeight = 0;
  
  area.elements.add(new GuiString(-1, area, 100, 10, "Validation Type: " + currentValidationType.getName()));
  totalHeight += 12;
  
  GuiButtonSimple typeButton = new GuiButtonSimple(6, area, 48, 16, StructureValidationType.GROUND.getName());
  typeButton.updateRenderPos(0, 0+12);
  if(currentValidationType==StructureValidationType.GROUND){typeButton.enabled = false;}
  area.elements.add(typeButton);
  typeButtonMap.put(typeButton, StructureValidationType.GROUND);
  
  typeButton = new GuiButtonSimple(7, area, 73, 16, StructureValidationType.UNDERGROUND.getName());
  typeButton.updateRenderPos(50, 0+12);
  if(currentValidationType==StructureValidationType.UNDERGROUND){typeButton.enabled = false;}
  area.elements.add(typeButton);
  typeButtonMap.put(typeButton, StructureValidationType.UNDERGROUND);
  
  typeButton = new GuiButtonSimple(8, area, 43, 16, StructureValidationType.SKY.getName());
  typeButton.updateRenderPos(125, 0+12);
  if(currentValidationType==StructureValidationType.SKY){typeButton.enabled = false;}
  area.elements.add(typeButton);
  typeButtonMap.put(typeButton, StructureValidationType.SKY);
  
  typeButton = new GuiButtonSimple(9, area, 48, 16, StructureValidationType.HARBOR.getName());
  typeButton.updateRenderPos(170, 0+12);
  if(currentValidationType==StructureValidationType.HARBOR){typeButton.enabled = false;}
  area.elements.add(typeButton); 
  typeButtonMap.put(typeButton, StructureValidationType.HARBOR);
  totalHeight += 18; 
  
  typeButton = new GuiButtonSimple(10, area, 48, 16, StructureValidationType.WATER.getName());
  typeButton.updateRenderPos(0, 18+12);
  if(currentValidationType==StructureValidationType.WATER){typeButton.enabled = false;}
  area.elements.add(typeButton);
  typeButtonMap.put(typeButton, StructureValidationType.WATER);
  
  typeButton = new GuiButtonSimple(11, area, 73, 16, StructureValidationType.UNDERWATER.getName());
  typeButton.updateRenderPos(50, 18+12);
  if(currentValidationType==StructureValidationType.UNDERWATER){typeButton.enabled = false;}
  area.elements.add(typeButton); 
  typeButtonMap.put(typeButton, StructureValidationType.UNDERWATER);
  
  typeButton = new GuiButtonSimple(12, area, 48, 16, StructureValidationType.ISLAND.getName());
  typeButton.updateRenderPos(125, 18+12);
  if(currentValidationType==StructureValidationType.ISLAND){typeButton.enabled = false;}
  area.elements.add(typeButton); 
  typeButtonMap.put(typeButton, StructureValidationType.ISLAND);
  totalHeight += 18;
  
  totalHeight += 8;
  
  totalHeight = addBooleanProp(13, "enableWorldGen", "Enable World Gen: ", false, totalHeight);
  totalHeight = addBooleanProp(14, "unique", "Is Unique: ", false, totalHeight);
  totalHeight = addBooleanProp(15, "preserveBlocks", "Preserve Blocks: ", false, totalHeight);
  
  totalHeight = addIntegerProp(16, "selectionWeight", "Selection Weight: ", 1, totalHeight);
  totalHeight = addIntegerProp(17, "clusterValue", "Cluster Value: ", 1, totalHeight);
  totalHeight = addIntegerProp(18, "minDuplicateDistance", "Min Duplicate Distance: ", 1, totalHeight);
  
  totalHeight = addIntegerProp(19, "borderSize", "Border Size: ", 0, totalHeight);
  totalHeight = addIntegerProp(20, "maxLeveling", "Max Leveling: ", 0, totalHeight);
  totalHeight = addIntegerProp(21, "maxFill", "Max Underfill: ", 0, totalHeight);
  
  int elementNum = 22;
  for(ValidationProperty prop : currentValidationType.getValidationProperties())
    {
    if(prop.clz == int.class)
      {
      totalHeight = addIntegerProp(elementNum, prop.propertyName, prop.displayName, 0, totalHeight);
      }
    else if(prop.clz == boolean.class)
      {
      totalHeight = addBooleanProp(elementNum, prop.propertyName, prop.displayName, false, totalHeight);
      }
    elementNum++;
    }
  
  totalHeight = addBooleanProp(elementNum, "dimensionWhiteList", "Dimension White List: ", false, totalHeight);
  elementNum++;
    
  area.elements.add(new GuiString(elementNum, area, 120, 12, "Dimension List: ").updateRenderPos(0, totalHeight));  
  area.elements.add( dimensionLine = new GuiTextInputLine(elementNum, area, 120, 12, 30, dimensionsString));
  dimensionLine.updateRenderPos(80, totalHeight);
  totalHeight+=18;
  elementNum++;  
  
  totalHeight = addBooleanProp(elementNum, "biomeWhiteList", "Biome White List: ", false, totalHeight);
  elementNum++;
  
  area.elements.add( (biomeSelectButton = new GuiButtonSimple(elementNum, area, 90, 16, "Select Biomes")).updateRenderPos(0, totalHeight));
  elementNum++;
  totalHeight+=18;
  
  area.elements.add( new GuiString(elementNum, area, 120, 12, "Selected Biomes: ").updateRenderPos(0, totalHeight));
  totalHeight+=12;
  
  for(String biome : this.biomeSelections)
    {
    area.elements.add(new GuiString(elementNum, area, 120, 12, biome).updateRenderPos(0, totalHeight));
    totalHeight+=12;
    elementNum++;
    }  
  totalHeight+=12;
    
  area.elements.add( (blockSelectButton = new GuiButtonSimple(elementNum, area, 90, 16, "Select Blocks")).updateRenderPos(0, totalHeight));
  elementNum++;
  totalHeight+=18;
  
  area.elements.add( new GuiString(elementNum, area, 120, 12, "Valid Target Blocks: ").updateRenderPos(0, totalHeight));
  totalHeight+=12;
  
  for(String block : this.blockSelections)
    {
    area.elements.add(new GuiString(elementNum, area, 120, 12, block).updateRenderPos(0, totalHeight));
    totalHeight+=12;
    elementNum++;
    }  
  totalHeight+=12;
        
  area.updateTotalHeight(totalHeight); 
  }

private void sendExportDataToServer()
  {
  NBTTagCompound tag = new NBTTagCompound();
  tag.setBoolean("export", includeBox.checked);
  tag.setString("name", name);
  tag.setString("validationType", currentValidationType.getName());
  
  String label;
  for(GuiCheckBoxSimple box : this.checkBoxNameMap.keySet())
    {
    label = this.checkBoxNameMap.get(box);
    tag.setBoolean(label, box.checked);
    }
  
  for(GuiNumberInputLine line : this.numberInputNameMap.keySet())
    {
    label = this.numberInputNameMap.get(line);
    tag.setInteger(label, line.getIntVal());
    }
  
  NBTTagList biomeList = new NBTTagList();
  for(String biome : this.biomeSelections)
    {
    biomeList.appendTag(new NBTTagString("name", biome));
    }
  tag.setTag("biomeList", biomeList);
  
  
  NBTTagList blockList = new NBTTagList();
  for(String block : this.blockSelections)
    {
    blockList.appendTag(new NBTTagString("name", block));
    }
  tag.setTag("blockList", blockList);
  
  int[] dimensionArray = StringTools.parseIntArray(dimensionsString);
  
  tag.setIntArray("dimensions", dimensionArray);
  this.sendDataToServer(tag);
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  switch(element.getElementNumber())
  {
  case 0://done
  closeGUI();
  break;
  
  case 1://export
    {
    if(!name.equals(""))
      {
      sendExportDataToServer();
      closeGUI();      
      }    
    }
  break;
    
  case 3://clearData
    {
    NBTTagCompound tag = new NBTTagCompound();
    tag.setBoolean("reset", true);
    this.sendDataToServer(tag);
    closeGUI();  
    }
  break;
    
  case 4://text field, validate text
    {
    this.nameBox.setText(this.validateString(this.nameBox.getText()));
    }  
  break;    
  }  
  
  if(this.typeButtonMap.containsKey(element))
    {    
    this.currentValidationType = this.typeButtonMap.get(element);
    AWLog.logDebug("updating current validation type to: "+this.currentValidationType);
    this.refreshGui();
    }
  else if(element==this.biomeSelectButton)
    {
    Minecraft.getMinecraft().displayGuiScreen(new GuiBiomeSelection(this));    
    }  
  else if(element==this.blockSelectButton)
    {
    Minecraft.getMinecraft().displayGuiScreen(new GuiBlockSelection(this));
    }
  this.name = nameBox.getText(); 
  this.dimensionsString = dimensionLine.getText();
  }

protected String validateString(String input)
  {
  String scrubbed = "";
  for(int i = 0; i < input.length(); i++)
    {
    char ch = input.charAt(i);
    if(isValidChar(ch))
      {
      scrubbed = scrubbed + ch;
      }
    }  
  return scrubbed;
  }

protected boolean isValidChar(char ch)
  {
  /**
   *  public static final char[] allowedCharactersArray = new char[] {'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
   */
  switch(ch)
  {
  case '/':
  case '\\':
  case '\n':
  case '\r':
  case '\"':
  case '\'':
  case '`':
  case '\t':
  case '\f':
  case '?':
  case '*':
  case '<':
  case '>':
  case '(':
  case ')':
  case '|':
  case ':':
  case '{':
  case '}':
  case '[':
  case ']':
  return false;
  default:
  return true;
  }
  }

}
