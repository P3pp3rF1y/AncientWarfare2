package net.shadowmage.ancientwarfare.core.gui.elements;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class Tooltip
{

private List<GuiElement> children = new ArrayList<GuiElement>();
int width, height;

/**
 * actual size of the tooltip is width+8, height+8 (it draws some borders around the internals)
 * @param width
 * @param height
 */
public Tooltip(int width, int height)
  {
  this.width = width;
  this.height = height;
  }

public void renderTooltip(int mouseX, int mouseY, float partialTick)  
  { 
  int width = Minecraft.getMinecraft().displayWidth;
  int height = Minecraft.getMinecraft().displayHeight;
  if (mouseX + this.width > width)
    {
    mouseX -= 28 + this.width;
    }
  if (mouseY + this.height + 6 > height)
    {
    mouseY = height - this.height - 6;
    }
  
  pushViewport(mouseX-4, mouseY-4, this.width+8, this.height+8);
  
  for(GuiElement element : this.children)
    {
    element.updateGuiPosition(mouseX, mouseY);
    }
  

  GL11.glDisable(GL11.GL_DEPTH_TEST);
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  drawBackground(mouseX, mouseY);   
  
  RenderHelper.disableStandardItemLighting();
  for(GuiElement element : this.children)
    {
    element.render(-1000, -1000, partialTick);
    }
  disableViewport();
  }

public final void addTooltipElement(GuiElement element)
  {
  this.children.add(element);
  }

private void drawBackground(int mouseX, int mouseY)
  {
  int color1 = -267386864;
  this.drawGradientRect(mouseX - 3, mouseY - 4, mouseX + this.width + 3, mouseY - 3, color1, color1);
  this.drawGradientRect(mouseX - 3, mouseY + this.height + 3, mouseX + this.width + 3, mouseY + this.height + 4, color1, color1);
  this.drawGradientRect(mouseX - 3, mouseY - 3, mouseX + this.width + 3, mouseY + this.height + 3, color1, color1);
  this.drawGradientRect(mouseX - 4, mouseY - 3, mouseX - 3, mouseY + this.height + 3, color1, color1);
  this.drawGradientRect(mouseX + this.width + 3, mouseY - 3, mouseX + this.width + 4, mouseY + this.height + 3, color1, color1);
  int color2 = 1347420415;
  int color3 = (color2 & 16711422) >> 1 | color2 & -16777216;
  this.drawGradientRect(mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + this.height + 3 - 1, color2, color3);
  this.drawGradientRect(mouseX + this.width + 2, mouseY - 3 + 1, mouseX + this.width + 3, mouseY + this.height + 3 - 1, color2, color3);
  this.drawGradientRect(mouseX - 3, mouseY - 3, mouseX + this.width + 3, mouseY - 3 + 1, color2, color2);
  this.drawGradientRect(mouseX - 3, mouseY + this.height + 2, mouseX + this.width + 3, mouseY + this.height + 3, color3, color3);
  }

private void drawGradientRect(int par1, int par2, int par3, int par4, int par5, int par6)
  {
  float f = (float)(par5 >> 24 & 255) / 255.0F;
  float f1 = (float)(par5 >> 16 & 255) / 255.0F;
  float f2 = (float)(par5 >> 8 & 255) / 255.0F;
  float f3 = (float)(par5 & 255) / 255.0F;
  float f4 = (float)(par6 >> 24 & 255) / 255.0F;
  float f5 = (float)(par6 >> 16 & 255) / 255.0F;
  float f6 = (float)(par6 >> 8 & 255) / 255.0F;
  float f7 = (float)(par6 & 255) / 255.0F;
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glEnable(GL11.GL_BLEND);
  GL11.glDisable(GL11.GL_ALPHA_TEST);
  OpenGlHelper.glBlendFunc(770, 771, 1, 0);
  GL11.glShadeModel(GL11.GL_SMOOTH);
  Tessellator tessellator = Tessellator.instance;
  tessellator.startDrawingQuads();
  tessellator.setColorRGBA_F(f1, f2, f3, f);
  tessellator.addVertex((double)par3, (double)par2, (double)0);
  tessellator.addVertex((double)par1, (double)par2, (double)0);
  tessellator.setColorRGBA_F(f5, f6, f7, f4);
  tessellator.addVertex((double)par1, (double)par4, (double)0);
  tessellator.addVertex((double)par3, (double)par4, (double)0);
  tessellator.draw();
  GL11.glShadeModel(GL11.GL_FLAT);
  GL11.glDisable(GL11.GL_BLEND);
  GL11.glEnable(GL11.GL_ALPHA_TEST);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

private static void pushViewport(int x, int y, int w, int h)
  {
  int tlx, tly, brx, bry;
  tlx = x;
  tly = y;
  brx = x + w;
  bry = y + h;
  
  x = tlx;
  y = tly;
  w = brx - tlx;
  h = bry - tly;
  
  Minecraft mc = Minecraft.getMinecraft();
  ScaledResolution scaledRes = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
  int guiScale = scaledRes.getScaleFactor();
  GL11.glEnable(GL11.GL_SCISSOR_TEST);    
  GL11.glScissor(x*guiScale, mc.displayHeight - y*guiScale - h*guiScale, w*guiScale, h*guiScale);  
  }

private static void disableViewport()
  {
  GL11.glScissor(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
  GL11.glDisable(GL11.GL_SCISSOR_TEST);
  }

}
