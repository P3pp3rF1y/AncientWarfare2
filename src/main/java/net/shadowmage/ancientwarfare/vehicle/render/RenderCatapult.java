package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehiclePart;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleTurreted;
import org.lwjgl.opengl.GL11;

public class RenderCatapult extends RenderEntity {

    ModelBaseAW model;

    AxisAlignedBB renderOBB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    AxisAlignedBB renderAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

    AxisAlignedBB testCollisionBB = AxisAlignedBB.getBoundingBox(1, 0, 1, 2, 1, 2);

    public RenderCatapult() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/vehicle/catapult.m2f"));
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float delta) {
        GL11.glPushMatrix();
        GL11.glTranslated(x, y, z);
        renderVehicle((VehicleBase) entity, delta);
        GL11.glPopMatrix();
        doRenderBB(entity, x, y, z, yaw, delta);
    }

    private void renderVehicle(VehicleBase vehicle, float delta) {
        float yawD = vehicle.rotationYaw - vehicle.prevRotationYaw;
        float yaw = vehicle.rotationYaw - ((1 - delta) * yawD);
        GL11.glRotatef(yaw, 0, 1, 0);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        model.renderModel();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }


    public void doRenderBB(Entity entity, double x, double y, double z, float yaw, float tick) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            return;
        }
        VehicleBase vehicle = (VehicleBase) entity;

        GL11.glPushMatrix();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        VehiclePart[] parts = vehicle.getParts();

        GL11.glTranslated(x, y, z);

        double x1, y1, z1;
        GL11.glColor4f(0, .75f, 0, 0.75f);

        for (VehiclePart part : parts) {
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
        renderAABB.offset(-entity.posX, -entity.posY, -entity.posZ);
        RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 1, 1, 0.0625f);

        GL11.glRotated(vehicle.rotationYaw, 0, 1, 0);
        renderAABB.setBounds(-vehicle.vehicleWidth / 2.f, 0, -vehicle.vehicleLength / 2.f, vehicle.vehicleWidth / 2.f, vehicle.vehicleHeight, vehicle.vehicleLength / 2.f);
        RenderTools.drawOutlinedBoundingBox2(renderAABB, 0, 1, 0, 0.0625f);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
        if (entity instanceof VehicleTurreted) {
            GL11.glPushMatrix();
            GL11.glTranslated(x, y, z);
            VehicleTurreted veh = (VehicleTurreted) entity;
            Vec3 turretOffset = veh.getTurretOffset();
            Vec3 launchVelocity = veh.getLaunchVelocity();
            TrajectoryRender.renderTrajectory(turretOffset.xCoord, turretOffset.yCoord, turretOffset.zCoord, launchVelocity.xCoord, launchVelocity.yCoord, launchVelocity.zCoord);
            GL11.glPopMatrix();
        }
    }

}
