package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueStorageFlywheelController;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueFlywheel extends TileEntitySpecialRenderer
{

private float[][] gearboxRotationMatrix = new float[6][];
ModelBaseAW controllerModel, smallModel, largeModel;
ModelPiece controlInput, controlOutput, controlSpindle;
ModelPiece spindleSmall;
ModelPiece upperShroudSmall;
ModelPiece lowerShroudSmall;
ModelPiece flywheelExtensionSmall;
ModelPiece spindleLarge;
ModelPiece upperShroudLarge;
ModelPiece lowerShroudLarge;
ModelPiece flywheelExtensionLarge;

ResourceLocation smallTex[] = new ResourceLocation[3];
ResourceLocation largeTex[] = new ResourceLocation[3];
ResourceLocation tex1, tex2, tex3;

public RenderTileTorqueFlywheel()
  {
  tex1 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_light.png");
  tex2 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_medium.png");
  tex3 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_heavy.png");
  
  smallTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  smallTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  smallTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  
  largeTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  largeTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  largeTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  
  ModelLoader loader = new ModelLoader();
  controllerModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_controller.m2f"));
  controlInput = controllerModel.getPiece("inputGear");
  controlOutput = controllerModel.getPiece("outputGear");
  controlSpindle = controllerModel.getPiece("spindle");
  
  smallModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_small.m2f"));
  spindleSmall = smallModel.getPiece("spindle");
  upperShroudSmall = smallModel.getPiece("shroudUpper");
  lowerShroudSmall = smallModel.getPiece("shroudLower");
  flywheelExtensionSmall = smallModel.getPiece("flywheelExtension");
  largeModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_large.m2f"));
  spindleLarge = largeModel.getPiece("spindle");
  upperShroudLarge = largeModel.getPiece("shroudUpper");
  lowerShroudLarge = largeModel.getPiece("shroudLower");
  flywheelExtensionLarge = largeModel.getPiece("flywheelExtension");
  
  gearboxRotationMatrix[0] = new float[]{ -90,   0,   0};//d
  gearboxRotationMatrix[1] = new float[]{  90,   0,   0};//u
  gearboxRotationMatrix[2] = new float[]{   0,   0,   0};//n
  gearboxRotationMatrix[3] = new float[]{   0, 180,   0};//s
  gearboxRotationMatrix[4] = new float[]{   0,  90,   0};//w
  gearboxRotationMatrix[5] = new float[]{   0, 270,   0};//e
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y, z+0.5d);
    
  TileTorqueStorageFlywheelController flywheel = (TileTorqueStorageFlywheelController)te;
  
  ITorqueTile[] neighbors = flywheel.getNeighborTorqueTiles();
  ForgeDirection d = flywheel.getPrimaryFacing();
  float[] rot = gearboxRotationMatrix[d.ordinal()];
  if(rot[0]!=0){GL11.glRotatef(rot[0], 1, 0, 0);}
  if(rot[1]!=0){GL11.glRotatef(rot[1], 0, 1, 0);}
  if(rot[2]!=0){GL11.glRotatef(rot[2], 0, 0, 1);}
  
  float outputRotation = (float) getRotation(flywheel.getClientOutputRotation(), flywheel.getPrevClientOutputRotation(), delta);
  float flywheelRotation = (float) getRotation(flywheel.rotation, flywheel.prevRotation, delta);
  float inputRotation = flywheelRotation;

  ITorqueTile inputNeighbor = neighbors[d.getOpposite().ordinal()];
  if(inputNeighbor!=null && inputNeighbor.canOutput(d) && inputNeighbor.useClientRotation())
    {
    inputRotation = (float) getRotation(inputNeighbor.getClientOutputRotation(), inputNeighbor.getPrevClientOutputRotation(), delta);
    }
  
  controlInput.setRotation(0, 0, -inputRotation);
  controlOutput.setRotation(0, 0, -outputRotation);
  controlSpindle.setRotation(0, flywheelRotation, 0);
  
  bindTexture(tex1);    
  controllerModel.renderModel();
  GL11.glPopMatrix();
    
  GL11.glPushMatrix();
  if(flywheel.controlType>=0 && flywheel.controlHeight>0)
    {
    GL11.glTranslated(x+0.5d, y-flywheel.controlHeight, z+0.5d);
    if(flywheel.controlSize>1)
      {
      renderLargeModel(flywheel.controlType, flywheel.controlHeight, flywheelRotation);
      }
    else
      {
      renderSmallModel(flywheel.controlType, flywheel.controlHeight, flywheelRotation);
      }
    }  
  GL11.glPopMatrix();  
  }

protected void renderSmallModel(int type, int height, float rotation)
  {  
  bindTexture(smallTex[type]);
  GL11.glPushMatrix();
  spindleSmall.setRotation(0, rotation, 0);
  for(int i = 0; i <height; i++)
    {
    flywheelExtensionSmall.setVisible(i<height-1);//at every level less than highest
    upperShroudSmall.setVisible(i==height-1);//at highest level
    lowerShroudSmall.setVisible(i==0);//at ground level
    smallModel.renderModel();
    GL11.glTranslatef(0, 1, 0);
    }
  GL11.glPopMatrix();
  }

protected void renderLargeModel(int type, int height, float rotation)
  {
  bindTexture(largeTex[type]);
  GL11.glPushMatrix();
  spindleLarge.setRotation(0, rotation, 0);
  for(int i = 0; i <height; i++)
    {
    flywheelExtensionLarge.setVisible(i<height-1);//at every level less than highest
    upperShroudLarge.setVisible(i==height-1);//at highest level
    lowerShroudLarge.setVisible(i==0);//at ground level
    largeModel.renderModel();
    GL11.glTranslatef(0, 1, 0);
    }
  GL11.glPopMatrix();
  }

private double getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (prevRotation + rd*delta);
  }

}
