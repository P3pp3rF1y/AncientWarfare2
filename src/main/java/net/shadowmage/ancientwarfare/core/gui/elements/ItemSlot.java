package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * basic item-slot gui element
 * renders a single item-stack and slot background
 * includes basic highlighting when mouse-is over
 * sub-classes should add listeners to handle mouse interaction for the slot
 * 
 * the ItooltipRenderer passed during construction should be the base GUI that will handle rendering
 * @author Shadowmage
 *
 */
public class ItemSlot extends GuiElement
{
protected static RenderItem itemRender = new RenderItem();
ItemStack item;
protected ITooltipRenderer render;
protected boolean highlightOnMouseOver = true;
protected boolean renderTooltip = true;
protected boolean renderSlotBackground = true;

public ItemSlot(int topLeftX, int topLeftY, ItemStack item, ITooltipRenderer render)
  {
  super(topLeftX, topLeftY, 18, 18);
  this.item = item;
  this.render = render;
  }

public void setItem(ItemStack item)
  {
  this.item = item;
  }

public ItemSlot setRenderTooltip(boolean val)
  {
  this.renderTooltip = val;
  return this;
  }

public ItemSlot setHighlightOnMouseOver(boolean val)
  {
  this.highlightOnMouseOver = val;
  return this;
  }

public ItemSlot setRenderSlotBackground(boolean val)
  {
  this.renderSlotBackground = val;
  return this;
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  if(visible && item!=null)
    {
    Minecraft mc = Minecraft.getMinecraft();    
    if(renderSlotBackground)
      {
      mc.renderEngine.bindTexture(widgetTexture1);
      RenderTools.renderQuarteredTexture(256, 256, 152, 120, 18, 18, renderX, renderY, width, height);      
      }

    GL11.glDisable(GL11.GL_DEPTH_TEST);
    if(this.item!=null && this.item.getItem()!=null)
      {
      RenderHelper.enableGUIStandardItemLighting();
      itemRender.zLevel = 10.0F;
      FontRenderer font = null;
      if (item != null && item.getItem()!=null){font = item.getItem().getFontRenderer(item);}    
      if (font == null){font = Minecraft.getMinecraft().fontRenderer;}
      
      GL11.glEnable(GL11.GL_LIGHTING);
      itemRender.renderItemAndEffectIntoGUI(font, mc.getTextureManager(), item, renderX+1, renderY+1);
      if(renderTooltip)
        {
        itemRender.renderItemOverlayIntoGUI(font, mc.getTextureManager(), item, renderX+1, renderY+1, String.valueOf(item.stackSize));        
        }
      GL11.glDisable(GL11.GL_LIGHTING);
      }    
    
    if(isMouseOverElement(mouseX, mouseY))
      {
      if(highlightOnMouseOver)
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
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(renderX, renderY);
        GL11.glVertex2f(renderX, renderY+height);
        GL11.glVertex2f(renderX+width, renderY+height);
        GL11.glVertex2d(renderX+width, renderY);      
        GL11.glEnd();      
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);        
        }
      if(renderTooltip && this.item!=null && this.render!=null)
        {
        this.render.handleItemStackTooltipRender(item);
        }      
      }
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_LIGHTING);
    }  
  }

}
