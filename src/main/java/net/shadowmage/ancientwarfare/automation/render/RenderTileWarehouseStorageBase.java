package net.shadowmage.ancientwarfare.automation.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase.WarehouseItemFilter;

import org.lwjgl.opengl.GL11;

public class RenderTileWarehouseStorageBase extends TileEntitySpecialRenderer
{
private static final ResourceLocation signTexture = new ResourceLocation("textures/entity/sign.png");
private static final ModelSign signModel = new ModelSign();
private static RenderItem render = new RenderItem();

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float deltaTime)
  {  
  TileWarehouseStorageBase tile = (TileWarehouseStorageBase)te;
  signModel.signStick.showModel = false;
    
  if(te.getWorldObj().isAirBlock(te.xCoord-1, te.yCoord, te.zCoord))
    {
    int i = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord-1, te.yCoord, te.zCoord, 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
    renderSignAt(tile, (x-1), y, z, 90);
    }
  if(te.getWorldObj().isAirBlock(te.xCoord+1, te.yCoord, te.zCoord))
    {
    int i = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord+1, te.yCoord, te.zCoord, 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
    renderSignAt(tile, (x+1), y, z, 90+180);
    }
  if(te.getWorldObj().isAirBlock(te.xCoord, te.yCoord, te.zCoord-1))
    {
    int i = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord-1, 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
    renderSignAt(tile, x, y, z-1, 90+90);
    }
  if(te.getWorldObj().isAirBlock(te.xCoord, te.yCoord, te.zCoord+1))
    {
    int i = te.getWorldObj().getLightBrightnessForSkyBlocks(te.xCoord, te.yCoord, te.zCoord+1, 0);
    int j = i % 65536;
    int k = i / 65536;
    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
    renderSignAt(tile, x, y, z+1, 90+90+180);
    }
  }

private void renderSignAt(TileWarehouseStorageBase te, double x, double y, double z, float r)
  {
  this.bindTexture(signTexture);
  float renderScale = 0.65f;//666667F;
  GL11.glPushMatrix();
  GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * renderScale, (float)z + 0.5F);    
  GL11.glRotatef(-r, 0.0F, 1.0F, 0.0F);
  GL11.glTranslatef(0.0F, -0.3125F, -0.4375F - 1.f*0.0625f);
  
  GL11.glPushMatrix();
  GL11.glScalef(renderScale, -renderScale, -renderScale);
  signModel.renderSign();  
  GL11.glPopMatrix();  
  
  GL11.glPushMatrix();
  renderFilterList(te.getFilters());
  GL11.glPopMatrix();
  GL11.glPopMatrix();  
  }

private void renderFilterList(List<WarehouseItemFilter>filters)
  {
  float f1 = 0.65f;
  
  float f3 = 0.016666668F * f1;
  
  GL11.glTranslatef(0.0F - 0.4f, 0.5F * f1 + 0.25f, 0.07F * f1 -0.6f);
  
  GL11.glScalef(f3, -f3, f3);
  
  GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
  
  Minecraft mc = Minecraft.getMinecraft();
  WarehouseItemFilter filter;
  
  testFilterRender();
  
//  int rendered = 0;
//  for(int i = 0; rendered<4 && i<filters.size(); i++)
//    {
//    filter = filters.get(i);
//    if(filter.getFilterItem()==null){continue;}
//    rendered++;
//    render.renderItemAndEffectIntoGUI(func_147498_b(), mc.getTextureManager(), filter.getFilterItem(), 0, 0);
//    }
  }

private void testFilterRender()
  {
  Minecraft mc = Minecraft.getMinecraft();
  WarehouseItemFilter filter;
  int rendered = 0;
  for(int i = 0; i<4; i++)
    {
    render.renderItemAndEffectIntoGUI(func_147498_b(), mc.getTextureManager(), new ItemStack(Blocks.wool,1,i), 0, i*18);
    }
  }

}
