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

import org.lwjgl.opengl.GL11;

import net.minecraft.util.ChatAllowedCharacters;
import shadowmage.ancient_framework.common.utils.StringTools;

public class GuiTextInputLine extends GuiElement
{

String text = "";

/**
 * cursor position in the text string
 */
int cursorPos;

/**
 * rendering offset of cursor
 */
int cursorOffset;

/**
 * ...currently unused
 */
int backGroundColor;
int foreGroundColor;
int textColor = 0xffffffff;
int cursorColor = 0xffff0000;

/**
 * is focused, can capture text
 */
public boolean selected;

int maxChars = 1;

/**
 * @param elementNum
 * @param parent
 * @param o
 * @param oY
 * @param w
 * @param h
 * @param defaultText
 */
public GuiTextInputLine(int elementNum, IGuiElementCallback parent, int w, int h, int maxChars, String defaultText)
  {
  super(elementNum, parent, w, h);
  this.maxChars = maxChars;  
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {
  if(this.hidden)
    {
    return;
    }
  int xPos = this.renderPosX + guiLeft;
  int yPos = this.renderPosY + guiTop;
  int syPos = yPos + ((this.height-8)/2);
  drawRect(xPos - 1, yPos - 1, xPos + width + 1, yPos + height + 1, -6250336);
  drawRect(xPos, yPos, xPos + width, yPos + height, -16777216);  
  this.fr.drawString(this.text, xPos+2, syPos, textColor, false);
  //TODO move this to a proper update position
  if(this.selected)
    {
    this.updateCursorOffset();
    this.fr.drawString("_", xPos+2+cursorOffset, syPos, cursorColor, false);
    GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    }
  }

@Override
public void onMousePressed(int x, int y, int num)
  {
  boolean wasSelected = this.selected;
  this.selected = false;
  super.onMousePressed(x, y, num);
  if(this.selected && !wasSelected)
    {
    this.cursorPos = this.text.length();
    }
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  this.selected = true;
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
public boolean handleKeyInput(char ch, int keyCode)
  {
  if(this.selected && this.enabled && !this.hidden)
    {          
    switch(keyCode)
    {
    case 200://up arrow
    break;

    case 208://dwn arrow
    break;

    case 203:
    moveCursorLeft();
    break;

    case 205:
    moveCursorRight();
    break;

    case 211:
    handleDeleteAction();
    break;

    case 14:
    handleBackspaceAction();
    break;

    case 28:
    handleEnterAction();
    break;

    case 1://escape
    this.selected = false;
    break;

    case 210:
    break;

    case 199:
    this.handleHomeAction();
    break;

    case 201://pg up
    break;

    case 207:
    this.handleEndAction();
    break;

    case 209://pg dwn
    break;

    case 54://rShift
    case 184://Ralt--Rmenu
    case 220://Rmeta
    case 157://RControl
    case 69://numlock
    case 183://sysReq
    case 70://scrollLock
    case 197://pause
    case 59://f1
    case 60://f2
    case 61://f3
    case 62://f4
    case 63://f5
    case 64://f6
    case 65://f7
    case 66://f8
    case 67://f9
    case 68://f10
    case 87://f11
    case 88://f12  
    case 58://capslock
    case 42://Lshift
    case 29://Lcont
    case 219://LMeta
    case 56://Lalt
    break;

    default:
    if(this.isValidChar(ch))
      {
      this.handleCharAction(ch);
      return true;
      } 
    else
      {
      return false;
      }
    }   
    return true;
    }
  return false;
  }

protected boolean isValidChar(char ch)
  {
  if(ChatAllowedCharacters.isAllowedCharacter(ch))
    {
    return true;
    } 
  else
    {
    return false;
    }
  }

public void setText(String text)
  {
  this.text = text;
  }

public String getText()
  {
  return this.text;
  }

protected void updateCursorOffset()
  {
  cursorOffset = 0;
  for(int i = 0; i < this.cursorPos; i++)
    {
    if(i<this.text.length())
      {
      cursorOffset += fr.getCharWidth(text.charAt(i));
      }
    else
      {
      break;
      }
    }
  }

protected void moveCursorRight()
  {
  cursorPos++;
  if(cursorPos>this.text.length())
    {
    cursorPos = this.text.length();
    }
  }

protected void moveCursorLeft()
  {
  cursorPos--;
  if(cursorPos<0)
    {
    cursorPos = 0;
    }
  }

protected void handleHomeAction()
  {
  this.cursorPos = 0;
  }

protected void handleEndAction()
  {
  this.cursorPos = this.text.length();
  }

protected void handleCharAction(char ch)
  {
  String firstPart = "";
  String lastPart = "";
  this.cursorPos = this.cursorPos > this.text.length() ? this.text.length() : this.cursorPos;
  firstPart = this.text.substring(0, this.cursorPos);
  lastPart = this.text.substring(cursorPos, text.length());
  this.text = firstPart + ch + lastPart;
  this.moveCursorRight();
  }

private void handleDeleteAction()
  {  
  this.text = StringTools.removeCharAt(text, cursorPos);
  this.parent.onElementActivated(this);
  }

private void handleBackspaceAction()
  {
  this.text = StringTools.removeCharAt(text, cursorPos-1);
  this.moveCursorLeft();
  }

private void handleEnterAction()
  {
  this.selected = false;
  this.parent.onElementActivated(this);
  }

}
