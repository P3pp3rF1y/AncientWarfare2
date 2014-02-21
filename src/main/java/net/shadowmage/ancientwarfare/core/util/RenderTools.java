package net.shadowmage.ancientwarfare.core.util;

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
}
