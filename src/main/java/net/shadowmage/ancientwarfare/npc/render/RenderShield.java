package net.shadowmage.ancientwarfare.npc.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class RenderShield implements IItemRenderer
{

public RenderShield()
  {
  ItemRenderer render = new ItemRenderer(Minecraft.getMinecraft());
  }

@Override
public boolean handleRenderType(ItemStack item, ItemRenderType type)
  {
  return type==ItemRenderType.EQUIPPED;
  }

@Override
public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
  {
  return type==ItemRenderType.EQUIPPED && helper==ItemRendererHelper.EQUIPPED_BLOCK;
  }

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data)
  {  
  RenderBlocks blocks = (RenderBlocks)data[0];
  EntityLivingBase entity = (EntityLivingBase)data[1];
//  AWLog.logDebug("rendering...: "+item+" :: "+entity);
//  Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem(entity, item, item.getItemDamage(), type);
  render(entity, item);
  }

private void render(EntityLivingBase entity, ItemStack stack)
  {
  TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
  IIcon iicon = entity.getItemIcon(stack, stack.getItemDamage());
  if (iicon == null)
    {
    return;
    }
  GL11.glPushMatrix();
  texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
  TextureUtil.func_147950_a(false, false);
  Tessellator tessellator = Tessellator.instance;
  float f = iicon.getMinU();
  float f1 = iicon.getMaxU();
  float f2 = iicon.getMinV();
  float f3 = iicon.getMaxV();
  float f4 = 0.0F;
  float f5 = 0.3F;
  GL11.glEnable(GL12.GL_RESCALE_NORMAL);

  GL11.glTranslatef(-f4, -f5, 0.0F);
  float f6 = 1.5F;
  GL11.glScalef(f6, f6, f6);
  GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
  GL11.glTranslatef(0.0625f, 0.125F, 0.6875f - 3.f*0.0625f);
  GL11.glRotatef(90, 0, 1, 0);
  GL11.glTranslatef(-8.f*0.0625f, 10.f*0.0625f, 0);
  GL11.glRotatef(-80.f, 1, 0, 0);
  GL11.glTranslatef(0, -3.f*0.0625f, 0);
  ItemRenderer.renderItemIn2D(tessellator, f1, f2, f, f3, iicon.getIconWidth(), iicon.getIconHeight(), 0.0625F);
  
  
  //TODO reenable for enchanted glint effect -- need to get a ref to the glint texture cleanly
//  if (stack.hasEffect(stack.getItemDamage()))
//  {
//      GL11.glDepthFunc(GL11.GL_EQUAL);
//      GL11.glDisable(GL11.GL_LIGHTING);
//      texturemanager.bindTexture(RES_ITEM_GLINT);
//      GL11.glEnable(GL11.GL_BLEND);
//      OpenGlHelper.glBlendFunc(768, 1, 1, 0);
//      float f7 = 0.76F;
//      GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
//      GL11.glMatrixMode(GL11.GL_TEXTURE);
//      GL11.glPushMatrix();
//      float f8 = 0.125F;
//      GL11.glScalef(f8, f8, f8);
//      float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
//      GL11.glTranslatef(f9, 0.0F, 0.0F);
//      GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
//      ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//      GL11.glPopMatrix();
//      GL11.glPushMatrix();
//      GL11.glScalef(f8, f8, f8);
//      f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
//      GL11.glTranslatef(-f9, 0.0F, 0.0F);
//      GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//      ItemRenderer.renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
//      GL11.glPopMatrix();
//      GL11.glMatrixMode(GL11.GL_MODELVIEW);
//      GL11.glDisable(GL11.GL_BLEND);
//      GL11.glEnable(GL11.GL_LIGHTING);
//      GL11.glDepthFunc(GL11.GL_LEQUAL);
//  }

  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
  TextureUtil.func_147945_b();
  GL11.glPopMatrix();
  }


}
