package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import org.lwjgl.opengl.GL11;

public class RenderTileTorqueShaft extends TileEntitySpecialRenderer implements IItemRenderer
{

private float[][] gearboxRotationMatrix = new float[6][];
private ModelBaseAW model = null;
ModelPiece inputHead, outputHead, shaft, gearbox;
ResourceLocation[] textures = new ResourceLocation[3];

public RenderTileTorqueShaft(ResourceLocation light, ResourceLocation med, ResourceLocation heavy)
  {
  this.textures[0] = light;
  this.textures[1] = med;
  this.textures[2] = heavy;
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
  
  TileTorqueShaft shaft = (TileTorqueShaft)te;
  ForgeDirection d = shaft.getPrimaryFacing();
  float[] rotations = gearboxRotationMatrix[d.ordinal()];
  if(rotations[0]!=0){GL11.glRotatef(rotations[0], 1, 0, 0);}
  if(rotations[1]!=0){GL11.glRotatef(rotations[1], 0, 1, 0);}
  if(rotations[2]!=0){GL11.glRotatef(rotations[2], 0, 0, 1);}
  
  this.inputHead.setVisible(shaft.prev()==null);
  this.gearbox.setVisible(shaft.prev()==null);
  this.outputHead.setVisible(shaft.next()==null);
  
  float rotation = shaft.getClientOutputRotation(d, delta);
  this.shaft.setRotation(0, 0, -rotation);
  this.outputHead.setRotation(0, 0, -rotation);   
  
  if(shaft.prev()==null)//no prev shaft, render gearbox and input head at either shaft rpm or input rpm
    {
    ITorqueTile itt = shaft.getTorqueCache()[d.getOpposite().ordinal()];
    if(itt!=null && itt.canOutputTorque(d) && itt.useOutputRotation(null))
      {
      rotation = itt.getClientOutputRotation(d, delta);
      }
    inputHead.setRotation(0, 0, -rotation);
    }
  
  bindTexture(textures[te.getBlockMetadata()]);
  model.renderModel();  
  GL11.glPopMatrix();
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
  GL11.glTranslatef(0.5f, 0.5f, 0.5f);

  float[] rotations = gearboxRotationMatrix[1];//render as if facing upwards for items
  if(rotations[0]!=0){GL11.glRotatef(rotations[0], 1, 0, 0);}
  if(rotations[1]!=0){GL11.glRotatef(rotations[1], 0, 1, 0);}
  if(rotations[2]!=0){GL11.glRotatef(rotations[2], 0, 0, 1);}
  this.inputHead.setVisible(true);
  this.gearbox.setVisible(true);
  this.outputHead.setVisible(true);
  
  this.shaft.setRotation(0, 0, 0);
  this.outputHead.setRotation(0, 0, 0);   
  this.inputHead.setRotation(0, 0, 0);

  bindTexture(textures[item.getItemDamage()]);
  model.renderModel();
  GL11.glPopMatrix();
  }


}
