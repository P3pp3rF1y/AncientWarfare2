package net.shadowmage.ancientwarfare.core.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class AWTextureManager
{

public static AWTextureManager instance(){return instance;}
private static AWTextureManager instance = new AWTextureManager();
private AWTextureManager(){}

private HashMap<Integer, Texture> textures = new HashMap<Integer, Texture>();
private HashMap<String, Integer> textureNames = new HashMap<String, Integer>();

/**
 * buffers for texture creation / alteration
 */
private IntBuffer dataBuffer = BufferUtils.createIntBuffer(1024*2048);
private int[] inBuff = new int[256*256];
private int[] outBuff = new int[256*256];
private int bufferSize = 256*256;

private void bindTexture(int texNum)
  {
  if(this.textures.containsKey(texNum))
    {
    this.textures.get(texNum).bindTexture();
    }
  else
    {
    throw new IllegalArgumentException("Could not locate texture for texNum: "+texNum);
    }
  }

public void loadTexture(String name, BufferedImage image)
  {
  int num = 0;
  Texture t = new Texture(num, image);
  textureNames.put(name, num);
  textures.put(num, t);
  t.updateTexture(image);
  }

public void bindTexture(String name)
  {
  if(textureNames.containsKey(name))
    {
    this.bindTexture(textureNames.get(name));
    }
  else
    {
    throw new IllegalArgumentException("Could not locate texture for name: "+name);
    }
  }

public void deleteTexture(String name)
  {
  if(textureNames.containsKey(name))
    {
    int tex = textureNames.get(name);
    textures.remove(tex);
    GL11.glDeleteTextures(tex);
    }
  }

private static void uploadTextureRGBAInts(IntBuffer imagedata, int width, int height)
  { 
  GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL12.GL_UNSIGNED_INT_8_8_8_8, imagedata); 
  }

/**
 * converts the input pixel from ARGB to RGBA using simple bit-shifting operations
 * @param pixel in ARGB format
 * @return pixel in RGBA format
 */
private static int ARGBtoRGBA(int pixel)
  {
  int out = 0;
//  int a, r, g, b;
//  a = (pixel >> 24) & 0xff;
//  r = (pixel >> 16) & 0xff;
//  g = (pixel >> 8 ) & 0xff;
//  b = (pixel >> 0 ) & 0xff;  
//  out = (a<<0) | (b<<8) | (g<<16) | (r<<24);
  out = ((pixel&0x00ffffff)<<8) | ((pixel&0xff000000)>>24);
  return out;
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
//    row = height-y-1;
    row = y;
    for(int x = 0; x < width; x++)
      {
      outBuff[row*width + x] = ARGBtoRGBA(inBuff[y*width + x]);
      }
    }
  buffer.put(outBuff);
  buffer.flip();
  }

public void updateTextureContents(String texName, BufferedImage image)
  {
  if(textureNames.containsKey(texName))
    {
    Texture t = textures.get(textureNames.get(texName));
    if(t==null){return;}  
    if(image!=null)
      {   
      t.updateTexture(image);    
      }  
    }  
  }

public Texture getTexture(String name)
  {
  if(textureNames.containsKey(name))
    {
    return textures.get(textureNames.get(name));
    }
  return null;
  }

public class Texture
{

int texNum;
BufferedImage image;

public Texture(int texNum, BufferedImage image)
  {
  this.texNum = texNum;
  this.image = image;
  }

public BufferedImage getImage()
  {
  return image;
  }

public void updateTexture(BufferedImage image)
  {  
  this.image = image;
  this.uploadImage();   
  }

private void uploadImage()
  {
  int previousTex = GL11.glGetInteger(GL11.GL_TEXTURE_2D);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, texNum);  
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
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTex);
  }

public void saveTexture(File file)
  {
  try
    {
    ImageIO.write(image, "png", file);
    } 
  catch (IOException e)
    {
    e.printStackTrace();
    }
  }

public void bindTexture()
  {
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, texNum);  
  }

}

}
