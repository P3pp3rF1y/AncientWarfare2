package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportShaft;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueShaft extends TileEntitySpecialRenderer
{

private float[][] gearboxRotationMatrix = new float[6][];
private ModelBaseAW model = null;
ModelPiece inputHead, outputHead, shaft, gearbox;
ResourceLocation tex;

public RenderTileTorqueShaft(ResourceLocation tex)
  {
  this.tex = tex;
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_shaft.m2f"));
  inputHead = model.getPiece("southShaft");
  outputHead = model.getPiece("northShaft");
  shaft = model.getPiece("shaft");
  gearbox = model.getPiece("gearBox");
  
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
  GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
  
  TileTorqueTransportShaft shaft = (TileTorqueTransportShaft)te;
  ForgeDirection d = shaft.getPrimaryFacing();
  float[] rotations = gearboxRotationMatrix[d.ordinal()];
  if(rotations[0]!=0){GL11.glRotatef(rotations[0], 1, 0, 0);}
  if(rotations[1]!=0){GL11.glRotatef(rotations[1], 0, 1, 0);}
  if(rotations[2]!=0){GL11.glRotatef(rotations[2], 0, 0, 1);}
  
  this.inputHead.setVisible(shaft.prev==null);
  this.gearbox.setVisible(shaft.prev==null);
  this.outputHead.setVisible(shaft.next==null);
  
  float rotation = (float) getRotation(shaft.rotation, shaft.prevRotation, delta);  
  this.shaft.setRotation(0, 0, -rotation);
  this.outputHead.setRotation(0, 0, -rotation);   
  
  if(shaft.prev==null)//no prev shaft, render gearbox and input head at either shaft rpm or input rpm
    {
    ITorqueTile itt = shaft.getNeighborTorqueTiles()[d.getOpposite().ordinal()];
    if(itt!=null && itt.canOutputTorque(d) && itt.useClientRotation())
      {
      rotation = (float) getRotation(itt.getClientOutputRotation(), itt.getPrevClientOutputRotation(), delta);
      }
    inputHead.setRotation(0, 0, -rotation);
    }
  
  bindTexture(tex);
  model.renderModel();  
  GL11.glPopMatrix();
  }

private double getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (prevRotation + rd*delta);
  }
}
