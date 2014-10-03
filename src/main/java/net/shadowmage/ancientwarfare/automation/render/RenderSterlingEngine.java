package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueGeneratorSterling;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.util.Trig;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderSterlingEngine extends TileEntitySpecialRenderer implements IItemRenderer
{

ModelBaseAW model;

float rotation;
ResourceLocation texture;

ModelPiece flywheel, pistonCrank, pistonCrank2, flywheel_arm, piston_arm, piston_arm2;


public RenderSterlingEngine()
  {
  ModelLoader loader = new ModelLoader();
  texture = new ResourceLocation("ancientwarfare:textures/model/automation/sterling_engine.png");
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/sterling_engine.m2f"));
  flywheel = model.getPiece("flywheel2");
  pistonCrank = model.getPiece("piston_crank");
  pistonCrank2 = model.getPiece("piston_crank2");
  
  flywheel_arm = model.getPiece("flywheel_arm");
  piston_arm = model.getPiece("piston_arm");
  piston_arm2 = model.getPiece("piston_arm2");
  }

@Override
public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick)
  {
  TileTorqueGeneratorSterling tt = (TileTorqueGeneratorSterling)tile;
  ForgeDirection d = tt.getPrimaryFacing();
  float baseRotation = d==ForgeDirection.SOUTH? 180.f : d==ForgeDirection.WEST ? 270.f : d==ForgeDirection.EAST? 90.f : 0.f;
    
  rotation = -(tt.getClientOutputRotation(d, partialTick));
      
  GL11.glPushMatrix();

//  GL11.glEnable(GL12.GL_RESCALE_NORMAL);
  GL11.glTranslated(x+0.5d, y, z+0.5d);
  GL11.glRotatef(-baseRotation, 0, 1, 0);
  bindTexture(texture);  
    
  flywheel.setRotation(0, 0, rotation);
  pistonCrank2.setRotation(0, 0, rotation);
  flywheel_arm.setRotation(0, 0, -rotation);

  calculateArmAngle1(-rotation);
  calculateArmAngle2(-rotation-90);
  pistonCrank.setRotation(0, 0, -rotation);
  piston_arm.setRotation(0, 0, rotation+armAngle);  
  piston_arm2.setRotation(0, 0, rotation+armAngle2);  
  model.renderModel();
//  GL11.glDisable(GL12.GL_RESCALE_NORMAL);
  GL11.glPopMatrix();
  }

float pistonPos, armAngle, pistonPos2, armAngle2;

private void calculateArmAngle1(float crankAngle)
  {
  float ra = crankAngle*Trig.TORADIANS;
  float crankDistance = 1.f;//side a
  float crankLength = 9.f;//side b
  calculatePistonPosition1(ra, crankDistance, crankLength);
  }

private void calculatePistonPosition1(float crankAngleRadians, float radius, float length)
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

private void calculateArmAngle2(float crankAngle)
  {
  float ra = crankAngle*Trig.TORADIANS;
  float crankDistance = 1.f;//side a
  float crankLength = 7.f;//side b
  calculatePistonPosition2(ra, crankDistance, crankLength);
  }

private void calculatePistonPosition2(float crankAngleRadians, float radius, float length)
  {
  float cA = MathHelper.cos(crankAngleRadians);
  float sA = MathHelper.sin(crankAngleRadians);
  pistonPos2 = radius*cA + MathHelper.sqrt_float( length*length-radius*radius*sA*sA );
  
  float bx = sA * radius;
  float by = cA * radius;
  float cx = 0;
  float cy = pistonPos2;

  float rlrA = (float) Math.atan2(cx-bx, cy-by);
  armAngle2 = rlrA*Trig.TODEGREES;
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
  bindTexture(texture);
      
  flywheel.setRotation(0, 0, 0);
  pistonCrank2.setRotation(0, 0, 0);
  flywheel_arm.setRotation(0, 0, 0);

  pistonCrank.setRotation(0, 0, 0);
  piston_arm.setRotation(0, 0, 0);  
  piston_arm2.setRotation(0, 0, 0);  
  model.renderModel();
  GL11.glPopMatrix();
  }

}
