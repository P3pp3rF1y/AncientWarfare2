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
package shadowmage.meim.client.gui;

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.client.gui.elements.GuiButtonSimple;
import shadowmage.ancient_framework.client.gui.elements.GuiScrollableArea;
import shadowmage.ancient_framework.common.container.ContainerBase;

public class PrimitiveDummySetup extends PrimitiveGuiSetup
{

public PrimitiveDummySetup(GuiModelEditor gui, GuiModelEditorSetup setup)
  {
  super(gui, setup);
  }

@Override
public void addElements(GuiScrollableArea area)
  {
  int totalHeight = 0;
  area.elements.clear();
  
  GuiButtonSimple button = new GuiButtonSimple(0, area, 84, 12, "Add Primitive")
    {
    @Override
    public boolean handleMousePressed(int x, int y, int num)
      {
      if(super.handleMousePressed(x, y, num))
        {
        Minecraft.getMinecraft().displayGuiScreen(new GuiNewPrimitive((ContainerBase) gui.inventorySlots, gui));
        }
      return true;
      }
    };  
  button.updateRenderPos(0, totalHeight);
  totalHeight+=12;
  area.elements.add(button);
  
  area.updateTotalHeight(totalHeight);
  
  }

public void updateButtonValues()
  {
 
  }
}
