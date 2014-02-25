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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import shadowmage.ancient_framework.common.config.AWLog;
import shadowmage.ancient_framework.common.utils.StringTools;

/**
 * A single piece of a model.  A piece is a discrete static component of the model.  Pieces may be rotated and moved
 * relative to other pieces in the model (in contrast to boxes, which may not be altered).  All animation is done
 * by moving pieces relative to each-other and the model origin.
 * 
 * Each piece has a box list for the boxes of that piece, as well as a children list for sub-pieces
 *  (pieces which rotate/move relative to this piece, but may also need to rotate independently)
 * Each piece may have multiple boxes and multiple sub-pieces.
 * @author Shadowmage
 *
 */
public class ModelPiece
{

private String pieceName;
private boolean visible = true;
private float x, y, z;//manipulatable coordinates for this piece, relative to either model origin or parent-piece origin (if base piece or has parent)
private float rx, ry, rz;//manipulatable rotation for this piece, relative to either model rotation or parent-piece rotation (if base piece or has parent)
private Set<ModelPiece> children = new HashSet<ModelPiece>();//the children of this piece
private List<Primitive> primitives = new ArrayList<Primitive>();//the list of boxes that make up this piece, really only used during first construction of display list
private ModelBaseAW model;
private ModelPiece parent;

public ModelPiece(ModelBaseAW model, String line)
  {  
  String[] bits = line.split(",");
  String pieceName = bits[0];
  String parentName = bits[1];
  float x = StringTools.safeParseFloat(bits[2]);
  float y = StringTools.safeParseFloat(bits[3]);
  float z = StringTools.safeParseFloat(bits[4]);
  float rx = StringTools.safeParseFloat(bits[5]);
  float ry = StringTools.safeParseFloat(bits[6]);
  float rz = StringTools.safeParseFloat(bits[7]);
  ModelPiece parent = parentName.equals("null")? null : model.getPiece(parentName);
  this.pieceName = pieceName;
  this.model = model;
  this.setPosition(x, y, z);
  this.setRotation(rx, ry, rz);
  if(parent!=null)
    {
    parent.addChild(this);
    }
  }

public ModelPiece(ModelBaseAW model, String name, float x, float y, float z, float rx, float ry, float rz, ModelPiece parent)
  {
  this.pieceName = name;
  this.model = model;
  this.setPosition(x, y, z);
  this.setRotation(rx, ry, rz);  
  if(parent!=null)
    {
    parent.addChild(this);
    }
  }

public ModelPiece copy()
  {
  ModelPiece piece = new ModelPiece(model, pieceName, x, y, z, rx, ry, rz, parent);
  for(Primitive primitive : this.primitives)
    {
    piece.addPrimitive(primitive.copy());
    }  
  return piece;
  }

public ModelPiece getParent(){return parent;}
public boolean isBasePiece(){return getParent()==null;}
public float x(){return x;}
public float y(){return y;}
public float z(){return z;}
public float rx(){return rx;}
public float ry(){return ry;}
public float rz(){return rz;}
public String getName(){return pieceName;}
public Collection<ModelPiece> getChildren(){return children;}

public void setRotation(float rx, float ry, float rz)
  {
  this.rx = rx;
  this.ry = ry;
  this.rz = rz;
  }

public void setPosition(float x, float y, float z)
  {
  this.x = x;
  this.y = y;
  this.z = z;
  }

public void setName(String name)
  {
  this.pieceName = name;
  }

public void addPrimitive(Primitive primitive)
  {
  this.model.addPrimitive(primitive);
  this.primitives.add(primitive);  
  primitive.parent = this;
  }

public void removePrimitive(Primitive primitive)
  {
  this.model.removePrimitive(primitive);
  this.primitives.remove(primitive);
  primitive.parent = null;
  }

public void addChild(ModelPiece piece)
  {
  this.children.add(piece);
  if(piece.parent!=null)
    {
    piece.parent.removeChild(piece);
    }
  piece.parent = this;
  }

public void removeChild(ModelPiece piece)
  {
  this.children.remove(piece);
  piece.parent = null;
  }

public void render()
  {
  if(!visible)
    {
    return;
    }
  GL11.glPushMatrix();
  if(x!=0 || y!=0 || z!=0){GL11.glTranslatef(x, y, z);}  
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);}  
  for(Primitive primitive : this.primitives)
    {  
    primitive.render();   
    }  
  for(ModelPiece child : this.children)
    {
    child.render();
    }
  GL11.glPopMatrix();
  }

public void renderForEditor(ModelPiece piece, Primitive prim)
  {
  GL11.glPushMatrix();
  if(x!=0 || y!=0 || z!=0){GL11.glTranslatef(x, y, z);}  
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);} 
  if(piece==this)
    {
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_TEXTURE_2D);
    GL11.glEnable(GL11.GL_POINT_SMOOTH);
    GL11.glColor4f(1.0f, 0.f, 0.f, 1.f);
    GL11.glPointSize(5.f);
    GL11.glBegin(GL11.GL_POINTS);    
    GL11.glVertex3f(0, 0, 0);
    GL11.glEnd();
    GL11.glColor4f(0.75f, 0.5f, 0.5f, 1.f);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
  for(Primitive primitive : this.primitives)
    {  
    if(primitive==prim)
      {
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_TEXTURE_2D);

      GL11.glColor4f(1.0f, 0.f, 0.f, 1.f);
      
      
      GL11.glBegin(GL11.GL_POINTS);    
      GL11.glVertex3f(prim.x, prim.y, prim.z);
      GL11.glEnd();
      
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_TEXTURE_2D);    

      GL11.glColor4f(1.0f, 0.5f, 0.5f, 1.f);
      }
    primitive.render();
    if(primitive==prim)
      {
      GL11.glColor4f(0.75f, 0.5f, 0.5f, 1.f);
      }
    }  
  for(ModelPiece child : this.children)
    {
    child.renderForEditor(piece, prim);
    }
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  GL11.glPopMatrix();
  }

public void renderForSelection()
  {
  GL11.glPushMatrix();
  if(x!=0 || y!=0 || z!=0)
    {
    GL11.glTranslatef(x, y, z);
    }  
  if(rx!=0){GL11.glRotatef(rx, 1, 0, 0);}
  if(ry!=0){GL11.glRotatef(ry, 0, 1, 0);}
  if(rz!=0){GL11.glRotatef(rz, 0, 0, 1);}  
 
  for(Primitive primitive : this.primitives)
    {
    byte r, g, b;
    r = (byte)((primitive.primitiveNumber >> 16) & 0xff);
    g = (byte)((primitive.primitiveNumber >> 8 ) & 0xff);
    b = (byte)((primitive.primitiveNumber >> 0 ) & 0xff);
    AWLog.logDebug("rendering primitive by color: "+r+","+g+","+b);
    GL11.glColor3b(r, g, b); 
    AWLog.logDebug("rendering primitive for selection as number: "+primitive.primitiveNumber);
    GL11.glPushMatrix();  
    primitive.render();
    GL11.glPopMatrix();
    }
       
  for(ModelPiece child : this.children)
    {
    child.renderForSelection();
    }
  GL11.glPopMatrix();
  }

protected void getPieces(List<ModelPiece> input)
  {
  input.add(this);
  for(ModelPiece piece : this.children)
    {
    piece.getPieces(input);
    }
  }

public List<Primitive> getPrimitives()
  {
  return this.primitives;
  }

public void addPieceLines(ArrayList<String> lines)
  {
  StringBuilder b = new StringBuilder("part=");
  b.append(this.pieceName).append(",");
  b.append(this.parent==null? "null" : this.parent.getName()).append(",");
  b.append(x).append(",").append(y).append(",").append(z).append(",").append(rx).append(",").append(ry).append(",").append(rz);
  lines.add(b.toString());
  for(Primitive p : this.primitives)
    {
    p.addPrimitiveLines(lines);
    }
  for(ModelPiece p : this.children)
    {
    p.addPieceLines(lines);
    }
  }

protected ModelBaseAW getModel()
  {
  return this.model;
  }

}
