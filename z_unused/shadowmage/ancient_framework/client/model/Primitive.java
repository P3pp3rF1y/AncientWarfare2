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

public abstract class Primitive
{

private float tx;//texture offsets, in texture space (0->1)
private float ty;
float x, y, z;//origin of this primitive, relative to parent origin and orientation

float rx, ry, rz;//rotation of this primitive, relative to parent orientation

int primitiveNumber = 0;

private boolean isCompiled = false;
int displayListNumber = -1;

public ModelPiece parent;

public Primitive(ModelPiece parent)
  {
  this.parent = parent;
  }

public void render()
  {
  if(!isCompiled)
    {   
    buildDisplayList();
    }
  GL11.glCallList(displayListNumber);
  }

public void buildDisplayList()
  {
  if(this.displayListNumber<0)
    {
    this.displayListNumber = GL11.glGenLists(1);
    }  
  GL11.glNewList(displayListNumber, GL11.GL_COMPILE);
  if(x!=0 || y!=0 || z!=0){GL11.glTranslatef(x, y, z);}
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);}  
  renderForDisplayList();
  if(x!=0 || y!=0 || z!=0){GL11.glTranslatef(-x, -y, -z);}
  if(rx!=0){GL11.glRotatef(-rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(-ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(-rz, 0, 0, 1);}  
  GL11.glEndList();
  setCompiled(true);
  }

protected abstract void renderForDisplayList();

public abstract Primitive copy();

public float x(){return x;}
public float y(){return y;}
public float z(){return z;}
public float rx(){return rx;}
public float ry(){return ry;}
public float rz(){return rz;}
public float tx(){return tx;}
public float ty(){return ty;}

public void setOrigin(float x, float y, float z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  this.setCompiled(false);
  }

public void setRotation(float rx, float ry, float rz)
  {
  this.rx = rx;
  this.ry = ry;
  this.rz = rz;
  this.setCompiled(false);
  }

public void setTx(float tx)
  {
  if(tx<0){tx = 0;}
  this.tx = tx;
  }

public void setTy(float ty)
  {
  if(ty<0){ty = 0;}
  this.ty = ty;
  }

public abstract void addPrimitiveLines(ArrayList<String> lines);

public abstract void readFromLine(String[] lineBits);

public void setCompiled(boolean isCompiled)
  {
  this.isCompiled = isCompiled;
  }

public abstract void addUVMapToImage(BufferedImage image);


}
