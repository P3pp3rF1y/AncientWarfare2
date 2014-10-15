package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import org.lwjgl.opengl.GL11;

public class VehicleBBRender extends Render
{

AxisAlignedBB renderOBB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
AxisAlignedBB renderAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

AxisAlignedBB testCollisionBB = AxisAlignedBB.getBoundingBox(1, 0, 1, 2, 1, 2);

public VehicleBBRender()
  {
  // TODO Auto-generated constructor stub
  }

@Override
public void doRender(Entity entity, double x, double y,  double z, float yaw, float tick)
  {
  VehicleBase vehicle = (VehicleBase)entity;
  
  GL11.glPushMatrix();
  
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glEnable(GL11.GL_BLEND);
  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
  GL11.glColor4f(.75f, .75f, .75f, 0.75f);
    
  renderAABB.setBB(entity.boundingBox);
  renderAABB.offset(x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
  renderAABB(renderAABB);
    
  GL11.glColor4f(0, 0.75f, 0, 0.75f);
  GL11.glTranslated(x, y+0.5d, z);
  
  renderOBB(vehicle.obb);
  
  if(vehicle.obb.collides(testCollisionBB))
    {
    GL11.glColor4f(1, 0, 0, 0.75f);
    }
  else
    {
    GL11.glColor4f(0, 0.75f, 0, 0.5f);
    }
  
//  GL11.glRotatef(yaw, 0, 1, 0);
//  float hw = entity.width / 2.f;
//  float hl = vehicle.length / 2.f;
//  renderOBB.setBounds(-hw, 0, -hl, hw, entity.height, hl);
//  renderAABB(renderOBB);

  
  renderAABB(testCollisionBB);
  
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_BLEND);  
  GL11.glPopMatrix();
  }

@Override
protected ResourceLocation getEntityTexture(Entity p_110775_1_)
  {
  // TODO Auto-generated method stub
  return null;
  }

public static void renderOBB(OBB bb)
  {  
  Tessellator tessellator = Tessellator.instance;
  tessellator.startDrawingQuads();
  double minY = 0;
  double maxY = bb.height;
  Vec3 c1 = bb.getCorner(0);
  Vec3 c2 = bb.getCorner(1);
  Vec3 c3 = bb.getCorner(2);
  Vec3 c4 = bb.getCorner(3);

  tessellator.addVertex(c1.xCoord, maxY, c1.zCoord);
  tessellator.addVertex(c2.xCoord, maxY, c2.zCoord);
  tessellator.addVertex(c2.xCoord, minY, c2.zCoord);
  tessellator.addVertex(c1.xCoord, minY, c1.zCoord);
  
  tessellator.addVertex(c2.xCoord, maxY, c2.zCoord);
  tessellator.addVertex(c3.xCoord, maxY, c3.zCoord);
  tessellator.addVertex(c3.xCoord, minY, c3.zCoord);
  tessellator.addVertex(c2.xCoord, minY, c2.zCoord);

  tessellator.addVertex(c3.xCoord, maxY, c3.zCoord);
  tessellator.addVertex(c4.xCoord, maxY, c4.zCoord);
  tessellator.addVertex(c4.xCoord, minY, c4.zCoord);
  tessellator.addVertex(c3.xCoord, minY, c3.zCoord);

  tessellator.addVertex(c4.xCoord, maxY, c4.zCoord);
  tessellator.addVertex(c1.xCoord, maxY, c1.zCoord);
  tessellator.addVertex(c1.xCoord, minY, c1.zCoord);
  tessellator.addVertex(c4.xCoord, minY, c4.zCoord);
  
  tessellator.addVertex(c1.xCoord, minY, c1.zCoord);
  tessellator.addVertex(c2.xCoord, minY, c2.zCoord);
  tessellator.addVertex(c3.xCoord, minY, c3.zCoord);
  tessellator.addVertex(c4.xCoord, minY, c4.zCoord);

  tessellator.addVertex(c4.xCoord, maxY, c4.zCoord);
  tessellator.addVertex(c3.xCoord, maxY, c3.zCoord);
  tessellator.addVertex(c2.xCoord, maxY, c2.zCoord);
  tessellator.addVertex(c1.xCoord, maxY, c1.zCoord);
  
  //TODO top/bottom  
  tessellator.draw();
  }

}
