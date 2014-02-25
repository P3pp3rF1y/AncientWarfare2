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
package shadowmage.ancient_framework.client.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;

public class RenderTools
{

static float zLevel = 0;


/**
 * draw a player-position-normalized bounding box (can only be called during worldRender)
 * @param bb
 */
public static void drawOutlinedBoundingBox(AxisAlignedBB bb, float r, float g, float b)
  {
  GL11.glEnable(GL11.GL_BLEND);
  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
  GL11.glColor4f(r, g, b, 0.4F);
  GL11.glLineWidth(8.0F);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDepthMask(false);
  Tessellator tess = Tessellator.instance;
  tess.startDrawing(3);
  tess.addVertex(bb.minX, bb.minY, bb.minZ);
  tess.addVertex(bb.maxX, bb.minY, bb.minZ);
  tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
  tess.addVertex(bb.minX, bb.minY, bb.maxZ);
  tess.addVertex(bb.minX, bb.minY, bb.minZ);
  tess.draw();
  tess.startDrawing(3);
  tess.addVertex(bb.minX, bb.maxY, bb.minZ);
  tess.addVertex(bb.maxX, bb.maxY, bb.minZ);
  tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);
  tess.addVertex(bb.minX, bb.maxY, bb.maxZ);
  tess.addVertex(bb.minX, bb.maxY, bb.minZ);
  tess.draw();
  tess.startDrawing(1);
  tess.addVertex(bb.minX, bb.minY, bb.minZ);
  tess.addVertex(bb.minX, bb.maxY, bb.minZ);
  tess.addVertex(bb.maxX, bb.minY, bb.minZ);
  tess.addVertex(bb.maxX, bb.maxY, bb.minZ);
  tess.addVertex(bb.maxX, bb.minY, bb.maxZ);
  tess.addVertex(bb.maxX, bb.maxY, bb.maxZ);
  tess.addVertex(bb.minX, bb.minY, bb.maxZ);
  tess.addVertex(bb.minX, bb.maxY, bb.maxZ);
  tess.draw();
  GL11.glDepthMask(true);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_BLEND);
  }

/**
 * @param bb
 * @param player
 * @param partialTick
 * @return
 */
public static AxisAlignedBB adjustBBForPlayerPos(AxisAlignedBB bb, EntityPlayer player, float partialTick)
  {
  double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
  double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
  double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;  
  return bb.offset(-x, -y, -z);
  }

/**
 * renders the given icon texture, of given size, at the current render coordinates
 * (pre-translate to position before rendering)
 * @param tex
 * @param width
 * @param height
 */
public static void renderIcon(String tex, int width, int height, int x, int y)
  {
  Tessellator tess = Tessellator.instance;
  AWTextureManager.bindTexture(tex);
  int halfW = width/2;
  int halfH = height/2;
  tess.startDrawingQuads();
  tess.addVertexWithUV(x-halfW, y-halfH, 0, 0, 0); 
  tess.addVertexWithUV(x-halfW, y+halfH, 0, 0, 1);
  tess.addVertexWithUV(x+halfW, y+halfH, 0, 1, 1);
  tess.addVertexWithUV(x+halfW, y+-halfH, 0, 1, 0);
  tess.draw();
  }

/**
 * renders the four corners of a texture, from the corner inward (e.g. for size-adaptable elements)
 * origin tex MUST be  256x256
 * @param x renderPosX
 * @param y renderPosY
 * @param w renderWidth
 * @param h renderHeight
 * @param tw textureUsedWidth
 * @param th textureUsedHeight
 * @param tex theTexture
 * @param u textureStartX
 * @param v textureStartY
 */
public static void drawQuadedTexture(int x, int y, int w, int h, int tw, int th, String tex, int u, int v)
  {  
  int halfW = w/2;
  int halfH = h/2;  
  int u1 = u + tw - halfW;
  int v1 = v + th - halfH;
  AWTextureManager.bindTexture(tex);
  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
  drawTexturedModalRect(x, y, u, v, halfW, halfH);
  drawTexturedModalRect(x + halfW, y, u1, v, halfW, halfH);
  drawTexturedModalRect(x, y + halfH, u, v1, halfW, halfH);
  drawTexturedModalRect(x + halfW, y + halfH, u1, v1, halfW, halfH);
  }

public static void drawTexturedModalRect(int par1, int par2, int par3, int par4, int par5, int par6)
  {
  float f = 0.00390625F;
  float f1 = 0.00390625F;
  Tessellator tessellator = Tessellator.instance;
  tessellator.startDrawingQuads();
  tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + par6) * f1));
  tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + par6), (double)zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + par6) * f1));
  tessellator.addVertexWithUV((double)(par1 + par5), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + par5) * f), (double)((float)(par4 + 0) * f1));
  tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)((float)(par3 + 0) * f), (double)((float)(par4 + 0) * f1));
  tessellator.draw();
  }

public static void drawTexturedModelRectFromIcon(int par1, int par2, Icon par3Icon, int par4, int par5)
  {
  Tessellator tessellator = Tessellator.instance;
  tessellator.startDrawingQuads();
  tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + par5), (double)zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMaxV());
  tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + par5), (double)zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMaxV());
  tessellator.addVertexWithUV((double)(par1 + par4), (double)(par2 + 0), (double)zLevel, (double)par3Icon.getMaxU(), (double)par3Icon.getMinV());
  tessellator.addVertexWithUV((double)(par1 + 0), (double)(par2 + 0), (double)zLevel, (double)par3Icon.getMinU(), (double)par3Icon.getMinV());
  tessellator.draw();
  }

public static List<String> getFormattedLines(List<String> inLines, int maxWidth)
  {
  FontRenderer render = Minecraft.getMinecraft().fontRenderer;
  List<String> outLines = new ArrayList<String>();
  Iterator<String> inIt = inLines.iterator();
  
  String line;
  String newLine = "";
  int currentLineWidth = 0;
  int absLineWidth = 0;
  int charWidth = 0;
  while(inIt.hasNext())
    {
    line = inIt.next();
    absLineWidth = render.getStringWidth(line);
    currentLineWidth = 0;    
    if(absLineWidth<maxWidth)
      {
      outLines.add(line);
      continue;
      }
    newLine = "";
    for(int i = 0; i < line.length(); i++)
      {      
      char ch = line.charAt(i);
      charWidth = render.getCharWidth(ch);
      if(currentLineWidth + charWidth <=maxWidth)
        {
        newLine = newLine + ch;
        currentLineWidth += charWidth;
        }
      else
        {
        if(ch!=' ' && newLine.charAt(newLine.length()-1)!=' ')
          {
          newLine += "-";
          }
        outLines.add(newLine);
        currentLineWidth = charWidth;
        if(ch!=' ')
          {
          newLine = String.valueOf(ch);
          }
        else
          {
          newLine = "";
          }
        }
      }
    if(!newLine.equals(""))
      {
      outLines.add(newLine);
      }
    }
  return outLines;
  }
}
