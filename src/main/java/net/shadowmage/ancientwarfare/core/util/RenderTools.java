package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

public class RenderTools
{


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

}
