package net.shadowmage.ancientwarfare.automation.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.IWarehouseStorageTile;
import net.shadowmage.ancientwarfare.automation.tile.TileWarehouseStorageBase;
import net.shadowmage.ancientwarfare.automation.tile.WarehouseItemFilter;

import org.lwjgl.opengl.GL11;

public class RenderTileWarehouseStorageBase extends TileEntitySpecialRenderer
{
private static final ResourceLocation signTexture = new ResourceLocation("textures/entity/sign.png");
private static final ModelSign signModel = new ModelSign();
private static RenderItem render = new RenderItem();


private static final ForgeDirection[] directions = new ForgeDirection[]{ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.NORTH, ForgeDirection.EAST};

public RenderTileWarehouseStorageBase()
  {
  render.zLevel = -50.f;
  signModel.signStick.showModel = false;
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float deltaTime)
  {  
  TileWarehouseStorageBase tile = (TileWarehouseStorageBase)te;
  
  ForgeDirection d;
  float r;
  for(int b = 0; b < 4; b++)
    {
    d = directions[b];
    r = 90 * b;
    if(te.getWorldObj().isAirBlock(te.x+d.offsetX, te.y+d.offsetY, te.z+d.offsetZ))
      {
      int i = te.getWorldObj().getCombinedLight(te.x+d.offsetX, te.y+d.offsetY, te.z+d.offsetZ, 0);
      int j = i % 65536;
      int k = i / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j / 1.0F, (float)k / 1.0F);
      renderSignBoard(x+d.offsetX, y+d.offsetY, z+d.offsetZ, r);

      GL11.glPushMatrix();
      GL11.glTranslated(x, y, z);
      GL11.glTranslatef(0.5f, 1.f, 0.5f);//translate the point to the top-center of the block
      GL11.glRotatef(-r+180.f, 0, 1, 0);//rotate for rotation
      GL11.glTranslatef(0.5f, 0, -0.5f);//translate to top-left corner
      renderSignContents(tile, x, y, z, r);
      GL11.glPopMatrix();      
      }
    }
  }

private void renderSignBoard(double x, double y, double z, float r)
  {
  this.bindTexture(signTexture);
  float renderScale = 0.65f;//666667F;
  GL11.glPushMatrix();
  GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * renderScale, (float)z + 0.5F);    
  GL11.glRotatef(-r, 0.0F, 1.0F, 0.0F);
  GL11.glTranslatef(0.0F, -0.3125F, -0.4375F - 1.f*0.0625f);  
  GL11.glScalef(renderScale, -renderScale, -renderScale);
  signModel.renderSign();  
  GL11.glPopMatrix();  
  }

/*
 * matrix should be setup so that 0,0 is upper-left-hand corner of the sign-board, with a
 * transformation of 1 being 1 BLOCK
 */
private void renderSignContents(IWarehouseStorageTile tile, double x, double y, double z, float r)
  {
  GL11.glPushMatrix();
//  drawPointAtCurrentOrigin();

  
  //adjust translation for sign-face, move right a little, down a 1/4 block, and out a little
  //this puts the origin at upper-left-hand corner of the sign-face, about 1 sign pixel in and down
  GL11.glTranslatef(0, -0.25f, -0.042f);
  
  //rescale for gui rendering axis flip
  GL11.glScalef(-1, -1, -1);
  //rescale for font rendering, can fit 4 text lines @ 10px spacing at this scale
//  GL11.glScalef(0.011f, 0.011f, 0.011f);
  GL11.glScalef(0.0050f, 0.0050f, 0.0050f);//this scale puts it at 200 pixels per block
  GL11.glScalef(1f, 1f, 0.0001f);
  FontRenderer fr = func_147498_b();
  
  ItemStack filterItem;
  WarehouseItemFilter filter;
  String name = "";
  List<WarehouseItemFilter>filters = tile.getFilters();
  String tileName = "what_to_do_with_this_field?";
  fr.drawString(tileName, 100-fr.getStringWidth(tileName)/2, 10, 0xffffffff);
  for(int i = 0; i < 4 && i<filters.size(); i++)
    {
    filter = filters.get(i);
    filterItem = filter.getFilterItem();
    
    if(filterItem!=null)
      {
      render.renderItemAndEffectIntoGUI(fr, Minecraft.getMinecraft().getTextureManager(), filter.getFilterItem(), 0+12, i*18+10+10);      
      }
    
    name = filterItem==null? "Empty Filter" : filterItem.getDisplayName();
    fr.drawString(name, 20+12, i*18+4+10+10, 0xffffffff);
    
    name = String.valueOf(filter.getFilterQuantity());
    fr.drawString(name, 200-13-fr.getStringWidth(name), i*18+4+10+10, 0xffffffff);
    
    }
    
  GL11.glPopMatrix();  
  }

private void drawPointAtCurrentOrigin()
  {
  //debug point rendering
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glColor4f(1.f, 0.f, 0.f, 1.f);
  GL11.glPointSize(10.f);
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f((float)0, (float)0, (float)0);
  GL11.glEnd();
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

}
