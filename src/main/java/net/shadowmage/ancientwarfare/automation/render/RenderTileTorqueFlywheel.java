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
ModelBaseAW model;
ModelPiece spindle;
ModelPiece inputGear;
ModelPiece outputGear;
ModelPiece inputGear1;
ModelPiece outputGear1;
ModelPiece upperShroud;
ModelPiece lowerShroud;
ModelPiece flywheelExtension;
ResourceLocation tex;

public RenderTileTorqueFlywheel()
  {
  tex = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_light.png");
  
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel.m2f"));
  spindle = model.getPiece("spindle");
  inputGear = model.getPiece("inputGear");
  inputGear1 = model.getPiece("inputGear1");
  outputGear = model.getPiece("outputGear");
  outputGear1 = model.getPiece("outputGear1");
  upperShroud = model.getPiece("shroudUpper");
  lowerShroud = model.getPiece("shroudLower");
  flywheelExtension = model.getPiece("flywheel_extension");
  
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
  
  float rotation = (float) getRotation(flywheel.getClientOutputRotation(), flywheel.getPrevClientOutputRotation(), delta);
    
  ITorqueTile outputNeighbor = neighbors[d.ordinal()];
  outputGear.setVisible(outputNeighbor!=null);
  outputGear1.setVisible(outputNeighbor!=null);
  if(outputNeighbor!=null)
    {
    outputGear.setRotation(0, 0, -rotation);
    outputGear1.setRotation(0, -rotation*1.5f, 0);
    }
  else
    {
    outputGear.setRotation(0, 0, 0);
    outputGear1.setRotation(0, 0, 0);
    }
  
  rotation = (float) getRotation(flywheel.rotation, flywheel.prevRotation, delta);
  spindle.setRotation(0, rotation, 0);
  ITorqueTile inputNeighbor = neighbors[d.getOpposite().ordinal()];
  boolean input = inputNeighbor!=null && inputNeighbor.canOutput(d);
  inputGear.setVisible(input);
  inputGear1.setVisible(input); 
  if(input)
    {
    if(inputNeighbor.useClientRotation())
      {
      rotation = (float) getRotation(inputNeighbor.getClientOutputRotation(), inputNeighbor.getPrevClientOutputRotation(), delta);
      }
    inputGear.setRotation(0, 0, -rotation);
    inputGear1.setRotation(0, -rotation*1.5f, 0);      
    }
  
  boolean upper = neighbors[1]!=null && neighbors[1].getClass()==flywheel.getClass();
  flywheelExtension.setVisible(upper);
  upperShroud.setVisible(!upper);
  boolean lower = neighbors[0]!=null && neighbors[0].getClass()==flywheel.getClass();
  lowerShroud.setVisible(!lower);
  
  bindTexture(tex);    
  model.renderModel();
  GL11.glPopMatrix();
  
  
  GL11.glPushMatrix();
  if(flywheel.controlType>=0 && flywheel.controlHeight>0)
    {
    GL11.glTranslated(x+0.5d, y-flywheel.controlHeight, z+0.5d);
    if(flywheel.controlSize>1)
      {
      GL11.glScalef(3, flywheel.controlHeight, 3);
      }
    model.renderModel();
    }  
  GL11.glPopMatrix();  
  }

private double getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (prevRotation + rd*delta);
  }

}
