/**
   Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.client.gui.elements.IGuiElement;
import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.container.ContainerBase;
import shadowmage.ancient_structures.common.container.ContainerSpawnerPlacer;

public class GuiSpawnerPlacer extends GuiContainerAdvanced
{

ContainerSpawnerPlacer container;
GuiScrollableArea area;

HashMap<IGuiElement, String> buttonToName = new HashMap<IGuiElement, String>();
/**
 * @param container
 */
public GuiSpawnerPlacer(ContainerBase container)
  {
  super(container);
  this.container = (ContainerSpawnerPlacer) container;
  this.shouldCloseOnVanillaKeys = true;
  }

@Override
public void onElementActivated(IGuiElement element)
  {
  if(buttonToName.containsKey(element))
    {
    NBTTagCompound tag = new NBTTagCompound();
    String mobID = buttonToName.get(element);
    tag.setString("mobID", mobID);
    this.sendDataToServer(tag);
    this.container.mobID = mobID;
    }
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
  this.drawStringGui("Mob to spawn: "+this.container.mobID, 8, 8, 0xffffffff);
  }

@Override
public void updateScreenContents()
  {
  //area.updateGuiPos(guiLeft, guiTop);
  }

@Override
public void setupControls()
  {
  Collection<String> names = EntityList.stringToClassMapping.keySet();
  area = new GuiScrollableArea(1, this, 8, 8+18+4, getXSize()-16, getYSize()-16-18-8, names.size()*16);
  this.guiElements.clear();
  this.guiElements.put(1, area);
  
  Iterator<String> it= names.iterator();
  String name;
  int i = 0;
  GuiButtonSimple button;
  while(it.hasNext() && (name=it.next())!=null)
    {
    button = (GuiButtonSimple) new GuiButtonSimple(i+10, area, 256-16-20, 14, name).updateRenderPos(0, i*16);
    area.elements.add(button);
    buttonToName.put(button, name);
    i++;
    }  
  }

@Override
public void updateControls()
  {
  }

}
