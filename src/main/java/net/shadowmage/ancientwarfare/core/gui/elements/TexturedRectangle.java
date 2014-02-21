package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.config.Statics;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class TexturedRectangle extends GuiElement
{

ResourceLocation texture;
int tx, ty, u, v, uw, vh;
float u1, v1, u2, v2;

public TexturedRectangle(int topLeftX, int topLeftY, int width, int height, String texture, int tx, int ty, int u, int v, int uw, int vh)
  {
  super(topLeftX, topLeftY, width, height);
  this.tx = tx;
  this.ty = ty;
  this.u = u;
  this.v = v;
  this.uw = uw;
  this.vh = vh;
  
  float perX = 1.f / ((float)tx);
  float perY = 1.f / ((float)ty);  
  u1 = ((float) u) * perX;
  v1 = ((float) v) * perY;
  u2 = (float)(u + uw) * perX;
  v2 = (float)(v + vh) * perY;
  this.texture = new ResourceLocation(Statics.coreModID, "textures/"+texture);
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible)
    {
    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    RenderTools.renderTexturedQuad(renderX, renderY, renderX+width, renderY+height, u1, v1, u2, v2);    
    }
  }

}
