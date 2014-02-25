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
import java.util.List;

import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.StringTools;
import shadowmage.ancient_framework.common.utils.Trig;

public class PrimitiveTriangle extends Primitive
{

float x1, y1, z1, x2, y2, z2, x3, y3, z3;
float u1, v1, u2, v2, u3, v3;//texture uv's...saved to file, as I have no idea how to dynamically calc a triangle uv map
float normalX, normalY, normalZ;//normal for lighting...should be calc'd when setBounds is called
float cx, cy;//triangle center point -- used for rotation of uv vertices around triangle center
/**
 * @param parent
 */
public PrimitiveTriangle(ModelPiece parent)
  {
  super(parent);
  }

public float x1(){return x1;}
public float y1(){return y1;}
public float z1(){return z1;}
public float x2(){return x2;}
public float y2(){return y2;}
public float z2(){return z2;}
public float x3(){return x3;}
public float y3(){return y3;}
public float z3(){return z3;}
public float u1(){return u1;}
public float u2(){return u2;}
public float u3(){return u3;}
public float v1(){return v1;}
public float v2(){return v2;}
public float v3(){return v3;}
public float cx(){return cx;}
public float cy(){return cy;}

@Override
protected void renderForDisplayList()
  {   
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);}  
  
  float tw = parent.getModel().textureWidth;
  float th = parent.getModel().textureHeight;
  float px = 1.f/tw;
  float py = 1.f/th;
  
  float u1, v1, u2, v2, u3, v3;
  u1 = this.u1 * px + this.tx() * px;
  u2 = this.u2 * px + this.tx() * px;
  u3 = this.u3 * px + this.tx() * px;
  v1 = this.v1 * py + this.ty() * py;
  v2 = this.v2 * py + this.ty() * py;
  v3 = this.v3 * py + this.ty() * py;
  GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
  GL11.glNormal3f(normalX, normalY, normalZ);
  GL11.glTexCoord2f(u1, v1);
  GL11.glVertex3f(x1, y1, z1);
  GL11.glTexCoord2f(u2, v2);
  GL11.glVertex3f(x2, y2, z2);
  GL11.glTexCoord2f(u3, v3);
  GL11.glVertex3f(x3, y3, z3);  
  GL11.glEnd();
  }

public void reverseVertexOrder()
  {
  float x = x2;
  float y = y2;
  float z = z2;
  x2 = x3;
  y2 = y3;
  z2 = z3;
  x3 = x;
  y3 = y;
  z3 = z;
  this.setBounds(x1, y1, z1, x2, y2, z1, x3, y3, z3);
  }

public void rotateTriangleUV(float degrees)
  {
  /**
   * grab triangle center(calc'd when UV is set) 
   * find angle and length for each corner
   * rotate by degrees, recompute new angle, set new corner point at new angle * length
   */
  float u1, v1, u2, v2, u3, v3;
  float dx = u1() - cx();
  float dy = v1() - cy();
  float length = MathHelper.sqrt_float(dx*dx+dy*dy);
  double radianAngle = Math.atan2(dx, dy);
  radianAngle += degrees * Trig.TORADIANS;  
  u1 = (float) Math.sin(radianAngle) * length;
  v1 = (float) Math.cos(radianAngle) * length;
  
  dx = u2() -cx();
  dy = v2() -cy();
  length = MathHelper.sqrt_float(dx*dx+dy*dy);
  radianAngle = Math.atan2(dx, dy);
  radianAngle += degrees * Trig.TORADIANS;  
  u2 = (float) Math.sin(radianAngle) * length;
  v2 = (float) Math.cos(radianAngle) * length;
  
  dx = u3() - cx();
  dy = v3() - cy();
  length = MathHelper.sqrt_float(dx*dx+dy*dy);
  radianAngle = Math.atan2(dx, dy);
  radianAngle += degrees * Trig.TORADIANS;  
  u3 = (float) Math.sin(radianAngle) * length;
  v3 = (float) Math.cos(radianAngle) * length;
  

  AWLog.logDebug(String.format("orig: %.2f, %.2f, %.2f, %.2f, %.2f, %.2f  new: %.2f, %.2f, %.2f, %.2f, %.2f, %.2f", u1(), v1(), u2(), v2(), u3(), v3(), u1, v1, u2, v2, u3, v3));
  
  setUV(u1, v1, u2, v2, u3, v3);
  setCompiled(false);
  }

@Override
public Primitive copy()
  {
  PrimitiveTriangle box = new PrimitiveTriangle(parent);
  box.setBounds(x1, y1, z1, x2, y2, z2, x3, y3, z3);
  box.setOrigin(x, y, z);
  box.setRotation(rx, ry, rz);
  box.setTx(tx());
  box.setTy(ty());
  box.setUV(u1, v1, u2, v2, u3, v3);
  return box;
  }

@Override
public void addPrimitiveLines(ArrayList<String> lines)
  {
  StringBuilder b = new StringBuilder("triangle=").append(parent.getName()).append(",");
  b.append(x).append(",").append(y).append(",").append(z).append(",").append(rx).append(",").append(ry).append(",").append(rz).append(",").append(tx()).append(",").append(ty()).append(",");
  b.append(x1).append(",").append(y1).append(",").append(z1).append(",").append(x2).append(",").append(y2).append(",").append(z2).append(",").append(x3).append(",").append(y3).append(",").append(z3).append(",");
  b.append(u1).append(",").append(v1).append(",").append(u2).append(",").append(v2).append(",").append(u3).append(",").append(v3);
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
  x3 = StringTools.safeParseFloat(lineBits[15]);
  y3 = StringTools.safeParseFloat(lineBits[16]);
  z3 = StringTools.safeParseFloat(lineBits[17]);
  u1 = StringTools.safeParseFloat(lineBits[18]);
  v1 = StringTools.safeParseFloat(lineBits[19]);
  u2 = StringTools.safeParseFloat(lineBits[20]);
  v2 = StringTools.safeParseFloat(lineBits[21]);
  u3 = StringTools.safeParseFloat(lineBits[22]);
  v3 = StringTools.safeParseFloat(lineBits[23]);    
  calcNormal();   
  calcCenter();
  }

public void setBounds(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3)
  {
  this.x1 = x1;
  this.x2 = x2;
  this.x3 = x3;
  this.y1 = y1;
  this.y2 = y2;
  this.y3 = y3;
  this.z1 = z1;
  this.z2 = z2;
  this.z3 = z3;
  this.calcNormal();
  this.calcCenter(); 
  this.setCompiled(false);
  }

public void setUV(float u1, float v1, float u2, float v2, float u3, float v3)
  {
  this.u1 = u1;
  this.v1 = v1;
  this.u2 = u2;
  this.v2 = v2;
  this.u3 = u3;
  this.v3 = v3;    
  calcCenter();
  this.setCompiled(false);
  }

private void calcCenter()
  {
  cx = (u1+u2+u3)/3;
  cy = (v1+v2+v3)/3;
  }

/**
 * reclac the normal for this triangle.  should be called whenever the vertex coordinates change
 */
private void calcNormal()
  {
  float vx, vy, vz, wx, wy, wz;
  vx = x2-x1;
  vy = y2-y1;
  vz = z2-z1;
  wx = x3-x1;
  wy = y3-y1;
  wz = z3-z1;
  
  normalX = (vy*wz)-(vz*wy);
  normalY = (vz*wx)-(vx*wz);
  normalZ = (vx*wy)-(vy*wx);
  
  float norm = MathHelper.sqrt_float(normalX * normalX + normalY * normalY + normalZ * normalZ);
  normalX/=norm;
  normalY/=norm;
  normalZ/=norm;
  this.setCompiled(false);
  }

/**
 * recalc the UV for this triangle based on side-lengths, 
 * with u1/v1 being upper-left on the texture, 
 * u2/v2 being right, 
 * and u3/v3 being bottom
 */
public void recalcUV()
  {  
  //http://www.mathsisfun.com/algebra/trig-solving-sss-triangles.html
  float a = Trig.getDistance(x1, y1, z1, x2, y2, z2) * 16.f;
  float b = Trig.getDistance(x2, y2, z2, x3, y3, z3) * 16.f;
  float c = Trig.getDistance(x3, y3, z3, x1, y1, z1) * 16.f;
  
  float cosA = (b*b + c*c - a*a) / 2*(b*c);
  cosA = (float) Math.pow((float) Math.cos(cosA), -1.f);
  
  float cosB = (c*c + a*a - b*b) / 2*(c*a);
  cosB = (float) Math.pow((float) Math.cos(cosB), -1.f);
  
  float cosC = (180.f - (cosA*Trig.TODEGREES) - (cosB * Trig.TODEGREES))*Trig.TORADIANS;

  u1 = 0;
  v1 = 0;
  
  u2 = u1 + a;
  v2 = 0;
      
  u3 = u1 + (float)(Math.sin(cosC*Trig.TORADIANS) * c);
  v3 = v1 + (float)(Math.cos(cosC*Trig.TORADIANS) * c);
    
  this.calcCenter();
  this.setCompiled(false);
  }

@Override
public void addUVMapToImage(BufferedImage image)
  {  
  float x, y, x1, y1, x2, y2, x3, y3;
  x = tx();
  y = ty();
    
  x1 = x + u1;
  y1 = y + v1;
  x2 = x + u2;
  y2 = y + v2;
  x3 = x + u3;
  y3 = y + v3;
  if(x1<0){x1 = 0;}
  if(x2<0){x2 = 0;}
  if(x3<0){x3 = 0;}
  if(y1<0){y1 = 0;}
  if(y2<0){y2 = 0;}
  if(y3<0){y3 = 0;}
  
  List<Point2i> points = new ArrayList<Point2i>();
  
  
  plotLine3((int)x1, (int)y1, (int)x2, (int)y2, points);
  for(Point2i point : points)
    {
    image.setRGB(point.x, point.y, 0xffff0000);
    }
  points.clear();
  
  plotLine3((int)x2, (int)y2, (int)x3, (int)y3, points);
  for(Point2i point : points)
    {
    image.setRGB(point.x, point.y, 0xffff0000);
    }
  points.clear();
  
  plotLine3((int)x3, (int)y3, (int)x1, (int)y1, points);
  for(Point2i point : points)
    {
    image.setRGB(point.x, point.y, 0xffff0000);
    }
  points.clear();
  
  image.setRGB((int)(tx()+cx()), (int)(ty()+cy()), 0xffff0000);
  }


//http://en.wikipedia.org/wiki/Bresenham%27s_line_algorithm
//http://www.mathworks.com/matlabcentral/fileexchange/21057-3d-bresenhams-line-generation/content/bresenham_line3d.m
//http://www.luberth.com/plotter/line3d.c.txt.html
//http://www.ict.griffith.edu.au/anthony/info/graphics/bresenham.procs
public static void plotLine3(int x1, int y1, int x2, int y2, List<Point2i> points)
  {
  int dx, dy, x, y, sx, sy;
  int err;
  int e2;
  dx = Math.abs(x2-x1);
  dy = Math.abs(y2-y1);
  if(x1<x2){sx = 1;}
  else{sx = -1;}
  if(y1<y2){sy = 1;}
  else{sy = -1;}
  err = dx-dy;
  x = x1;
  y = y1;
  while(true)
    {
    points.add(new Point2i(x,y));
    if(x==x2 && y==y2){break;}
    e2 = 2*err;
    if(e2 > -dy)
      {
      err = err - dy;
      x += sx;
      }
    if(x==x2&&y==y2)
      {
      points.add(new Point2i(x,y));
      break;
      }   
    if(e2 < dx)
      {
      err = err + dx;
      y = y + sy;
      }
    }
  }


public void setCenterx(float cx, float cy)
  {
  this.cx = cx;
  this.cy = cy;
  }

public static class Point2i
{
public int x;
public int y;
public Point2i(int x, int y)
  {
  this.x = x;
  this.y = y;
  }
}


}
