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
package shadowmage.ancient_framework.client.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.StringTools;

public class PrimitiveBox extends Primitive
{
float x1, y1, z1, x2, y2, z2;//extends of bounding box in local space (post rotation) -- used for w/l/h for boxes
public PrimitiveBox(ModelPiece parent)
  {
  super(parent);
  }

public float x1(){return x1;}
public float y1(){return y1;}
public float z1(){return z1;}
public float width(){return x2-x1;}
public float height(){return y2-y1;}
public float length(){return z2-z1;}

public PrimitiveBox(ModelPiece parent, float x1, float y1, float z1, float x2, float y2, float z2, float rx, float ry, float rz, float tx, float ty)
  {
  this(parent);
  this.x1 = x1;
  this.y1 = y1;
  this.z1 = z1;
  this.x2 = x2;
  this.y2 = y2;
  this.z2 = z2;
  this.rx = rx;
  this.ry = ry;
  this.rz = rz;
  this.setTx(tx);
  this.setTy(ty);
  }

public void setBounds(float x1, float y1, float z1, float width, float height, float length)
  {
  this.x1 = x1;
  this.x2 = x1 + width;
  this.y1 = y1;
  this.y2 = y1 + height;
  this.z1 = z1;
  this.z2 = z1 + length;
  this.setCompiled(false);
  }

@Override
public Primitive copy()
  {
  PrimitiveBox box = new PrimitiveBox(parent);
  box.setBounds(x1, y1, z1, x2-x1, y2-y1, z2-z1);
  box.setOrigin(x, y, z);
  box.setRotation(rx, ry, rz);
  box.setTx(tx());
  box.setTy(ty());
  return box;
  }

@Override
protected void renderForDisplayList()
  {
  
  float tw = parent.getModel().textureWidth;
  float th = parent.getModel().textureHeight;
  float px = 1.f/tw;
  float py = 1.f/th;
  float w = (x2-x1)*16.f;
  float h = (y2-y1)*16.f;
  float l = (z2-z1)*16.f;
  float ty = this.ty();
  float tx = this.tx();
  
  float tx1, ty1, tx2, ty2;
  
//render the cube. only called a single time when building the display list for a piece
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);}  
  
 
  GL11.glBegin(GL11.GL_QUADS);

  AWLog.logDebug("tx, ty: "+tx+","+ty);
  AWLog.logDebug("w,l,h: "+w+","+l+","+h);
  
  //front side  
  tx1 = (tx + l)*px;  
  ty1 = (th - (ty + l + h))*py;
  tx2 = (tx + l + w)*px;
  ty2 = (th - (ty + l))*py;    
  GL11.glNormal3f(0, 0, 1);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x1, y1, z2);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x2, y1, z2);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x2, y2, z2);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x1, y2, z2);
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));
   
  ////rear side
  tx1 = (tx + l + l + w)*px;  
  ty1 = (th - (ty + l + h))*py; 
  tx2 = (tx + l + w + l + w)*px;
  ty2 = (th - (ty + l))*py;
  GL11.glNormal3f(0, 0, -1);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x2, y1, z1);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x1, y1, z1);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x1, y2, z1);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x2, y2, z1); 
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));
  
  //right side
  tx1 = (tx + l + w)*px;  
  ty1 = (th - (ty + l + h))*py;
  tx2 = (tx + l + w + l)*px;
  ty2 = (th - (ty + l))*py; 
  GL11.glNormal3f(1, 0, 0);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x1, y1, z1);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x1, y1, z2);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x1, y2, z2);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x1, y2, z1);
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));
  
//  //left side
  tx1 = (tx)*px;  
  ty1 = (th - (ty + l + h))*py;
  tx2 = (tx + l)*px;
  ty2 = (th - (ty + l))*py; 
  GL11.glNormal3f(-1, 0, 0);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x2, y1, z2);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x2, y1, z1);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x2, y2, z1);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x2, y2, z2);
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));
  
//  //top side
  tx1 = (tx + l)*px;  
  ty1 = (th - (ty + l))*py;
  tx2 = (tx + l + w)*px;
  ty2 = (th - (ty))*py; 
  GL11.glNormal3f(0, 1, 0);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x2, y2, z1);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x1, y2, z1);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x1, y2, z2);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x2, y2, z2);
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));
  
//  //bottom side
  tx1 = (tx + l + w)*px;  
  ty1 = (th - (ty + l))*py;
  tx2 = (tx + l + w + w)*px;
  ty2 = (th - (ty))*py; 
  GL11.glNormal3f(0, -1, 0);
  GL11.glTexCoord2f(tx1, ty1);
  GL11.glVertex3f(x2, y1, z2);
  GL11.glTexCoord2f(tx2, ty1);
  GL11.glVertex3f(x1, y1, z2);
  GL11.glTexCoord2f(tx2, ty2);
  GL11.glVertex3f(x1, y1, z1);
  GL11.glTexCoord2f(tx1, ty2);
  GL11.glVertex3f(x2, y1, z1);
  AWLog.logDebug(String.format("t: %.4f, %.4f, %.4f, %.4f", tx1, ty1, tx2, ty2));

    
  GL11.glEnd();
  }

@Override
public void addPrimitiveLines(ArrayList<String> lines)
  {
  StringBuilder b = new StringBuilder("box=").append(parent.getName()).append(",");
  b.append(x).append(",").append(y).append(",").append(z).append(",").append(rx).append(",").append(ry).append(",").append(rz).append(",").append(tx()).append(",").append(ty()).append(",");
  b.append(x1).append(",").append(y1).append(",").append(z1).append(",").append(x2).append(",").append(y2).append(",").append(z2);
  lines.add(b.toString());
  }

@Override
public void readFromLine(String[] lineBits)
  {
//  String parent = lineBits[0];
  x = StringTools.safeParseFloat(lineBits[1]);
  y = StringTools.safeParseFloat(lineBits[2]);
  z = StringTools.safeParseFloat(lineBits[3]);
  rx = StringTools.safeParseFloat(lineBits[4]);
  ry = StringTools.safeParseFloat(lineBits[5]);
  rz = StringTools.safeParseFloat(lineBits[6]);
  setTx(StringTools.safeParseFloat(lineBits[7]));
  setTy(StringTools.safeParseFloat(lineBits[8]));
  x1 = StringTools.safeParseFloat(lineBits[9]);
  y1 = StringTools.safeParseFloat(lineBits[10]);
  z1 = StringTools.safeParseFloat(lineBits[11]);
  x2 = StringTools.safeParseFloat(lineBits[12]);
  y2 = StringTools.safeParseFloat(lineBits[13]);
  z2 = StringTools.safeParseFloat(lineBits[14]);
  }

@Override
public void addUVMapToImage(BufferedImage image)
  {
  int u = (int) tx();
  int v = (int) ty();
  int w = (int) (x2-x1)*16;
  int h = (int) (y2-y1)*16;
  int l = (int) (z2-z1)*16;
  int x, y;
  /**
   * front face
   */
  for(x = u + l; x < u + l + w; x++)
    {
    for(y = v + l; y< v+ l + h; y++)
      {
      image.setRGB(x, y, 0xffff0000);
      }
    }
  //left face
  for(x = u ; x< u + l; x++)
    {
    for(y = v+l; y< v+l+h; y++)
      {
      image.setRGB(x, y, 0xff00aa00);
      }
    }
  //right face
  for(x = u + l + w ; x< u + l + w + l; x++)
    {
    for(y = v+l; y< v+l+h; y++)
      {
      image.setRGB(x, y, 0xff00ff00);
      }
    }
  //rear face
  for(x = u + l + w + l ; x< u + l + w + l + w; x++)
    {
    for(y = v+l; y< v+l+h; y++)
      {
      image.setRGB(x, y, 0xffaa0000);
      }
    }
  //top face
  for(x = u + l; x< u +l +w; x++)
    {
    for(y = v; y< v+l; y++)
      {
      image.setRGB(x, y, 0xff0000ff);
      }
    }
  //bottom face
  for(x = u + l + w; x < u + l + w + w; x++)
    {
    for(y = v; y< v+l; y++)
      {
      image.setRGB(x, y, 0xff0000aa);
      }
    }
  }

}
