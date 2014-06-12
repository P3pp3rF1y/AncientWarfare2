package net.shadowmage.ancientwarfare.automation.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;

import net.minecraft.client.model.ModelPig;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.model.PrimitiveBox;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

public class RenderSterlingEngine extends TileEntitySpecialRenderer
{

ModelBaseAW model;
Sphere sphere;

float rotation;

long lastTick;

ModelPig pmt;

ModelBaseAW m;
ModelPiece p;
PrimitiveBox bx;

public RenderSterlingEngine()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/sterling_engine.mf2"));
  pmt = new ModelPig();
  sphere = new Sphere();
  sphere.setNormals(GLU.GLU_FLAT);
  sphere.setTextureFlag(false);
  
  m = new ModelBaseAW();
  p = new ModelPiece(m, "", 0, 0, 0, 0, 0, 0, null);
  m.addPiece(p);  
  bx = new PrimitiveBox(p, -0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, 0, 0, 0, 0, 0);
  p.addPrimitive(bx);
  }

@Override
public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick)
  {
  rotation+=0.1f;
  GL11.glPushMatrix();
  
  RenderTools.setFullColorLightmap();
  GL11.glTranslated(x+0.5d, y+1, z+0.5d);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  
//  model.getPiece("flywheel2").setRotation(0, 0, rotation);
//  model.renderModel();

  GL11.glTranslatef(0, 1, 0);
  GL11.glRotatef(rotation, 0, 0, 1);
//  pmt.render(null, 0.f, 0.f, 0, 0, 0, 0.0625f);
  
//  sphere.draw(1.f, 30, 30);
  
  bx.render();
  GL11.glPopMatrix();
  }

}
