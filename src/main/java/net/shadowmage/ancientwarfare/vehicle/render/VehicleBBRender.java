package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehiclePart;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleTurreted;

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
  if(MinecraftForgeClient.getRenderPass()==0){return;}
  VehicleBase vehicle = (VehicleBase)entity;
  
  GL11.glPushMatrix();
  
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glEnable(GL11.GL_BLEND);
  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    
  VehiclePart[] parts = vehicle.getParts();
  
  GL11.glTranslated(x, y, z);
    
  double x1, y1, z1;
  GL11.glColor4f(0, .75f, 0, 0.75f);

  for(VehiclePart part : parts)
    {
    x1 = part.posX - entity.posX;
    y1 = part.posY - entity.posY;
    z1 = part.posZ - entity.posZ;
    renderAABB.setBB(part.boundingBox);
    renderAABB.offset(x1, y1, z1);
    renderAABB.offset(-part.posX, -part.posY, -part.posZ);
    RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 0, 0, 0.0625f);
    }    

  GL11.glColor4f(.75f, .75f, .75f, 0.75f);
  renderAABB.setBB(entity.boundingBox);
  renderAABB.offset(-entity.posX, -entity.posY,  -entity.posZ);
  RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 1, 1, 0.0625f);
  
  GL11.glRotated(vehicle.rotationYaw, 0, 1, 0);
  renderAABB.setBounds(-vehicle.vehicleWidth/2.f, 0, -vehicle.vehicleLength/2.f, vehicle.vehicleWidth/2.f, vehicle.vehicleHeight, vehicle.vehicleLength/2.f);
  RenderTools.drawOutlinedBoundingBox2(renderAABB, 0, 1, 0, 0.0625f);
  
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_BLEND);  
  GL11.glPopMatrix();
  if(entity instanceof VehicleTurreted)
    {
    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);
    VehicleTurreted veh = (VehicleTurreted)entity;
    Vec3 turretOffset = veh.getTurretOffset();
    Vec3 launchVelocity = veh.getLaunchVelocity();
    TrajectoryRender.renderTrajectory(turretOffset.xCoord, turretOffset.yCoord, turretOffset.zCoord, launchVelocity.xCoord, launchVelocity.yCoord, launchVelocity.zCoord);
    GL11.glPopMatrix();
    }
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
  
  tessellator.draw();
  }

}
