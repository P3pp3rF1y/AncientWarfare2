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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import shadowmage.ancient_framework.client.render.AWTextureManager;

public abstract class GuiElement extends Gui implements IGuiElement
{

protected final IGuiElementCallback parent;
protected static RenderItem itemRenderer = new RenderItem();
protected final int elementNum;
protected int renderPosX;
protected int renderPosY;
protected int width;
protected int height;

/**
 * the most recently pressed button on this element, -1 for none
 */
protected int mouseButton = -1;

/**
 * the X,Y of where the mouse button was pressed or moved to
 */
protected int mouseDownX;
protected int mouseDownY;

/**
 * the X,Y of where the mouse button was pressed or moved from (used with mouseDown to calc move delta)
 */
protected int mouseLastX;
protected int mouseLastY;

/**
 * set by parent GUI to update render pos & mouse interaction relative to scaled screen size/etc. scrollable area uses this to offset render & mousePos relative to the scroll area
 */
protected int guiLeft;
protected int guiTop;

/**
 * cached variable of isMouseOver(x,y). updated onMouseMoved
 */
protected boolean isMouseOver = false;

/**
 * determines interaction status, render status for some elements
 */
public boolean enabled = true;

/**
 * if should render at all, and/or accept input
 */
public boolean hidden = false;

/**
 * if should render offset using guiLeft/guiTop (not fully implemented in all elements...mostly deprecated in favor of renderPos updating)
 */
public boolean renderWithGuiOffset = true;

public boolean renderTooltip = false;
protected List<String> tooltipString = new ArrayList<String>();

protected Minecraft mc;
protected FontRenderer fr;

public GuiElement(int elementNum, IGuiElementCallback parent, int w, int h)
  {
  this.elementNum = elementNum;
  this.parent = parent;
  this.width = w;
  this.height = h;
  this.mc = Minecraft.getMinecraft();
  this.fr = mc.fontRenderer;
  }

public GuiElement addToToolitp(String line)
  {
  this.tooltipString.add(line);
  this.renderTooltip = true;
  return this;
  }

@Override
public void setTooltip(List<String> lines)
  {
  this.tooltipString = lines;
  this.renderTooltip = true;
  }

@Override
public List<String> getTooltip()
  {  
  return tooltipString;
  }

@Override
public GuiElement updateRenderPos(int newX, int newY)
  {
  this.renderPosX = newX;
  this.renderPosY = newY;
  return this;
  }

@Override
public void updateGuiPos(int x, int y)
  {  
  this.guiLeft = x;
  this.guiTop = y; 
  }

@Override
public int getElementNumber()
  {
  return this.elementNum;
  }

@Override
public void onMousePressed(int x, int y, int num)
  {
  if(this.isMouseOver(x, y) && this.mouseButton == -1)
    {
    this.mouseButton = num;
    this.mouseDownX = x;
    this.mouseDownY = y;
    this.mouseLastX = x;
    this.mouseLastY = y;
    if(this.handleMousePressed(x, y, num) && this.parent!=null)
      {
      this.onElementActivated();
      this.parent.onElementActivated(this);
      }
    }
  }

@Override
public void onMouseReleased(int x, int y, int num)
  {  
  if(this.mouseButton >=0 && num==this.mouseButton)
    {
    this.mouseLastX = x;
    this.mouseLastY = y;
    this.mouseButton = -1;
    if(this.handleMouseReleased(x, y, num) && this.parent!=null)
      {
      this.parent.onElementReleased(this);
      }
    }
  }

@Override
public void onMouseMoved(int x, int y, int num)
  {  
  this.isMouseOver = false;
  if(this.isMouseOver(x, y))
    {
    this.isMouseOver = true;
    }
  if(this.mouseButton>=0)
    {
    this.mouseLastX = this.mouseDownX;
    this.mouseLastY = this.mouseDownY;
    this.mouseDownX = x;
    this.mouseDownY = y;
    if(this.handleMouseMoved(x, y, num) && this.parent!=null)
      {
      this.parent.onElementDragged(this);
      }
    }
  }

@Override
public void onMouseWheel(int x, int y, int wheel)
  {
  if(this.isMouseOver(x,y))
    {
    if(this.handleMouseWheel(x, y, wheel))
      {
      this.onElementActivated();
      this.parent.onElementMouseWheel(this, wheel);
      }
    }
  }

public void onKeyTyped(char ch, int keyNum)
  {
  if(this.handleKeyInput(ch, keyNum) && this.parent!=null)
    {
    if(keyNum==Keyboard.KEY_RETURN)
      {
      this.onElementActivated();      
      }
    this.parent.onElementKeyTyped(ch, keyNum);
    }
  }

/**
 * anonymous classes may override this for a single onActivated call
 */
public void onElementActivated()
  {
  
  }

/**
 * renders the four corners of a texture, from the corner inward (e.g. for size-adaptable elements)
 * @param x renderPosX
 * @param y renderPosY
 * @param w renderWidth
 * @param h renderHeight
 * @param tw textureUsedWidth
 * @param th textureUsedHeight
 * @param tex theTexture
 * @param u textureStartX
 * @param v textureStartY
 */
protected void drawQuadedTexture(int x, int y, int w, int h, int tw, int th, String tex, int u, int v)
  {  
  int halfW = w/2;
  int halfH = h/2;  
  int u1 = u + tw - halfW;
  int v1 = v + th - halfH;
  AWTextureManager.bindTexture(tex);
  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  this.drawTexturedModalRect(x, y, u, v, halfW, halfH);
  this.drawTexturedModalRect(x + halfW, y, u1, v, halfW, halfH);
  this.drawTexturedModalRect(x, y + halfH, u, v1, halfW, halfH);
  this.drawTexturedModalRect(x + halfW, y + halfH, u1, v1, halfW, halfH);
  }

public boolean wasMouseOver()
  {
  return this.isMouseOver;
  }

@Override
public boolean isMouseOver(int x, int y)
  {
  if(!this.renderWithGuiOffset)
    {
    return x >=this.renderPosX && x<this.renderPosX+width && y>=this.renderPosY && y<this.renderPosY+height;
    }
  return x >=this.renderPosX+guiLeft && x<this.renderPosX+width+guiLeft && y>=this.renderPosY+guiTop && y<this.renderPosY+height+guiTop;
  }

public void clearMouseButton()
  {
  this.mouseButton =-1;  
  }

public void setMouseButton(int num)
  {
  this.mouseButton = num;
  }

}
