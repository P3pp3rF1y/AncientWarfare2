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
ModelBaseAW controllerModel;
ModelPiece controlInput, controlOutput, controlSpindle;

ResourceLocation tex1, tex2, tex3;

public RenderTileTorqueFlywheel()
  {
  tex1 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_light.png");
  tex2 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_medium.png");
  tex3 = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_heavy.png");
    
  ModelLoader loader = new ModelLoader();
  controllerModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_controller.m2f"));
  controlInput = controllerModel.getPiece("inputGear");
  controlOutput = controllerModel.getPiece("outputGear");
  controlSpindle = controllerModel.getPiece("spindle");
  
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
  
  ITorqueTile[] neighbors = flywheel.getTorqueCache();
  ForgeDirection d = flywheel.getPrimaryFacing();
  float[] rot = gearboxRotationMatrix[d.ordinal()];
  if(rot[0]!=0){GL11.glRotatef(rot[0], 1, 0, 0);}
  if(rot[1]!=0){GL11.glRotatef(rot[1], 0, 1, 0);}
  if(rot[2]!=0){GL11.glRotatef(rot[2], 0, 0, 1);}
  
  float outputRotation = flywheel.getClientOutputRotation(d, delta);
  float inputRotation = (float) getRotation(flywheel.getInputRotation(), flywheel.getInputPrevRotation(), delta);
  float flywheelRotation = (float) getRotation(flywheel.getFlywheelRotation(), flywheel.getFlywheelPrevRotation(), delta);

  ITorqueTile inputNeighbor = neighbors[d.getOpposite().ordinal()];
  if(inputNeighbor!=null && inputNeighbor.canOutputTorque(d) && inputNeighbor.useOutputRotation(d.getOpposite()))
    {
    inputRotation = inputNeighbor.getClientOutputRotation(d.getOpposite(), delta);
    }
  
  controlInput.setRotation(0, 0, -inputRotation);
  controlOutput.setRotation(0, 0, -outputRotation);
  
  
  controlSpindle.setRotation(0, flywheelRotation, 0);
  
  bindTexture(tex1);    
  controllerModel.renderModel();
  GL11.glPopMatrix(); 
  }

private double getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (prevRotation + rd*delta);
  }

}
