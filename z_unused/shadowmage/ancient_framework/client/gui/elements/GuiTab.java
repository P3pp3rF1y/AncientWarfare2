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

import net.minecraft.client.Minecraft;
import shadowmage.ancient_framework.common.config.Statics;

public class GuiTab extends GuiElement
{

public boolean inverted = false;
protected String displayString = "";
/**
 * @param elementNum
 * @param parent
 * @param w
 * @param h
 */
public GuiTab(int elementNum, IGuiElementCallback parent, int w, int h, String name)
  {
  super(elementNum, parent, w, h);
  if(name!=null)
    {
    this.displayString = name;
    }
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {
  if(!this.hidden)
    {
    int texOffset = this.getHoverState();
    int vOffset = texOffset * 24;//will return 0, 40, or 80..for inactive, active, hover, apply to Y offset in UV rendering
    vOffset = inverted ? vOffset + 24*2 : vOffset;
    int guiLeftOffset = this.renderWithGuiOffset ? this.guiLeft : 0;
    int guiTopOffset = this.renderWithGuiOffset ? this.guiTop : 0;    
    String tex = Statics.TEXTURE_PATH+"gui/guiButtons.png";
    this.drawQuadedTexture(guiLeftOffset+renderPosX, guiTopOffset+renderPosY, width, height, 104, 24, tex, 152, vOffset+138);
    int fontColor = 14737632;
    if(this.isMouseOver)
      {
      fontColor = 16777120;
      }
    else if(this.enabled)
      {
      fontColor = 0xffffffff;
      }
    this.drawCenteredString(Minecraft.getMinecraft().fontRenderer, this.displayString, guiLeftOffset + this.renderPosX + this.width / 2, guiTopOffset + this.renderPosY + (this.height - 8) / 2, fontColor);
    }
  }

protected int getHoverState()
  {
  byte renderState = 0;
  if(!this.enabled)
    {
    renderState = 1;
    }  
  return renderState;
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  if(!this.hidden)
    {
    this.mc.sndManager.playSoundFX("random.click", 1.0F, 1.0F);
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

}
