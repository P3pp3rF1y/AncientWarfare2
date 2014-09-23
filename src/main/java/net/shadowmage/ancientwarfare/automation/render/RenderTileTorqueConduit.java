package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueConduit extends TileEntitySpecialRenderer
{

private float[][] rotationMatrices = new float[6][];

ResourceLocation regTex;
ResourceLocation outTex;
ModelBaseAW model;

String[] sideNames = new String[]{"downShaft", "upShaft", "northShaft", "southShaft", "westShaft", "eastShaft"};

public RenderTileTorqueConduit()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_conduit.m2f"));
  regTex = new ResourceLocation("ancientwarfare", "foo1.png");
  outTex = new ResourceLocation("ancientwarfare", "foo2.png");
  
  rotationMatrices[0] = new float[]{ 0,-1, 0};//down
  rotationMatrices[1] = new float[]{ 0, 1, 0};//up
  rotationMatrices[2] = new float[]{ 0, 0,-1};//north
  rotationMatrices[3] = new float[]{ 0, 0, 1};//south
  rotationMatrices[4] = new float[]{-1, 0, 0};//west
  rotationMatrices[5] = new float[]{ 1, 0, 0};//east
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  TileTorqueTransportConduit conduit = (TileTorqueTransportConduit)te;
  
  ForgeDirection d = conduit.getPrimaryFacing();
  int d1 = d.ordinal();
  float rotation = (float)conduit.rotation;
  
  boolean[] connections = conduit.getConnections();
  
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y, z+0.5d);
  
  ModelPiece piece;
  for(int i = 0; i < 6; i++)
    {
    piece = model.getPiece(sideNames[i]);
    piece.setVisible(false);    
    }
  
  bindTexture(outTex);
  piece = model.getPiece(sideNames[d1]);
  piece.setVisible(true);
  
  
  float[] rot = rotationMatrices[d1];
  float rx = rot[0]*rotation;
  float ry = rot[1]*rotation;
  float rz = rot[2]*rotation;
  piece.setRotation(rx, ry, rz);
  model.renderModel();
  
  for(int i = 0; i < 6; i++)
    {
    if(i==d1){model.getPiece(sideNames[i]).setVisible(false);}//already rendered 'output' side
    else if(connections[i])
      {      
      rot = rotationMatrices[i];
      rx = rot[0]*rotation;
      ry = rot[1]*rotation;
      rz = rot[2]*rotation;
      piece = model.getPiece(sideNames[i]);
      piece.setVisible(true);
      piece.setRotation(-rx, -ry, -rz);
      }
    }
  bindTexture(regTex);  
  model.renderModel();
  
  GL11.glPopMatrix();
  }

}
