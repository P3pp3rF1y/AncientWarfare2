package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.core.util.Trig;

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
  rotation+=0.4f;
  GL11.glPushMatrix();
  
  RenderTools.setFullColorLightmap();
  GL11.glTranslated(x+0.5d, y+1, z+0.5d);
  GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
  
  model.getPiece("flywheel2").setRotation(0, 0, rotation);
  calculateArmAngle(rotation);
  
  model.getPiece("flywheel_arm").setRotation(0, 0, -rotation);
  
  model.getPiece("piston_crank").setRotation(0, 0, rotation);
  model.getPiece("piston_arm").setRotation(0, 0, -rotation+armAngle);
  model.renderModel();

  GL11.glPopMatrix();
  }

float pistonPos, armAngle;

private void calculateArmAngle(float crankAngle)
  {
  float ra = crankAngle*Trig.TORADIANS;
  float crankDistance = 1.f;//side a
  float crankLength = 9.f;//side b
  calculatePistonPosition(ra, crankDistance, crankLength);
  calculateArmAngle(ra, crankLength, crankDistance, pistonPos);
  }

private void calculatePistonPosition(float crankAngleRadians, float radius, float length)
  {
  float cA = MathHelper.cos(crankAngleRadians);
  float sA = MathHelper.sin(crankAngleRadians);
  pistonPos = radius*cA + MathHelper.sqrt_float( length*length-radius*radius*sA*sA );
  
  float bx = sA * radius;
  float by = cA * radius;
  float cx = 0;
  float cy = pistonPos;
  
  float rlrA = (float) Math.atan2(cx-bx, cy-by);
  armAngle = rlrA*Trig.TODEGREES;
  }

private float calculateArmAngle(float radAngleA, float sideA, float sideB, float sideC)
  {
  if(radAngleA==0.f){return 90.f;}
  float shortSide = sideA<sideB? sideA : sideB;
  shortSide = sideC<shortSide? sideC : shortSide;
  float longSide = sideA>sideB ? sideA : sideB;
  longSide = sideC>longSide ? sideC : longSide;
  float a = (float)((Math.asin(radAngleA)*shortSide)/longSide);    
  return a;
  }

}
