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

import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;

public class GuiScrollableArea extends GuiScrollableAreaSimple
{

public GuiScrollableArea(int elementNum, GuiContainerAdvanced parent, int x, int y, int w,  int h, int totalHeight)
  {
  super(elementNum, parent, x, y, w, h, w, totalHeight); 
  this.scrollBar = new GuiScrollBarSimple(elementNum, this, 16, h, totalHeight, h);
  this.scrollBar.updateRenderPos(w-16, 0);
  }

@Override
public void setHeight(int height)
  {
  super.setHeight(height);
  this.scrollBar = new GuiScrollBarSimple(elementNum, this, 16, height, totalHeight, height);
  this.scrollBar.updateRenderPos(width-16, 0);
  }

@Override
public void updateTotalHeight(int height)
  {
  this.totalHeight = height;
  this.scrollBar.updateHandleHeight(totalHeight, this.height);  
  }
}
