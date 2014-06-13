package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import org.lwjgl.opengl.GL11;

public class RenderSterlingEngine extends TileEntitySpecialRenderer
{

ModelBaseAW model;

float rotation;



public RenderSterlingEngine()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/sterling_engine.mf2"));  
  }

@Override
public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick)
  {
  rotation+=0.1f;
  GL11.glPushMatrix();
  
  RenderTools.setFullColorLightmap();
  GL11.glTranslated(x+0.5d, y+1, z+0.5d);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  
  model.getPiece("flywheel2").setRotation(0, 0, rotation);
  model.renderModel();

  GL11.glPopMatrix();
  }

}
