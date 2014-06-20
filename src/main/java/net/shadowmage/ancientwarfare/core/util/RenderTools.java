package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

public class RenderTools
{


public static void setFullColorLightmap()
  {
  OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0.f, 240.f);  
  }

/**
 * @param textureWidth texture width
 * @param textureHeight texture height
 * @param texStartX pixel start U
 * @param texStartY pixel start V
 * @param texUsedWidth pixel U width (width of used tex in pixels)
 * @param texUsedHeight pixel V height (height of used tex in pixels)
 * @param renderStartX render position x
 * @param renderStartY render position y
 * @param renderHeight render height
 * @param renderWidth render width
 */
public static void renderQuarteredTexture(int textureWidth, int textureHeight, int texStartX, int texStartY, int texUsedWidth, int texUsedHeight, int renderStartX, int renderStartY, int renderWidth, int renderHeight)
  {
  //perspective percent x, y
  float perX = 1.f / ((float)textureWidth);
  float perY = 1.f / ((float)textureHeight);  
  float texMinX = ((float) texStartX) * perX;
  float texMinY = ((float) texStartY) * perY;
  float texMaxX = (float)(texStartX + texUsedWidth) * perX;
  float texMaxY = (float)(texStartY + texUsedHeight) * perY;
  float halfWidth = (((float) renderWidth) / 2.f) * perX;
  float halfHeight = (((float) renderHeight) / 2.f) * perY;    
  float halfRenderWidth = ((float)renderWidth) * 0.5f;
  float halfRenderHeight = ((float)renderHeight) * 0.5f;
    
  //draw top-left quadrant
  renderTexturedQuad(renderStartX, renderStartY, renderStartX+halfRenderWidth, renderStartY+halfRenderHeight, texMinX, texMinY, texMinX+halfWidth, texMinY+halfHeight);
  
  //draw top-right quadrant
  renderTexturedQuad(renderStartX+halfRenderWidth, renderStartY, renderStartX+halfRenderWidth*2, renderStartY+halfRenderHeight, texMaxX-halfWidth, texMinY, texMaxX, texMinY+halfHeight);
  
//  draw bottom-left quadrant
  renderTexturedQuad(renderStartX, renderStartY+halfRenderHeight, renderStartX+halfRenderWidth, renderStartY+halfRenderHeight*2, texMinX, texMaxY-halfHeight, texMinX+halfWidth, texMaxY);
 
  //draw bottom-right quadrant
  renderTexturedQuad(renderStartX+halfRenderWidth, renderStartY+halfRenderHeight, renderStartX+halfRenderWidth*2, renderStartY+halfRenderHeight*2, texMaxX-halfWidth, texMaxY-halfHeight, texMaxX, texMaxY);
  }

public static void renderTexturedQuad(float x1, float y1, float x2, float y2, float u1, float v1, float u2, float v2)
  {
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glTexCoord2f(u1, v1);
  GL11.glVertex2f(x1, y1);
  GL11.glTexCoord2f(u1, v2);
  GL11.glVertex2f(x1, y2);
  GL11.glTexCoord2f(u2, v2);
  GL11.glVertex2f(x2, y2);
  GL11.glTexCoord2f(u2, v1);
  GL11.glVertex2f(x2, y1);
  GL11.glEnd();
  }

/**
 * render a BB as a set of enlarged cuboids.
 * @param bb
 * @param r
 * @param g
 * @param b
 * @param width
 */
public static void drawOutlinedBoundingBox2(AxisAlignedBB bb, float r, float g, float b, float width)
  {
  GL11.glEnable(GL11.GL_BLEND);
  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
  GL11.glColor4f(r, g, b, 0.4F);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);  
  float hw = width/2;  
  drawCuboid((float)bb.minX, (float)bb.minY-hw, (float)bb.minZ-hw, (float)bb.maxX, (float)bb.minY+hw, (float)bb.minZ+hw);
  drawCuboid((float)bb.minX, (float)bb.maxY-hw, (float)bb.minZ-hw, (float)bb.maxX, (float)bb.maxY+hw, (float)bb.minZ+hw);  
  drawCuboid((float)bb.minX, (float)bb.minY-hw, (float)bb.maxZ-hw, (float)bb.maxX, (float)bb.minY+hw, (float)bb.maxZ+hw);
  drawCuboid((float)bb.minX, (float)bb.maxY-hw, (float)bb.maxZ-hw, (float)bb.maxX, (float)bb.maxY+hw, (float)bb.maxZ+hw);
  
  drawCuboid((float)bb.minX-hw, (float)bb.minY, (float)bb.minZ-hw, (float)bb.minX+hw, (float)bb.maxY, (float)bb.minZ+hw);
  drawCuboid((float)bb.maxX-hw, (float)bb.minY, (float)bb.minZ-hw, (float)bb.maxX+hw, (float)bb.maxY, (float)bb.minZ+hw);
  drawCuboid((float)bb.minX-hw, (float)bb.minY, (float)bb.maxZ-hw, (float)bb.minX+hw, (float)bb.maxY, (float)bb.maxZ+hw);
  drawCuboid((float)bb.maxX-hw, (float)bb.minY, (float)bb.maxZ-hw, (float)bb.maxX+hw, (float)bb.maxY, (float)bb.maxZ+hw);
  
  drawCuboid((float)bb.minX-hw, (float)bb.minY-hw, (float)bb.minZ, (float)bb.minX+hw, (float)bb.minY+hw, (float)bb.maxZ);
  drawCuboid((float)bb.minX-hw, (float)bb.maxY-hw, (float)bb.minZ, (float)bb.minX+hw, (float)bb.maxY+hw, (float)bb.maxZ);  
  drawCuboid((float)bb.maxX-hw, (float)bb.minY-hw, (float)bb.minZ, (float)bb.maxX+hw, (float)bb.minY+hw, (float)bb.maxZ);
  drawCuboid((float)bb.maxX-hw, (float)bb.maxY-hw, (float)bb.minZ, (float)bb.maxX+hw, (float)bb.maxY+hw, (float)bb.maxZ);
  GL11.glDisable(GL11.GL_BLEND);
  }

public static void drawCuboid(float x, float y, float z, float mx, float my, float mz)
  {
  GL11.glBegin(GL11.GL_QUADS);
  //z+ side
  GL11.glNormal3f(0, 0, 1);
  GL11.glVertex3f(x, my, mz);
  GL11.glVertex3f(x, y, mz);
  GL11.glVertex3f(mx, y, mz);
  GL11.glVertex3f(mx, my, mz);
  
  //x+ side
  GL11.glNormal3f(1, 0, 0);
  GL11.glVertex3f(mx, my, mz);
  GL11.glVertex3f(mx, y, mz);
  GL11.glVertex3f(mx, y, z);
  GL11.glVertex3f(mx, my, z);
  
  //y+ side
  GL11.glNormal3f(0, 1, 0);
  GL11.glVertex3f(x, my, z);
  GL11.glVertex3f(x, my, mz);
  GL11.glVertex3f(mx, my, mz);
  GL11.glVertex3f(mx, my, z);
  
  //z- side
  GL11.glNormal3f(0, 0, -1);
  GL11.glVertex3f(x, my, z);
  GL11.glVertex3f(mx, my, z);
  GL11.glVertex3f(mx, y, z);
  GL11.glVertex3f(x, y, z);
  
  //x-side
  GL11.glNormal3f(-1, 0, 0);
  GL11.glVertex3f(x, y, mz);
  GL11.glVertex3f(x, my, mz);
  GL11.glVertex3f(x, my, z);
  GL11.glVertex3f(x, y, z);
  
  //y- side
  GL11.glNormal3f(0, -1, 0);
  GL11.glVertex3f(x, y, z);
  GL11.glVertex3f(mx, y, z);
  GL11.glVertex3f(mx, y, mz);
  GL11.glVertex3f(x, y, mz);
  
  GL11.glEnd();
  }

/**
 * Renders a white point in center, and RGB lines/points for X,Y,Z axis'
 * @param colorMult
 */
public static void renderOrientationPoints(float colorMult)
  {
  GL11.glPushMatrix();
  GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glPointSize(3.f);
    
  GL11.glColor4f(colorMult,colorMult,colorMult, 1.f);
  
  //draw origin point
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f(0, 0, 0);
  GL11.glEnd();
  

  
  GL11.glColor4f(colorMult,0, 0, 1.f);//red for x axis
  GL11.glBegin(GL11.GL_LINES);
  GL11.glVertex3f(0, 0, 0);
  GL11.glVertex3f(1, 0, 0);
  GL11.glEnd();
  
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f(1, 0, 0);
  GL11.glEnd();
  
  
  GL11.glColor4f(0, colorMult,0, 1.f);//green for y axis
  GL11.glBegin(GL11.GL_LINES);
  GL11.glVertex3f(0, 0, 0);
  GL11.glVertex3f(0, 1, 0);
  GL11.glEnd();
  
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f(0, 1, 0);
  GL11.glEnd();
  
  
  GL11.glColor4f(0,0,colorMult, 1.f);//blue for z axis
  GL11.glBegin(GL11.GL_LINES);
  GL11.glVertex3f(0, 0, 0);
  GL11.glVertex3f(0, 0, 1);
  GL11.glEnd();
  
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f(0, 0, 1);
  GL11.glEnd();
  
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  GL11.glPopAttrib();
  GL11.glPopMatrix();
  }

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
  double x = getRenderOffsetX(player, partialTick);
  double y = getRenderOffsetY(player, partialTick);
  double z = getRenderOffsetZ(player, partialTick);
  return bb.offset(-x, -y, -z);
  }

public static double getRenderOffsetX(EntityPlayer player, float partialTick)
  {
  return player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
  }

public static double getRenderOffsetY(EntityPlayer player, float partialTick)
  {
  return player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
  }

public static double getRenderOffsetZ(EntityPlayer player, float partialTick)
  {
  return player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
  }

}
