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

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import org.lwjgl.opengl.GL11;


public class GuiTextBox extends Gui
{

/**
 * basic params
 */
int xSize;
int ySize;
int xPos;
int yPos;

final int charWidth = 7;
final int border = 4;
final int charHeight = 10;

final int displayLines;
final int lineLength;
int textColor;
int backGroundColor;
public boolean activated = false;
boolean fileDirty = false;

/**
 * the actual lines driving the char array to be displayed
 */
List<String> lines;

/**
 * cursor position on screen
 */
int cursorPosX;
int cursorPosY;

/**
 * raw x and y of cursor relative to lines
 */
int cursorRawX;
int cursorRawY;

/**
 * selection start xy
 */
int selectionRawX;
int selectionRawY;

/**
 * selection end xy
 */
int selectionEndRawX;
int selectionEndRawY;

/**
 * data stored in the clipboard.... attempt to grab input from system clipboard first?
 */
List<String> clipboardLines = new ArrayList<String>();


/**
 * leftMost char drawn
 * and topMost drawn char
 */
int viewX;
int viewY;

/**
 * used to trigger updates in viewedChars array
 */
int prevViewX;
int prevViewY;

/**
 * the data that is drawn onto the screen
 */
char[][] screenChars;

/**
 * if true, will update screen characters, and set to false
 */
private boolean dirty = true;

public GuiTextBox(int xSize, int ySize, List<String> lines)
  {
  this(xSize, ySize, 10, 32, 0xffffffff, 0x00000000, lines);  
  }

public GuiTextBox(int xSize, int ySize, int displayLines, int lineLength, int textColorHex, int backColorHex, List<String> lines)
  {
  this.xSize = xSize;
  this.ySize = ySize;
  this.displayLines = displayLines;
  this.lineLength = lineLength;
  this.textColor = textColorHex;
  this.backGroundColor = backColorHex;
  this.lines = lines;
  this.screenChars = new char[displayLines][lineLength];
  if(this.lines!=null)
    {
    this.updateScreenChars();
    }
  }

public boolean isFileDirty()
  {
  return this.fileDirty;
  }

public void setFileClean()
  {
  this.fileDirty = false;
  }

public boolean onKeyTyped(char charValue, int keyCode)
  {  
  if(!this.activated)
    {
    return false;
    }
  
  boolean validChar = false;
  switch(keyCode)
  {
  case 200:
  moveCursorUp();
  break;
  
  case 208:
  moveCursorDown();
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
  
  case 1:
  break;
  
  case 210:
  break;
  
  case 199:
  this.handleHomeAction();
  break;
  
  case 201:
  this.handlePgUpAction();
  break;
  
  case 207:
  this.handleEndAction();
  break;
  
  case 209:
  this.handlePgDownAction();
  break;
  
  case 54://rShift
  case 184://Ralt--Rmenu
  case 220://Rmeta
  case 157://RControl
  case 69://numlock
  case 183://sysReq
  case 70://scrollLock
  case 197://pause
  case 59://f1-f12
  case 60:
  case 61:
  case 62:
  case 63:
  case 64:
  case 65:
  case 66:
  case 67:
  case 68:
  case 87:
  case 88:  
  case 58://capslock
  case 42://Lshift
  case 29://Lcont
  case 219://LMeta
  case 56://Lalt
  break;
  
  default:
  validChar = true;
  break;
  }
  if(validChar)
    {
    this.handleCharAction(charValue);
    }  
  /**
   * TODO
   * shift+arrow keys--highlight selection
   * copy/cut/paste -- copy/cut/paste
   */ 
  return true;
  }

private void setLineAt(String line, int y)
  {
  if(y>=lines.size())
    {
    lines.add(line);    
    }
  else
    {
    this.lines.set(y, line);
    }
  }

private void insertLineAt(String line, int y)
  {
  if(y>=this.lines.size())
    {
    this.lines.add(line);
    }
  else
    {
    this.lines.add(y, line);
    }
  }

private String removeLineAt(int y)
  {
  if(y>=lines.size() || y<0)
    {
    return "";
    }
  String line = lines.get(y);  
  lines.remove(y);
  return line;
  }

private String getLineAt(int y)
  {
  if(y>=lines.size())
    {
    return "";
    }
  return lines.get(y);
  }

private String removeCharAt(String line, int x)
  {
  if(x<0 || x>=line.length())
    {    
    return "";
    }
  String newLine = "";
  for(int i = 0; i < line.length(); i++)
    {
    if(i != x)
      {
      newLine = newLine + String.valueOf(line.charAt(i));
      }
    }
  return newLine;
  }

private String insertCharAt(String line, int x, char ch)
  {
  if(x<0 || x>line.length())
    {    
    return "";
    }
  String newLine = "";
  for(int i = 0; i < x; i++)
    {
    newLine = newLine + String.valueOf(line.charAt(i));
    }
  newLine = newLine + String.valueOf(ch);
  for(int i = x; i <line.length(); i++)
    {
    newLine = newLine + String.valueOf(line.charAt(i));
    }
  return newLine;
  }

private void handleCharAction(char ch)
  {
  if(this.cursorRawY>=this.lines.size())
    {
    this.lines.add(String.valueOf(ch));    
    }
  else
    {
    String line = this.getLineAt(cursorRawY);
    line = this.insertCharAt(line, cursorRawX, ch);
    this.setLineAt(line, cursorRawY);    
    }
  this.moveCursorRight();
  this.setDirty();
  this.fileDirty = true;
  }

private void handleEnterAction()
  {
  if(this.cursorRawY>=this.lines.size())
    {
    this.lines.add("");    
    }
  else
    {
    String line = this.getLineAt(cursorRawY);
    String curLine = line.substring(0, cursorRawX);
    this.setLineAt(curLine, cursorRawY);      
    String nextLine = line.substring(cursorRawX);
    this.insertLineAt(nextLine, cursorRawY+1);
    this.cursorRawX=0;
    this.viewX = 0;    
    }
  this.moveCursorDown();
  this.setDirty();
  this.fileDirty = true;
  }

private void handleEndAction()
  {
  this.setCursorXAtEndOfLine(this.getLineAt(cursorRawY));
  }

private void handleHomeAction()
  {
  this.cursorRawX = 0;
  this.viewX = 0;
  this.updateLocalCursorPos();
  this.setDirty();
  }

private void handlePgDownAction()
  {
  for(int i = 0; i < displayLines; i++)
    {
    this.moveCursorDown();
    }
  }

private void handlePgUpAction()
  {
  for(int i = 0; i < displayLines; i++)
    {
    this.moveCursorUp();
    }
  }

private void handleDeleteAction()
  {
  if(this.cursorRawY >= this.lines.size() || this.cursorRawY<0)
    {
    return;//at end of lines..nothing to delete... || is out of bounds...
    }
  String line = this.getLineAt(cursorRawY);
  if(this.cursorRawX>=line.length())//at end of current line..
    {
    if(this.cursorRawY+1 < this.lines.size())//if there is a next line..
      {
      String nextLine = this.removeLineAt(this.cursorRawY+1);//grab it
      line = line + nextLine;//throw it onto current line
      this.setLineAt(line, cursorRawY);//and set that line as current line in lines array
      this.setDirty();
      }
    }
  else//has chars to delete...
    {
    line = this.removeCharAt(line, cursorRawX);
    this.setLineAt(line, cursorRawY);
    this.setDirty();
    }  
  this.fileDirty = true;
  }

private boolean setDirty()
  {
  boolean dirty = this.dirty;
  this.dirty = true;
  return dirty;
  }

private void handleBackspaceAction()
  {
  if(this.cursorRawX<=0 && this.cursorRawY <=0)
    {
    return;
    }
  if(cursorRawX<=0 && this.cursorRawY>0)
    {
    String line = this.removeLineAt(cursorRawY);
    String prevLine = this.getLineAt(cursorRawY-1);
    this.setCursorXAtEndOfLine(prevLine);
    prevLine = prevLine+line;
    this.setLineAt(prevLine, cursorRawY-1);    
    this.moveCursorUp();
    this.fileDirty = true;
    this.setDirty();
    return;
    }  
  String line = this.getLineAt(cursorRawY);
  line = this.removeCharAt(line, cursorRawX-1);
  this.setLineAt(line, cursorRawY);
  this.moveCursorLeft();
  this.setDirty();
  this.fileDirty = true;
  }

private void setCursorXAtEndOfLine(String line)
  {
  this.cursorRawX = line.length();  
  if(this.cursorRawX < this.viewX)
    {
    this.viewX = this.cursorRawX;
    }
  if(this.cursorRawX > this.viewX + this.lineLength)
    {
    this.viewX = this.cursorRawX - this.lineLength;//TODO this might be off by one...
    }
  this.updateLocalCursorPos();
  this.setDirty();
  }

public void updateLocalCursorPos()
  {
  cursorPosY = cursorRawY - viewY;
  cursorPosX = cursorRawX - viewX;
  }

private void moveCursorUp()
  {
  if(cursorRawY <= 0)
    {
    return;
    } 
  cursorRawY--;
  if(cursorRawY<lines.size())//if this line is a valid line...
    {
    if(cursorRawX > lines.get(cursorRawY).length())//if cursor would now be past end of line
      {
      cursorRawX = lines.get(cursorRawY).length();//set it to end of line
      if(cursorRawX < viewX)
        {
        viewX = cursorRawX;
        }      
      }
    }    
  else//else set cursor to x=0;
    {
    cursorRawX = 0;
    }
  if(cursorRawY < viewY)
    {
    viewY--;    
    }
  if(hasViewChanged())
    {
    this.setDirty();
    }
  updateLocalCursorPos();
  }

private void moveCursorDown()
  {
  if(cursorRawY>=lines.size())
    {
    return;
    }
  cursorRawY++;
  if(cursorRawY<lines.size())//if this line is a valid line...
    {
    if(cursorRawX > lines.get(cursorRawY).length())//if cursor would now be past end of line
      {
      cursorRawX = lines.get(cursorRawY).length();//set it to end of line
      if(cursorRawX < viewX)
        {
        viewX = cursorRawX;
        }
      }
    }    
  else//else set cursor to x=0;
    {
    cursorRawX = 0;
    }
  if(cursorRawY - displayLines >= viewY)
    {
    viewY++;
    }
  if(hasViewChanged())
    {
    this.setDirty();
    }
  updateLocalCursorPos();
  }

private void moveCursorLeft()
  {
  if(cursorRawX <= 0)
    {
    return;
    }
  cursorRawX--;
  if(cursorRawX < viewX)
    {
    viewX--;
    this.setDirty();
    }
  updateLocalCursorPos();
  }

private void moveCursorRight()
  {
  if(cursorRawY >= lines.size())
    {
    return;
    }
  if(cursorRawX >= lines.get(cursorRawY).length())
    {
    return;
    }
  cursorRawX++;  
  if(cursorRawX - lineLength > viewX)
    {    
    viewX++;
    this.setDirty();
    }
  updateLocalCursorPos();
  }

private boolean hasViewChanged()
  {
  return this.viewX != this.prevViewX || this.viewY != this.prevViewY;
  }

public boolean isMouseOver(int x, int y)
  {
  if(x>= this.xPos && x < this.xPos+this.xSize && y >=this.yPos && y < this.yPos+this.ySize)
    {
    return true;
    }  
  return false;
  }

/**
 * has mouse button been depressed on this control?
 */
private boolean buttonDown = false;

public boolean onMouseReleased(int buttonNum, int x, int y)
  {
  if(!isButtonDown())
    {
    return false;
    }
  
  this.setButtonDown(false);
  
  int xMin = this.xPos+this.border;
  int xMax = this.xPos+this.xSize-this.border;
  int yMin = this.yPos+this.border;
  int yMax = this.yPos+this.ySize-this.border;
  if(x>=xMin && x < xMax && y >= yMin && y < yMax)
    {
    return true;
    }
  else
    {
    return false;
    }
  }

/**
 * called when mouse is over, and button pressed
 * @param buttonNum
 * @param x raw screen x
 * @param y raw screen y
 * @return
 */
public boolean onMouseClicked(int buttonNum, int x, int y)
  {
  int xMin = this.xPos+this.border;
  int xMax = this.xPos+this.xSize-this.border;
  int yMin = this.yPos+this.border;
  int yMax = this.yPos+this.ySize-this.border;
  if(x>=xMin && x < xMax && y >= yMin && y < yMax)
    {
    x-=xMin;
    y-=yMin;
    int charX = x/charWidth + viewX;
    int charY = y/charHeight + viewY;
    String line = this.getLineAt(charY);
    this.cursorRawY = charY;
    if(charX>=line.length())
      {
      this.setCursorXAtEndOfLine(line);
      }
    else
      {
      this.cursorRawX = charX;
      }
    this.updateLocalCursorPos();
    this.setDirty();
    return true;
    }  
  return false;
  }

public void updateDrawPos(int x, int y)
  {
  this.xPos = x;
  this.yPos = y;
  }

public void drawTextBox(FontRenderer fontRenderer, int xPos, int yPos)
  {   
  GL11.glPushMatrix();
  GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
  drawRect(xPos - 1, yPos - 1, xPos + this.xSize + 1, yPos + this.ySize + 1, -6250336);
  drawRect(xPos, yPos, xPos + this.xSize, yPos + this.ySize, -16777216);
  if(this.lines==null)
    {
    return;
    }
  if(this.dirty)
    {
    this.updateScreenChars();
    }  
  for(int y = 0; y < this.screenChars.length; y++)
    {
    for(int x = 0; x < this.screenChars[y].length; x++)
      {
      this.renderCharAt(fontRenderer, xPos + border + charWidth*x, yPos + border + charHeight*y, this.screenChars[y][x]);
      }
    }
  this.renderCursor(fontRenderer, xPos + border + charWidth * cursorPosX, yPos + border + charHeight*cursorPosY + 1);
  GL11.glPopAttrib();
  GL11.glPopMatrix();
  }

private void renderCursor(FontRenderer fontRenderer, int posX, int posY)
  {
  fontRenderer.drawString("_", posX, posY, 0xffff7f7f, false);
  }

private void updateScreenChars()
  {
  if(!this.dirty)
    {
    return;
    }
  this.dirty = false;
  this.prevViewX = this.viewX;
  this.prevViewY = this.viewY;
  for(int y = 0; y < this.displayLines; y++)
    {
    int currentLine = viewY + y;
    if(currentLine>=lines.size())
      {
      for(int x = 0; x < this.lineLength; x++)
        {
        this.screenChars[y][x] = ' ';
        }
      }
    else
      {
      for(int x = 0; x < this.lineLength; x++)
        {
        int charIndex = this.viewX + x;
        String line = lines.get(currentLine);
        if(charIndex>=line.length())
          {
          this.screenChars[y][x] = ' ';
          }
        else
          {
          this.screenChars[y][x] = line.charAt(charIndex);
          }
        }
      } 
    }
  }

private void renderCharAt(FontRenderer fontRenderer, int x, int y, char ch)
  {  
  //TODO not make this so freaking ghetto--- e.g. write a proper char renderer instead of 
  //using an entire string renderer to render a single char
  int wid = fontRenderer.getCharWidth(ch);
  int xOff = (7-wid)/2;
  fontRenderer.drawString(String.valueOf(ch), x+xOff, y, textColor, false);
  
  }

/**
 * @return the buttonDown
 */
public boolean isButtonDown()
  {
    return buttonDown;
  }

/**
 * @param buttonDown the buttonDown to set
 */
public void setButtonDown(boolean buttonDown)
  {
    this.buttonDown = buttonDown;
  }

}
