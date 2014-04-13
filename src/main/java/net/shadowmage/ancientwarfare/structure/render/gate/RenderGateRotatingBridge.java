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
package net.shadowmage.ancientwarfare.structure.render.gate;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.model.ModelGateBridge;

import org.lwjgl.opengl.GL11;

public class RenderGateRotatingBridge extends Render
{

ModelGateBridge model = new ModelGateBridge();

public RenderGateRotatingBridge()
  {
  
  }

@Override
public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
  {
  GL11.glPushMatrix();  
  EntityGate g = (EntityGate) entity;
  BlockPosition min = g.pos1;
  BlockPosition max = g.pos2;
   
  boolean wideOnXAxis = min.x!=max.x;
  
  float rx = wideOnXAxis ? g.edgePosition + g.openingSpeed * (1-f1) : 0;
  float rz = wideOnXAxis ? 0 : g.edgePosition + g.openingSpeed * (1-f1);
  boolean invert = g.gateOrientation== 0 || g.gateOrientation==3;
  if(invert)
    {
    rx *=-1;
    rz *=-1;
    }
//  GL11.glRotatef(90, 0, 1, 0);
  GL11.glTranslatef(0, -0.5f, 0);
  GL11.glRotatef(rx, 1, 0, 0);
  GL11.glRotatef(rz, 0, 0, 1);
  float width = wideOnXAxis ? max.x-min.x+1 : max.z-min.z + 1;
  float height = max.y - min.y + 1;
  float xOffset = wideOnXAxis ? width*0.5f-0.5f : 0f;
  float zOffset = wideOnXAxis ? 0f : -width*0.5f+0.5f;
  
  float tx = wideOnXAxis ? 1 : 0;
  float ty = -1;
  float tz = wideOnXAxis ? 0 : 1;    
  float axisRotation = wideOnXAxis ? 180 : 90;
  if(invert)
    {
    GL11.glRotatef(180, 0, 1, 0);
    }
  GL11.glTranslatef(-xOffset, 0, zOffset);  
  for(int y = 0; y<height; y++)
    {    
    GL11.glPushMatrix();
    for(int x = 0; x <width; x++)
      {
      model.setModelRotation(axisRotation);
      if(y==0)
        {
        model.renderGateBlock();
        }
      if(y == height-1 && x>0 && x<width-1)
        {
        model.renderTop();
        }
      else if(y==height-1 && x==0)
        {
        model.renderCorner();
        }
      else if(y==height-1 && x==width-1)
        {
        model.renderCorner2();
        }
      else if(x==0 && y>0)
        {
        model.renderSide1();
        }
      else if(x==width-1 && y > 0)
        {
        model.renderSide2();
        }
      if(y>0)
        {
        GL11.glPushMatrix();
        model.setModelRotation(axisRotation);
        model.renderSolidWall();    
        GL11.glPopMatrix();        
        }      
      GL11.glTranslatef(tx, 0, tz);
      }
    GL11.glPopMatrix();
    GL11.glTranslatef(0, ty, 0);
    }
  GL11.glPopMatrix();
  }

@Override
protected ResourceLocation getEntityTexture(Entity entity)
  {
  return ((EntityGate)entity).getGateType().getTexture();
  }
}
