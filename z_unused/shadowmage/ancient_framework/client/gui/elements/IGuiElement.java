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

import java.util.List;

public interface IGuiElement
{

public int getElementNumber();

public void setTooltip(List<String> lines);
public List<String> getTooltip();
public void drawElement(int mouseX, int mouseY);
public void updateGuiPos(int newX, int newY);
public boolean isMouseOver(int x, int y);
public void onMousePressed(int x, int y, int num);
public void onMouseReleased(int x, int y, int num);
public void onMouseMoved(int x, int y, int num);
public void onMouseWheel(int x, int y, int wheel);
public void onKeyTyped(char ch, int keyNum);
public boolean handleMousePressed(int x, int y, int num);
public boolean handleMouseReleased(int x, int y, int num);
public boolean handleMouseMoved(int x, int y, int num);
public boolean handleMouseWheel(int x, int y, int wheel);
public boolean handleKeyInput(char ch, int keyNum);

/**
 * GUIs which have dynamically placed elements outside of the gui space will need to call this every screen init
 * GUIs which have all elements inside of the GUI may set this at time of construction and need not update every tick
 * @param newX
 * @param newY
 * @return
 */
public IGuiElement updateRenderPos(int newX, int newY);

}
