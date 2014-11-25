package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import org.lwjgl.opengl.GL11;

public class RenderCatapult extends RenderEntity
{

ModelBaseAW model;

public RenderCatapult()
  {
  ModelLoader loader = new ModelLoader();
  model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/vehicle/catapult.m2f"));
  }

@Override
public void doRender(Entity entity, double x, double y, double z, float yaw, float delta)
  {
  GL11.glPushMatrix();
  GL11.glTranslated(x, y, z);
  renderVehicle((VehicleBase) entity, delta);  
  GL11.glPopMatrix();
  }
  
private void renderVehicle(VehicleBase vehicle, float delta)
  {
  float yawD = vehicle.rotationYaw - vehicle.prevRotationYaw;
  float yaw = vehicle.rotationYaw - (delta * yawD);
  GL11.glRotatef(yaw, 0, 1, 0);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  model.renderModel();
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  }

}
