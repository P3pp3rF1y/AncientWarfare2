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
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.model.ModelGateBasic;

import org.lwjgl.opengl.GL11;

public class RenderGateDouble extends Render
{

ModelGateBasic model = new ModelGateBasic();

public RenderGateDouble()
  {
  
  }

@Override
public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1)
  {
  GL11.glPushMatrix();  
  EntityGate g = (EntityGate) entity;
  BlockPosition min = BlockTools.getMin(g.pos1, g.pos2);//g.pos1;
  BlockPosition max = BlockTools.getMax(g.pos1, g.pos2);
   
  boolean wideOnXAxis = min.x!=max.x;
  float width = wideOnXAxis ? max.x-min.x+1 : max.z-min.z + 1;
  float height = max.y - min.y + 1;
  float xOffset = wideOnXAxis ? width*0.5f-0.5f : 0f;
  float zOffset = wideOnXAxis ? 0f : -width*0.5f+0.5f;
  
  float tx = wideOnXAxis ? 1 : 0;
  float ty = -1;
  float tz = wideOnXAxis ? 0 : 1;    
  float axisRotation = wideOnXAxis ? 90 : 0;
  GL11.glTranslatef(-xOffset, 0, zOffset);  
  for(int y = 0; y<height; y++)
    {    
    GL11.glPushMatrix();
    for(int x = 0; x <width; x++)
      {
      model.setModelRotation(axisRotation);
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
        model.setModelRotation(axisRotation+180);
        model.renderCorner();
        }
      else if(x==0)
        {
        model.renderSide();
        }
      else if(x==width-1)
        {
        model.setModelRotation(axisRotation+180);
        model.renderSide();
        }
      float move = 0.f;
      boolean render = false;
      if(x < width * 0.5f)
        {
        move = -g.edgePosition - g.openingSpeed * (1-f1);
        if( x + move > -0.5f)
          {
          render = true;
          }
        }
      else
        {
        move = g.edgePosition + g.openingSpeed * (1-f1);
        if(x+move <= width-0.475f)
          {
          render = true;
          }
        }      
      float wallTx = wideOnXAxis ? move : 0;
      float wallTz = wideOnXAxis ? 0 : move;
      if(render)
        {      
        GL11.glPushMatrix();
        GL11.glTranslatef(wallTx, 0, wallTz);
        model.setModelRotation(axisRotation);
        if(g.getGateType().getModelType()==0)
          {
          model.renderSolidWall();          
          }
        else
          {
          model.renderBars();
          }
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
