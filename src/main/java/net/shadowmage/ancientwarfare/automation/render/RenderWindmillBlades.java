package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorHandCranked;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderWindmillBlades extends TileEntitySpecialRenderer implements IItemRenderer
{

ResourceLocation texture;
ModelBaseAW model;
ModelPiece outputGear;

public RenderWindmillBlades()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/windmill_10.m2f"));
  outputGear = model.getPiece("outputGear");
  
  texture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_10.png");
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  TileWindmillBlade blade = (TileWindmillBlade)te;
  if(blade.isControl)
    {
    GL11.glPushMatrix();
    bindTexture(texture);
    GL11.glTranslated(x+0.5d, y, z+0.5d);
    renderModel(0, 0, blade.windmillDirection);
    GL11.glPopMatrix();
    }
  }

protected void renderModel(float inR, float outR, int face)  
  {
  float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
  if(rot[0]!=0){GL11.glRotatef(rot[0], 1, 0, 0);}
  if(rot[1]!=0){GL11.glRotatef(rot[1], 0, 1, 0);}
  if(rot[2]!=0){GL11.glRotatef(rot[2], 0, 0, 1);}  
  outputGear.setRotation(0, 0, outR);
  model.renderModel();
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
  bindTexture(texture);
  GL11.glTranslated(0.5d, 0, 0.5d);
  renderModel(0, 0, 2);
  GL11.glPopMatrix();  
  }

}
