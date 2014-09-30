package net.shadowmage.ancientwarfare.automation.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;

public class RenderTileFlywheelStorage extends TileEntitySpecialRenderer
{

ModelBaseAW cube;
ResourceLocation tex;

public RenderTileFlywheelStorage(ResourceLocation tex)
  {
  ModelLoader loader = new ModelLoader();
  cube = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/cube.m2f"));
  this.tex = tex;
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  TileFlywheelStorage storage = (TileFlywheelStorage)te;
  if(storage.controllerPos==null)
    {
    GL11.glPushMatrix();
    GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
    bindTexture(tex);
    cube.renderModel();
    GL11.glPopMatrix();
    }
  }

}
