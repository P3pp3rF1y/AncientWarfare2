package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.core.model.crafting_table.ModelCraftingBase;

import org.lwjgl.opengl.GL11;

public class TileCraftingTableRender extends TileEntitySpecialRenderer implements IItemRenderer
{

ModelCraftingBase model;
ResourceLocation texture;

public TileCraftingTableRender(ModelCraftingBase model, String tex)
  {
  this.model = model;
  texture = new ResourceLocation("ancientwarfare", tex);
  }

@Override
public boolean handleRenderType(ItemStack item, ItemRenderType type)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data)
  {
  GL11.glPushMatrix(); 
  GL11.glScalef(-1, -1, 1);
  GL11.glTranslatef(-0.5f, 0.0f, 0.5f);
  GL11.glRotatef(270, 0, 1, 0);
  bindTexture(texture);
  model.renderModel();
  GL11.glPopMatrix();
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  { 
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y, z+0.5d);
  float rotation = 0;
  GL11.glRotatef(-90 * rotation, 0, 1, 0);
  GL11.glScalef(-1, -1, 1);
  bindTexture(texture);
  model.renderModel();
  GL11.glPopMatrix();
  }

}
