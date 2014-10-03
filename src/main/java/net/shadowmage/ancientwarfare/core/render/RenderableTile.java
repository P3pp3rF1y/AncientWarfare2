package net.shadowmage.ancientwarfare.core.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public abstract class RenderableTile extends TileEntitySpecialRenderer implements IItemRenderer
{

public RenderableTile()
  {
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y, z+0.5d);
  renderWorldTile(te, delta);
  GL11.glPopMatrix();
  }

@Override
public boolean handleRenderType(ItemStack item, ItemRenderType type)
  {
  return true;
  }

@Override
public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
  {
  return true;
  }

@Override
public void renderItem(ItemRenderType type, ItemStack item, Object... data)
  {
  GL11.glPushMatrix();
  GL11.glTranslated(0.5d, 0, 0.5d);
  renderItemTile(item);
  GL11.glPopMatrix();
  }

protected abstract void renderWorldTile(TileEntity te, float delta);

protected abstract void renderItemTile(ItemStack stack);

}
