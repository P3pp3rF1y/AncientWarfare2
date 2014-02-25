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

import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

import shadowmage.ancient_framework.client.gui.GuiContainerAdvanced;

public class GuiScrollableAreaSimple extends GuiElement implements IGuiElementCallback
{

int scrollPosX;//topLeft of the screen currently being drawn 
int scrollPosY;//topLeft of the screen currently being drawn

int parentGuiWidth;
int parentGuiHeight;

public int totalHeight;
protected int totalWidth;

public List<GuiElement> elements = new ArrayList<GuiElement>();

protected GuiContainerAdvanced parentGui;

GuiScrollBarSimple scrollBar;

public GuiScrollableAreaSimple(int elementNum, GuiContainerAdvanced parent, int x, int y, int w,  int h, int totalWidth, int totalHeight)
  {
  super(elementNum, parent, w, h);
  this.parentGui = parent;
  this.parentGuiWidth = parent.getXSize();
  this.parentGuiHeight = parent.getYSize();
  this.renderPosX = x;
  this.renderPosY = y;
  this.totalHeight = totalHeight;  
  }

public void setHeight(int height)
  {
  this.height = height;
  if(this.height<0)
    {
    this.height = 0;
    }
  }

public void updateTotalHeight(int height)
  {
  this.totalHeight = height;
  }

public void addGuiElement(GuiElement el)
  {
  this.elements.add(el);
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {
  this.setupViewport();  
  mouseX = mouseX - this.scrollPosX - this.guiLeft - this.renderPosX;
  mouseY = mouseY + this.scrollPosY - this.guiTop - this.renderPosY;  
  if(this.scrollBar!=null)
    {
    GL11.glPushMatrix();
    this.scrollBar.updateHandleHeight(totalHeight, height);  
    this.scrollPosY = this.scrollBar.getTopIndexForSet(totalHeight, height);  
    this.scrollBar.updateGuiPos(0, 0);
    this.scrollBar.drawElement(mouseX, mouseY-scrollPosY);
    GL11.glPopMatrix();
    }
  for(GuiElement el : this.elements)
    {    
    if(el.renderPosX<width || el.renderPosX+el.width > 0 || el.renderPosY<height || el.renderPosY+el.height>0)
      {
      GL11.glPushMatrix();
      el.drawElement(mouseX, mouseY);  
      GL11.glPopMatrix();    
      }    
    }  
  this.resetViewPort();
  }

public void setupViewport()
  {
  GL11.glPushMatrix();
  ScaledResolution scaledRes = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);
  int guiScale = scaledRes.getScaleFactor();
//  float vAspect = (float)this.mc.displayWidth/(float)this.mc.displayHeight;
  float w = this.width * guiScale;
  float h = height * guiScale;
  float x = guiLeft*guiScale + renderPosX*guiScale;
  float y = guiTop*guiScale + parentGui.getYSize()*guiScale - h - renderPosY*guiScale;
  float scaleY = (float)mc.displayHeight / h;
  float scaleX = (float)mc.displayWidth / w;  
  GL11.glViewport((int)x, (int)y, (int)w, (int)h);  
  GL11.glScalef(scaleX, scaleY, 1);
  }

public void resetViewPort()
  {
  GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
  GL11.glPopMatrix();
  }

@Override
public void updateGuiPos(int x, int y)
  {  
  this.guiLeft = x;
  this.guiTop = y; 
  if(this.scrollBar!=null)
    {
    this.scrollBar.updateHandleHeight(totalHeight, this.height);
    this.scrollPosY = this.scrollBar.getTopIndexForSet(totalHeight, height);
    }  
  for(GuiElement el : this.elements)
    {
    el.updateGuiPos(scrollPosX, -scrollPosY);
    }
  }

@Override
public void onMousePressed(int x, int y, int num)
  {
  int adjX = x - this.guiLeft - renderPosX;
  int adjY = y - this.guiTop - renderPosY;
  if(this.isMouseOver(x,y))
    {
    if(this.scrollBar!=null)
      {
      this.scrollBar.onMousePressed(adjX, adjY, num);  
      }
    for(GuiElement el : this.elements)
      {
      el.onMousePressed(adjX, adjY, num);
      }
    }
  super.onMousePressed(x, y, num);  
  }

@Override
public void onMouseReleased(int x, int y, int num)
  { 
  int adjX = x - this.guiLeft - renderPosX;
  int adjY = y - this.guiTop - renderPosY;
  if(this.scrollBar!=null)
    {
    this.scrollBar.onMouseReleased(adjX, adjY, num);
    }
  for(GuiElement el : this.elements)
    {
    el.onMouseReleased(adjX, adjY, num);
    } 
  super.onMouseReleased(x, y, num);  
  }

@Override
public void onMouseMoved(int x, int y, int num)
  {
  int adjX = x - this.guiLeft - renderPosX;
  int adjY = y - this.guiTop - renderPosY;
  boolean over = this.isMouseOver(x,y);
  for(GuiElement el : this.elements)
    {
    el.isMouseOver = false;
    if(over)
      {
      el.onMouseMoved(adjX, adjY, num);
      }
    }
  if(this.scrollBar!=null)
    {
    this.scrollBar.isMouseOver = false;
    if(over)
      {
      this.scrollBar.onMouseMoved(adjX, adjY, num);
      this.updateGuiPos(parentGui.getGuiLeft(), parentGui.getGuiTop());
      }
    }
  super.onMouseMoved(x, y, num);  
  }

@Override
public void onMouseWheel(int x, int y, int wheel)
  {
  super.onMouseWheel(x, y, wheel);
  if(this.isMouseOver(x,y))
    {
    int adjX = x - this.guiLeft - renderPosX;
    int adjY = y - this.guiTop - renderPosY;
    boolean overElement = false;
    for(GuiElement el : this.elements)
      {
      el.onMouseWheel(adjX, adjY, wheel);
      if(el.isMouseOver(adjX, adjY))
        {
        overElement = true;
        }
      }    
    if(this.scrollBar!=null && !overElement)
      {
      this.scrollBar.handleMouseWheel(x, y, wheel);
      this.scrollBar.updateHandleHeight(totalHeight, this.height);
      this.updateGuiPos(parentGui.getGuiLeft(), parentGui.getGuiTop());
      }    
    }
  }

public void onKeyTyped(char ch, int keyNum)
  {  
  for(GuiElement el : this.elements)
    {
    el.onKeyTyped(ch, keyNum);
    } 
  if(this.scrollBar!=null)
    {
    this.scrollBar.onKeyTyped(ch, keyNum);
    }
  super.onKeyTyped(ch, keyNum); 
  }

public void updateScrollPos(int x, int y)
  {  
  int deltaX = this.mouseDownX-this.mouseLastX;
  int deltaY = this.mouseDownY-this.mouseLastY;
  x = this.scrollPosX + deltaX;
  y = this.scrollPosY + deltaY;
  if(x<0)
    {
    x=0;
    }
  int adjWidth = this.totalWidth-this.width;
  if(this.scrollBar!=null)
    {
    adjWidth -= 16;
    }
  if(x>adjWidth)
    {
    x = adjWidth;
    }
  if(y<0)
    {
    y = 0;
    }
  if(y>this.totalHeight-this.height)
    {
    y = this.totalHeight-this.height;
    }
  if(x !=this.scrollPosX || y !=this.scrollPosY)
    {
    this.scrollPosX = x;
    this.scrollPosY = y;
    }
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {  
  
  return false;
  }

@Override
public boolean handleMouseReleased(int x, int y, int num)
  { 
  if(this.scrollBar==null)
    {   
    this.updateScrollPos(x, y);
    }
  return true;
  }

@Override
public boolean handleMouseMoved(int x, int y, int num)
  {
  if(this.scrollBar==null)
    {
    this.updateGuiPos(parentGui.getGuiLeft(), parentGui.getGuiTop());
    this.updateScrollPos(x, y);
    }
  return true;
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

@Override
public void onElementActivated(IGuiElement element)
  {
  this.parent.onElementActivated(element);
  }

@Override
public void onElementReleased(IGuiElement element)
  {
  this.parent.onElementReleased(element);
  }

@Override
public void onElementDragged(IGuiElement element)
  {
  this.parent.onElementDragged(element);
  }

@Override
public void onElementMouseWheel(IGuiElement element, int amt)
  {
  this.parent.onElementMouseWheel(element, amt);
  }

@Override
public void onElementKeyTyped(char ch, int keyNum)
  {
  this.parent.onElementKeyTyped(ch, keyNum);
  }

}
