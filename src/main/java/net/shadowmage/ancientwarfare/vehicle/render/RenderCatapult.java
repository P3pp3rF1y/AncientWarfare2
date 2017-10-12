package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
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

    AxisAlignedBB renderOBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    AxisAlignedBB renderAABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    AxisAlignedBB testCollisionBB = new AxisAlignedBB(1, 0, 1, 2, 1, 2);

    public RenderCatapult(RenderManager renderManager) {
        super(renderManager);
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/vehicle/catapult.m2f"));
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float delta) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderVehicle((VehicleBase) entity, delta);
        GlStateManager.popMatrix();
        doRenderBB(entity, x, y, z, yaw, delta);
    }

    private void renderVehicle(VehicleBase vehicle, float delta) {
        float yawD = vehicle.rotationYaw - vehicle.prevRotationYaw;
        float yaw = vehicle.rotationYaw - ((1 - delta) * yawD);
        GlStateManager.rotate(yaw, 0, 1, 0);
        GlStateManager.disableTexture2D();
        model.renderModel();
        GlStateManager.enableTexture2D();
    }


    public void doRenderBB(Entity entity, double x, double y, double z, float yaw, float tick) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            return;
        }
        VehicleBase vehicle = (VehicleBase) entity;

        GlStateManager.pushMatrix();

        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        VehiclePart[] parts = vehicle.getParts();

        GlStateManager.translate(x, y, z);

        double x1, y1, z1;
        GlStateManager.color(0, .75f, 0, 0.75f);

        for (VehiclePart part : parts) {
            x1 = part.posX - entity.posX;
            y1 = part.posY - entity.posY;
            z1 = part.posZ - entity.posZ;
            renderAABB = part.getEntityBoundingBox().offset(x1, y1, z1);
            renderAABB = renderAABB.offset(-part.posX, -part.posY, -part.posZ);
            RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 0, 0, 0.0625f);
        }

        GlStateManager.color(.75f, .75f, .75f, 0.75f);
        renderAABB = entity.getEntityBoundingBox().offset(-entity.posX, -entity.posY, -entity.posZ);
        RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 1, 1, 0.0625f);

        GL11.glRotated(vehicle.rotationYaw, 0, 1, 0);
        renderAABB = new AxisAlignedBB(-vehicle.vehicleWidth / 2.f, 0, -vehicle.vehicleLength / 2.f, vehicle.vehicleWidth / 2.f, vehicle.vehicleHeight, vehicle.vehicleLength / 2.f);
        RenderTools.drawOutlinedBoundingBox2(renderAABB, 0, 1, 0, 0.0625f);

        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        if (entity instanceof VehicleTurreted) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            VehicleTurreted veh = (VehicleTurreted) entity;
            Vec3d turretOffset = veh.getTurretOffset();
            Vec3d launchVelocity = veh.getLaunchVelocity();
            TrajectoryRender.renderTrajectory(turretOffset.x, turretOffset.y, turretOffset.z, launchVelocity.x, launchVelocity.y, launchVelocity.z);
            GlStateManager.popMatrix();
        }
    }

}
