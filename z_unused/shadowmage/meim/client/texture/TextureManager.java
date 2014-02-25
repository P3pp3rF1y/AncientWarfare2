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
package shadowmage.meim.client.texture;

import java.awt.image.BufferedImage;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * single-texture texture manager, for use by MEIM for texture-map output textures
 *
 */
public class TextureManager
{

static int texNum = -1;
static int prevTexNum = -1;
private static IntBuffer dataBuffer = BufferUtils.createIntBuffer(1024*2048);
private static int[] inBuff = new int[256*256];
private static int[] outBuff = new int[256*256];
private static int bufferSize = 256*256;

public static void allocateTexture()
  {
  if(texNum>=0)
    {
    GL11.glDeleteTextures(texNum);
    }
  texNum = GL11.glGenTextures();
  bindTexture();    
  for(int i =0; i < 256*256 ; i++)
    {
    dataBuffer.put(i, 0xfffffff); 
    }
  dataBuffer.rewind();
  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //GL11.GL_NEAREST);
  GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); //GL11.GL_NEAREST);
  uploadTextureRGBAInts(dataBuffer, 256, 256);//upload empty data to texture so that it is 'valid'?
  resetBoundTexture();
  }

public static void bindTexture()
  {
  prevTexNum = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, texNum);
  }

public static void resetBoundTexture()
  {
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexNum);
  }

public static void updateTextureContents(BufferedImage image)
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
 * upload the imageData to openGL using the currently bound texture number
 * @param imagedata in RGBA format
 * @param width
 * @param height
 */
private static void uploadTextureRGBAInts(IntBuffer imagedata, int width, int height)
  { 
  GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8, imagedata); 
  }

/**
 * fills an intBuffer with pixel data from the input image
 * @param image to read from
 * @param buffer to fill
 */
private static void fillImageArray(BufferedImage image, IntBuffer buffer)
  {
  int width = image.getWidth();
  int height = image.getHeight();  
  int row = 0; 
  image.getRGB(0, 0, width, height, inBuff, 0, width);
  for(int y = 0; y < height; y++)
    {
    row = height-y-1;
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
private static int ARGBtoRGBA(int pixel)
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

public static void saveTexture(String fileName)
  {
  
  }

}
