package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportBase;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueTransport extends TileEntitySpecialRenderer
{

private static float[][] headRotationDirectionMatrix = new float[6][];
private static float[][] gearboxRotationMatrix = new float[6][];
private static ModelBaseAW model = null;
private static ModelPiece[] gearHeads = null;
private static ModelPiece gearbox = null;
private static float textureWidth, textureHeight;

private ResourceLocation regTex;

public RenderTileTorqueTransport(ResourceLocation reg)
  {
  constructModel();
  this.regTex = reg; 
  }

/**
 * really should be called optionalStaticInitialization()... but w/e
 */
private void constructModel()
  {
  if(model==null)
    {
    ModelLoader loader = new ModelLoader();
    model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_conduit.m2f"));    
    gearHeads = new ModelPiece[6];
    gearHeads[0] = model.getPiece("downShaft");
    gearHeads[1] = model.getPiece("upShaft");
    gearHeads[2] = model.getPiece("northShaft");
    gearHeads[3] = model.getPiece("southShaft");
    gearHeads[4] = model.getPiece("westShaft");
    gearHeads[5] = model.getPiece("eastShaft");
    gearbox = model.getPiece("gearBox");
    textureWidth = model.textureWidth();
    textureHeight = model.textureHeight();
    
    headRotationDirectionMatrix[0] = new float[]{ 0,-1, 0};//down
    headRotationDirectionMatrix[1] = new float[]{ 0, 1, 0};//up
    headRotationDirectionMatrix[2] = new float[]{ 0, 0,-1};//north
    headRotationDirectionMatrix[3] = new float[]{ 0, 0, 1};//south
    headRotationDirectionMatrix[4] = new float[]{-1, 0, 0};//west
    headRotationDirectionMatrix[5] = new float[]{ 1, 0, 0};//east  
    gearboxRotationMatrix[0] = new float[]{ -90,   0,   0};//d
    gearboxRotationMatrix[1] = new float[]{  90,   0,   0};//u
    gearboxRotationMatrix[2] = new float[]{   0,   0,   0};//n
    gearboxRotationMatrix[3] = new float[]{   0, 180,   0};//s
    gearboxRotationMatrix[4] = new float[]{   0,  90,   0};//w
    gearboxRotationMatrix[5] = new float[]{   0, 270,   0};//e
    }
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
  
  TileTorqueTransportBase conduit = (TileTorqueTransportBase)te;
  
  ITorqueTile[] neighbors = conduit.getNeighborTorqueTiles();//TODO update speed of input shafts to match speed of the output shaft of neighbor
  boolean[] connections = conduit.getConnections();
  float rotation = (float) getRotation(conduit.getClientOutputRotation(conduit.getPrimaryFacing()), conduit.getPrevClientOutputRotation(conduit.getPrimaryFacing()), delta);
    
  float[] rotationArray;
  float rx, ry, rz;

  ForgeDirection d;
  ModelPiece piece;
  
  //render heads
  bindTexture(regTex);
  for(int i = 0; i < 6; i++)
    {
    if(connections[i])
      {
      piece = gearHeads[i];
      rotationArray = headRotationDirectionMatrix[i];
      if(conduit.canOutputTorque(ForgeDirection.getOrientation(i)))
        {
        rx = rotationArray[0]*rotation;
        ry = rotationArray[1]*rotation;
        rz = rotationArray[2]*rotation;
        piece.setRotation(rx, ry, rz);        
        }
      else
        {
        if(neighbors!=null && neighbors[i]!=null && neighbors[i].useOutputRotation(null))
          {
          float r = (float) getRotation(neighbors[i].getClientOutputRotation(null), neighbors[i].getPrevClientOutputRotation(null), delta);
          rx = rotationArray[0]*r;
          ry = rotationArray[1]*r;
          rz = rotationArray[2]*r;
          }
        else
          {
          rx = rotationArray[0]*rotation;
          ry = rotationArray[1]*rotation;
          rz = rotationArray[2]*rotation;
          }
        piece.setRotation(-rx, -ry, -rz);
        }
      piece.render(textureWidth, textureHeight);
      }    
    }
  
  //render gearbox, texture already bound, rotate for proper orienation on gearbox texture  
  d = conduit.getPrimaryFacing();
  rotationArray = gearboxRotationMatrix[d.ordinal()];
  gearbox.setRotation(rotationArray[0], rotationArray[1], rotationArray[2]);
  gearbox.render(textureWidth, textureHeight);
    
  GL11.glPopMatrix();
  }

private double getRotation(double rotation, double prevRotation, float delta)
  {
  double rd = rotation-prevRotation;  
  return (prevRotation + rd*delta);
  }

}
