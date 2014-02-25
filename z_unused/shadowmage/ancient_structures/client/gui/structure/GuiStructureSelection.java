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
import java.util.Collections;

import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.GuiTextInputLine;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_structures.common.container.ContainerCSB;
import shadowmage.ancient_structures.common.manager.StructureTemplateManager;
import shadowmage.ancient_structures.common.template.StructureTemplateClient;
import shadowmage.ancient_structures.common.utils.ComparatorStructureTemplateClient;

/**
 * creative structure builder
 * @author Shadowmage
 *
 */
public class GuiStructureSelection extends GuiContainerAdvanced
{

private ContainerCSB container;
String currentStructure = "";

ArrayList<StructureTemplateClient> clientStructures = new ArrayList<StructureTemplateClient>();
ComparatorStructureTemplateClient sorter = new ComparatorStructureTemplateClient();

/**
 * @param par1Container
 */
public GuiStructureSelection(ContainerBase container)
  {
  super(container);
  this.container = (ContainerCSB) container;
  clientStructures.addAll(StructureTemplateManager.instance().getClientStructures());
  Collections.sort(clientStructures, sorter);
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
  this.drawString(fontRenderer, "Structure: "+currentStructure, guiLeft + 10, guiTop + 14, 0xffffffff); 
  }

@Override
public void updateScreenContents()
  {
  //area.updateGuiPos(guiLeft, guiTop);
  }

@Override
public void refreshGui()
  {
  super.refreshGui();
  this.currentStructure = this.container.structureName;
  }

GuiScrollableArea area;
GuiTextInputLine searchBox;

@Override
public void setupControls()
  {
  this.addGuiButton(0, 35, 18, "Done").updateRenderPos(256-35-10, 10); 

  searchBox = (GuiTextInputLine) this.addTextField(1, getXSize()-16, 12, 30, "").updateRenderPos(8, 10+18+4);

  int totalHeight = clientStructures.size()*14;
  area = new GuiScrollableArea(4, this, 10, 50, this.getXSize()-20, this.getYSize()-60, totalHeight);
  this.guiElements.put(4, area);

  int kX = 5;
  int kY = 0;
  int buttonHeight = 12;
  int buffer = 2;

  for(int i = 0; i < clientStructures.size(); i++)
    {
    String name = this.clientStructures.get(i).name;
    kY = i * (buffer+buttonHeight);
    area.addGuiElement(new GuiButtonSimple(i+20, area, this.getXSize()-20-16-10, buttonHeight, name).updateRenderPos(kX, kY));
    } 
  }

@Override
public void updateControls()
  {

  }

@Override
public void onElementKeyTyped(char ch, int keyNum)
  {
  super.onElementKeyTyped(ch, keyNum);
  sorter.setFilterText(searchBox.getText());
  Collections.sort(clientStructures, sorter);  
  area.elements.clear();
  int kX = 5;
  int kY = 0;
  int buttonHeight = 12;
  int buffer = 2;
  for(int i = 0; i < clientStructures.size(); i++)
    {
    String name = this.clientStructures.get(i).name;
    kY = i * (buffer+buttonHeight);
    area.addGuiElement(new GuiButtonSimple(i+20, area, this.getXSize()-20-16-10, buttonHeight, name).updateRenderPos(kX, kY));
    } 
  }

@Override
public void onElementActivated(IGuiElement button)
  {  
  switch(button.getElementNumber())
    {
    case 0:
    closeGUI();
    return; 
    }  

  if(button.getElementNumber()>=20)
    {
    int index = button.getElementNumber()-20;
    if(index>=this.clientStructures.size() || index <0)
      { 
      return;
      }
    AWLog.logDebug("sending datas...");
    this.forceUpdate = true;
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("name", this.clientStructures.get(index).name);
    this.currentStructure = this.clientStructures.get(index).name;
    this.sendDataToServer(tag);
    }  
  }


}
