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
package shadowmage.ancient_framework.client.gui.elements;

import shadowmage.ancient_framework.common.config.Statics;



public class GuiCheckBoxSimple extends GuiButtonSimple
{

public boolean checked = false;

/**
 * @param elementNum
 * @param parent
 * @param x
 * @param y
 * @param w
 * @param h
 */
public GuiCheckBoxSimple(int elementNum, IGuiElementCallback parent, int w, int h)
  {
  super(elementNum, parent, w, h, "");
  if(width>40)
    {
    this.width = 40;
    }
  if(height>40)
    {
    this.height = 40;
    }
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {
  if(!this.hidden)
    {
    int texOffset = this.getHoverState();
    int vOffset = texOffset * 40;//will return 0, 40, or 80..for inactive, active, hover, apply to Y offset in UV rendering
    int hOffset = checked ? 40 : 0;
    String tex = Statics.TEXTURE_PATH+"gui/guiButtons.png";
    this.drawQuadedTexture(guiLeft+renderPosX, guiTop+renderPosY, width, height, 40, 40, tex, 0+hOffset, 120+vOffset);
    if(!this.displayString.equals(""))
      {
      this.drawString(fr, displayString, guiLeft+renderPosX+width+2, guiTop+renderPosY+height/2-4, 0xffffffff);
      }
    }  
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  this.checked = !this.checked;
  return true;
  }

public GuiCheckBoxSimple setChecked(boolean checked)
  {
  this.checked = checked;
  return this;
  }

public boolean checked()
  {
  return this.checked;
  }

}
