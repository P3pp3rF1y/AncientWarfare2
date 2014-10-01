package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileFlywheelStorage extends TileEntitySpecialRenderer
{

ModelBaseAW cube, smallModel, largeModel;
ResourceLocation tex;
ResourceLocation smallTex[] = new ResourceLocation[3];
ResourceLocation largeTex[] = new ResourceLocation[3];

ModelPiece spindleSmall;
ModelPiece upperShroudSmall;
ModelPiece lowerShroudSmall;
ModelPiece flywheelExtensionSmall;
ModelPiece spindleLarge;
ModelPiece upperShroudLarge;
ModelPiece lowerShroudLarge;
ModelPiece flywheelExtensionLarge;

public RenderTileFlywheelStorage(ResourceLocation tex)
  {
  ModelLoader loader = new ModelLoader();
  cube = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/cube.m2f"));
  this.tex = tex;
  
  smallTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  smallTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  smallTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
  
  largeTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  largeTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  largeTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
  
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
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta)
  {
  TileFlywheelStorage storage = (TileFlywheelStorage)te;
  if(storage.controllerPos==null)
    {
    GL11.glPushMatrix();
    GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
    bindTexture(tex);
    cube.renderModel();
    GL11.glPopMatrix();
    }
  else if(storage.isControl)
    {
    GL11.glPushMatrix();
    float rotation = (float)getRotation(storage.rotation, storage.prevRotation, delta);
    if(storage.setType>=0 && storage.setHeight>0)
      {
      GL11.glTranslated(x+0.5d, y, z+0.5d);
      if(storage.setWidth>1)
        {
        renderLargeModel(storage.setType, storage.setHeight, rotation);
        }
      else
        {
        renderSmallModel(storage.setType, storage.setHeight, rotation);
        }
      }  
    GL11.glPopMatrix();  
    }
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
