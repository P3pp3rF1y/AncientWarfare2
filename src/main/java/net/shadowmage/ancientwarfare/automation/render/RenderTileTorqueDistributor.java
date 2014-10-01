package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportDistributor;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueDistributor extends TileEntitySpecialRenderer
{


private float[][] rotationMatrices = new float[6][];

ResourceLocation regTex;
ResourceLocation outTex;
ModelBaseAW model;

String[] sideNames = new String[]{"downShaft", "upShaft", "northShaft", "southShaft", "westShaft", "eastShaft"};

public RenderTileTorqueDistributor(ModelBaseAW model, ResourceLocation reg, ResourceLocation out)
  {
  this.model = model;
  this.regTex = reg;
  this.outTex = out;
  
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
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y, z+0.5d);
  
  TileTorqueTransportDistributor conduit = (TileTorqueTransportDistributor)te;
  
  boolean[] connections = conduit.getConnections();
  float pr = (float) conduit.prevRotation;
  float r = (float) conduit.rotation;
  float rd = r-pr;  
  float rotation = (pr + rd*delta);  
  
  ModelPiece piece;
  float[] rot;
  float rx, ry, rz;
  boolean vis;
    
  for(int i = 0; i < 6; i++)
    {
    piece = model.getPiece(sideNames[i]);    
    vis = (conduit.canOutputTorque(ForgeDirection.getOrientation(i)) && connections[i]) || i==conduit.getPrimaryFacing().ordinal();
    piece.setVisible(vis);
    if(vis)
      {
      rot = rotationMatrices[i];
      rx = rot[0]*rotation;
      ry = rot[1]*rotation;
      rz = rot[2]*rotation;
      piece.setRotation(rx, ry, rz);      
      } 
    }  
  bindTexture(outTex);
  model.renderModel();
  
  for(int i = 0; i < 6; i++)
    {
    piece = model.getPiece(sideNames[i]);
    vis = conduit.canInputTorque(ForgeDirection.getOrientation(i)) && connections[i];
    piece.setVisible(vis);
    if(vis)
      {
      rot = rotationMatrices[i];
      rx = rot[0]*rotation;
      ry = rot[1]*rotation;
      rz = rot[2]*rotation;
      piece.setRotation(-rx, -ry, -rz);      
      } 
    }  
  bindTexture(regTex);  
  model.renderModel();
    
  GL11.glPopMatrix();
  }

}
