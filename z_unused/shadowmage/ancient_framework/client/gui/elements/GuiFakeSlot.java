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

import net.minecraft.item.ItemStack;

import org.lwjgl.input.Keyboard;

import shadowmage.ancient_framework.common.utils.InventoryTools;

public class GuiFakeSlot extends GuiItemStack
{

public boolean autoUpdateOnClick = true;

/**
 * @param elementNum
 * @param parent
 * @param w
 * @param h
 */
public GuiFakeSlot(int elementNum, IGuiElementCallback parent)
  {
  this(elementNum, parent, 18, 18);
  }

public GuiFakeSlot(int elementNum, IGuiElementCallback parent, int x, int y)
  {
  super(elementNum, parent, x, y);
  this.isFake = true;
  this.renderSlotBackground = true;
  this.isClickable = true;
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  if(!enabled || hidden || !isClickable)
    {
    return false;
    }
  if(this.autoUpdateOnClick)
    {
    ItemStack p = mc.thePlayer.inventory.getItemStack();
    if(p!=null)
      {
      if(InventoryTools.doItemsMatch(fakeStack, p))
        {
        if(num==0)
          {
          if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
            {
            fakeStack.stackSize++;
            }
          else
            {
            fakeStack.stackSize+=p.stackSize;
            }
          }
        else
          {
          fakeStack.stackSize--;
          }
        }
      else if(fakeStack!=null)
        {
        fakeStack=null;
        }
      else
        {
        fakeStack = p.copy();      
        }
      }
    else
      {
      if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
        {
        if(fakeStack!=null)
          {
          if(num==0)
            {
            fakeStack.stackSize++;
            }
          else
            {
            fakeStack.stackSize--;
            }
          }
        }
      else
        {
        fakeStack = null;
        }
      }
    }
  return true;
  }

}
