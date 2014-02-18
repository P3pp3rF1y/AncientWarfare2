package net.shadowmage.ancientwarfare.core.gui.elements;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

public class ItemSlot extends GuiElement
{
protected static RenderItem itemRender = new RenderItem();
ItemStack item;

public ItemSlot(int topLeftX, int topLeftY, ItemStack item)
  {
  super(topLeftX, topLeftY, 18, 18);
  this.item = item;
  }

public void setItem(ItemStack item)
  {
  
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible && item!=null)
    {
    Minecraft mc = Minecraft.getMinecraft();    
    mc.renderEngine.bindTexture(widgetTexture1);
    renderQuarteredTexture(256, 256, 152, 120, 18, 18, renderX, renderY, width, height);
    GL11.glTranslatef(0.0F, 0.0F, 32.0F);
    
    itemRender.zLevel = 10.0F;
    FontRenderer font = null;
    if (item != null){font = item.getItem().getFontRenderer(item);}    
    if (font == null){font = Minecraft.getMinecraft().fontRenderer;}
    
    itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), item, renderX+1, renderY+1);
    itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), item, renderX+1, renderY+1, String.valueOf(item.stackSize));
    
    if(isMouseOverElement(mouseX, mouseY))
      {
      /**
       *  TODO -- find proper alpha for blend..it is close now, but probably not an exact match for vanilla
       *  highlighting
       */
      GL11.glColor4f(1.f, 1.f, 1.f, 0.55f);
      GL11.glEnable(GL11.GL_BLEND);
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
      GL11.glDisable(GL11.GL_LIGHTING);
      GL11.glDisable(GL11.GL_TEXTURE_2D);
      GL11.glDisable(GL11.GL_DEPTH_TEST);
      GL11.glBegin(GL11.GL_QUADS);
      GL11.glVertex2f(renderX, renderY);
      GL11.glVertex2f(renderX, renderY+height);
      GL11.glVertex2f(renderX+width, renderY+height);
      GL11.glVertex2d(renderX+width, renderY);      
      GL11.glEnd();      
      GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
      GL11.glDisable(GL11.GL_BLEND);
      GL11.glEnable(GL11.GL_DEPTH_TEST);
      GL11.glEnable(GL11.GL_LIGHTING);
      GL11.glEnable(GL11.GL_TEXTURE_2D);
      }
    }  
  }

}
