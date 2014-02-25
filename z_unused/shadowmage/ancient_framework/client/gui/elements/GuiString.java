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


public class GuiString extends GuiElement
{

public String text = "";
public boolean shadow = true;
public boolean center = false;
public int color = 0xffffffff;
public int hoverColor = 0xffffffff;
public boolean clickable = false;

/**
 * @param elementNum
 * @param parent
 * @param w
 * @param h
 */
public GuiString(int elementNum, IGuiElementCallback parent, int w, int h, String text)
  {
  super(elementNum, parent, w, h);
  this.text = text;
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {  

  int x = this.guiLeft+renderPosX;
  int y = this.guiTop+renderPosY + this.height/2 - 4;// stringHeight/2
  if(this.center)
    {
    int wid = fr.getStringWidth(text);
    x -= wid/2;    
    }
  int color = this.isMouseOver ? hoverColor : this.color;
  this.fr.drawString(text, x, y, color, shadow);
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  if(this.clickable)
    {
    return true;
    }
  return false;
  }

@Override
public boolean handleMouseReleased(int x, int y, int num)
  {
  return false;
  }

@Override
public boolean handleMouseMoved(int x, int y, int num)
  {
  return false;
  }

@Override
public boolean handleMouseWheel(int x, int y, int wheel)
  {
  return false;
  }

@Override
public boolean handleKeyInput(char ch, int keyNum)
  {
  return false;
  }

public void setText(String text)
  {
  this.text = text;
  }

}
