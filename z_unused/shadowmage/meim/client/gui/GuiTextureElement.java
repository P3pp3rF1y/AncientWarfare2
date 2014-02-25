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

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import shadowmage.ancient_framework.client.gui.elements.GuiElement;
import shadowmage.ancient_framework.client.gui.elements.IGuiElementCallback;

public class GuiTextureElement extends GuiElement
{

private IntBuffer dataBuffer = BufferUtils.createIntBuffer(1024*2048);
private int[] inBuff = new int[256*256];
private int[] outBuff = new int[256*256];
private int bufferSize = 256*256;

BufferedImage image;
int openGLTextureNumber = 0;
private int prevTexNum;

int viewX;//top-leftX of current view of texture
int viewY;//top-leftY of current view of texture
float scale;//zoom factor applied to view of texture

public GuiTextureElement(int elementNum, IGuiElementCallback parent, int w, int h, BufferedImage image)
  {
  super(elementNum, parent, w, h); 
  this.image = image;
  this.allocateTexture();
  this.updateTextureContents(image);
  scale = 1.f;
  }

public void updateImage(BufferedImage image)
  {
  this.image = image;
  this.updateTextureContents(image);
  }

private void uploadTextureRGBAInts(IntBuffer imagedata, int width, int height)
  {
  GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8, imagedata); 
  }

private void bindTexture()
  {
  prevTexNum = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, openGLTextureNumber);
  }

public void resetBoundTexture()
  {
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexNum);
  }

private void updateTextureContents(BufferedImage image)
  {
  bindTexture();
  if(image!=null)
    {     
    int size = image.getWidth()*image.getHeight();
    if(bufferSize < size)
      {
      bufferSize = size;
      dataBuffer = BufferUtils.createIntBuffer(bufferSize);
      inBuff = new int[bufferSize];
      outBuff = new int[bufferSize];
      }  
    dataBuffer.clear();
    fillImageArray(image, dataBuffer);    
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //GL11.GL_NEAREST);
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); //GL11.GL_NEAREST);
    uploadTextureRGBAInts(dataBuffer, image.getWidth(), image.getHeight());   
    }  
  resetBoundTexture();
  }

/**
 * fills an intBuffer with pixel data from the input image
 * @param image to read from
 * @param buffer to fill
 */
private void fillImageArray(BufferedImage image, IntBuffer buffer)
  {
  int width = image.getWidth();
  int height = image.getHeight();  
  int row = 0; 
  image.getRGB(0, 0, width, height, inBuff, 0, width);
  for(int y = 0; y < height; y++)
    {
    row = y;
//    row = height-y-1;
    for(int x = 0; x < width; x++)
      {
      outBuff[row*width + x] = ARGBtoRGBA(inBuff[y*width + x]);
      }
    }
  buffer.put(outBuff);
  buffer.flip();
  }

/**
 * converts the input pixel from ARGB to RGBA using simple bit-shifting operations
 * @param pixel in ARGB format
 * @return pixel in RGBA format
 */
private int ARGBtoRGBA(int pixel)
  {
  int out = 0;
  int a, r, g, b;
  a = (pixel >> 24) & 0xff;
  r = (pixel >> 16) & 0xff;
  g = (pixel >> 8 ) & 0xff;
  b = (pixel >> 0 ) & 0xff;  
  out = (a<<0) | (b<<8) | (g<<16) | (r<<24);
  return out;
  }

public void allocateTexture()
  {
  if(openGLTextureNumber>0)
    {
    GL11.glDeleteTextures(openGLTextureNumber);
    }
  openGLTextureNumber = GL11.glGenTextures(); 
  this.updateTextureContents(image); 
  }

@Override
public void drawElement(int mouseX, int mouseY)
  {
  int x = renderPosX + guiLeft;
  int y = renderPosY + guiTop;
  int x1 = x;
  int y1 = y;
  int x2 = x;
  int y2 = y + height;
  int x3 = x + width;
  int y3 = y + height;
  int x4 = x + width;
  int y4 = y;
  
  
  float pixX = 1.f / (float)image.getWidth();
  float pixY = 1.f / (float)image.getHeight();
  float u1, v1, u2, v2, u3, v3, u4, v4;
  float uw, uh;
    
  uw = scale;//image.getWidth() * pixX * scale;
  uh = scale;
  
  float u = pixX * (float)viewX; 
  float v = pixY * (float)viewY;
  v = v > 1 ? 1 : v;
  
  u1 = u;
  v1 = v;    
  u2 = u;
  v2 = v + uh;
  u3 = u + uw;
  v3 = v + uh;
  u4 = u + uw;
  v4 = v;
  
  
  bindTexture();
  GL11.glBegin(GL11.GL_QUADS);
  
  GL11.glTexCoord2f(u1, v1);
  GL11.glVertex3f(x1, y1, 0);
  GL11.glTexCoord2f(u2, v2);
  GL11.glVertex3f(x2, y2, 0);
  GL11.glTexCoord2f(u3, v3);
  GL11.glVertex3f(x3, y3, 0);
  GL11.glTexCoord2f(u4, v4);
  GL11.glVertex3f(x4, y4, 0);
  
  GL11.glEnd();
  resetBoundTexture();
  
  
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  
  GL11.glColor4f(1.f, 0.f, 0.f, 1.f);
  GL11.glPointSize(5);
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f(x, y, 0.f);
  GL11.glEnd();
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

@Override
public boolean handleMousePressed(int x, int y, int num)
  {
  if(num==0)
    {
    scroll = true;
    lastX = x;
    lastY = y;
    }
  return false;
  }

boolean scroll = false;
int lastX;
int lastY;

@Override
public boolean handleMouseReleased(int x, int y, int num)
  {
  if(num==0)
    {
    scroll = false;
    lastX = x;
    lastY = y;
    }
  return false;
  }

@Override
public boolean handleMouseMoved(int x, int y, int num)
  {
  if(scroll)
    {
    int dx = x - lastX;
    int dy = y - lastY;
    viewX -= dx;
    viewY -= dy;
    if(viewX<0){viewX = 0;}
    if(viewY<0){viewY = 0;}
    }
  lastX = x;
  lastY = y;
  return false;
  }

@Override
public boolean handleMouseWheel(int x, int y, int wheel)
  {
  if(wheel>0)
    {
    scale *= 0.99f;    
    }
  else
    {
    scale *= 1.010101f;
    }
  return false;
  }

@Override
public boolean handleKeyInput(char ch, int keyNum)
  {
  return false;
  }

}
