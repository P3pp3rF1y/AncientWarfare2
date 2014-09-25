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

public class RenderTileTorqueConduit2 extends TileEntitySpecialRenderer
{

float[][] rotationMatrix = new float[6][];
ResourceLocation tex, outTex;
ModelBaseAW model;

public RenderTileTorqueConduit2()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_conduit2.m2f"));  
  tex = new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_reg2.png");
  outTex = new ResourceLocation("ancientwarfare", "textures/model/automation/tile_torque_conduit_light_out2.png");
  rotationMatrix[0] = new float[]{ -90,   0,   0};//d
  rotationMatrix[1] = new float[]{  90,   0,   0};//u
  rotationMatrix[2] = new float[]{   0,   0,   0};//n
  rotationMatrix[3] = new float[]{   0, 180,   0};//s
  rotationMatrix[4] = new float[]{   0,  90,   0};//w
  rotationMatrix[5] = new float[]{   0, 270,   0};//e
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
//  long t1 = System.nanoTime();
  GL11.glPushMatrix();
  GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
  
  TileTorqueTransportConduit conduit = (TileTorqueTransportConduit)te;  
  boolean[] connections = conduit.getConnections();
  ForgeDirection d = conduit.getPrimaryFacing();
  int d1 = d.ordinal();
  float pr = (float) conduit.prevRotation;
  float r = (float) conduit.rotation;
  float rd = r-pr;  
  float rotation = (pr + rd*delta); 
  
  ModelPiece shaft = model.getPiece("shaft");
  ModelPiece shroud = model.getPiece("shroud");
  float[] rot;
  
  float zOffset = 3*0.0625f;  
  for(int i = 0; i < 6; i++)
    {  
    if(!connections[i]){continue;}
    GL11.glPushMatrix();
    rot = rotationMatrix[i];
    if(rot[0]!=0){GL11.glRotatef(rot[0], 1, 0, 0);}
    if(rot[1]!=0){GL11.glRotatef(rot[1], 0, 1, 0);}
    if(rot[2]!=0){GL11.glRotatef(rot[2], 0, 0, 1);}   
    bindTexture(d1==i? outTex : tex);
    shaft.setRotation(0, 0, d1==i ? -rotation : rotation);
    shaft.setPosition(0, 0, connections[i]? 0 : zOffset);
    shaft.render(model.textureWidth(), model.textureHeight());
    bindTexture(tex);
    shroud.render(model.textureWidth(), model.textureHeight());
    GL11.glPopMatrix();
    }
  
  rot = rotationMatrix[d1];
  if(rot[0]!=0){GL11.glRotatef(rot[0], 1, 0, 0);}
  if(rot[1]!=0){GL11.glRotatef(rot[1], 0, 1, 0);}
  if(rot[2]!=0){GL11.glRotatef(rot[2], 0, 0, 1);}   
  bindTexture(tex);
  model.getPiece("gearBox").render(model.textureWidth(), model.textureHeight());
  GL11.glPopMatrix();
//  long t2 = System.nanoTime();
//  AWLog.logDebug("conduit render time: "+(t2-t1));
  }

}
