package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import org.lwjgl.opengl.GL11;

public class VehicleBBRender extends Render
{

AxisAlignedBB renderOBB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
AxisAlignedBB renderAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

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
  GL11.glEnable(GL11.GL_ALPHA_TEST);
  GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
  GL11.glColor4f(1, 1, 1, 0.5f);
    
  renderAABB.setBB(entity.boundingBox);
  renderAABB.offset(x - entity.lastTickPosX, y - entity.lastTickPosY, z - entity.lastTickPosZ);
  renderAABB(renderAABB);
    
  GL11.glColor4f(0, 1, 0, 0.5f);
  GL11.glTranslated(x, y, z);
  
  GL11.glRotatef(yaw, 0, 1, 0);
  float hw = entity.width / 2.f;
  float hl = vehicle.length / 2.f;
  renderOBB.setBounds(-hw, 0, -hl, hw, entity.height, hl);
  renderAABB(renderOBB);
  
  GL11.glDisable(GL11.GL_ALPHA_TEST);
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

}
