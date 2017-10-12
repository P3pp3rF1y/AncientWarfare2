package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehiclePart;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleTurreted;
import org.lwjgl.opengl.GL11;

public class VehicleBBRender extends Render {

    AxisAlignedBB renderOBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    AxisAlignedBB renderAABB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);

    AxisAlignedBB testCollisionBB = new AxisAlignedBB(1, 0, 1, 2, 1, 2);

    public VehicleBBRender(RenderManager renderManager) {
        super(renderManager);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
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

    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
        // TODO Auto-generated method stub
        return null;
    }

    public static void renderOBB(OBB bb) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        double minY = 0;
        double maxY = bb.height;
        Vec3d c1 = bb.getCorner(0);
        Vec3d c2 = bb.getCorner(1);
        Vec3d c3 = bb.getCorner(2);
        Vec3d c4 = bb.getCorner(3);

        bufferBuilder.pos(c1.x, maxY, c1.z).endVertex();
        bufferBuilder.pos(c2.x, maxY, c2.z).endVertex();
        bufferBuilder.pos(c2.x, minY, c2.z).endVertex();
        bufferBuilder.pos(c1.x, minY, c1.z).endVertex();

        bufferBuilder.pos(c2.x, maxY, c2.z).endVertex();
        bufferBuilder.pos(c3.x, maxY, c3.z).endVertex();
        bufferBuilder.pos(c3.x, minY, c3.z).endVertex();
        bufferBuilder.pos(c2.x, minY, c2.z).endVertex();

        bufferBuilder.pos(c3.x, maxY, c3.z).endVertex();
        bufferBuilder.pos(c4.x, maxY, c4.z).endVertex();
        bufferBuilder.pos(c4.x, minY, c4.z).endVertex();
        bufferBuilder.pos(c3.x, minY, c3.z).endVertex();

        bufferBuilder.pos(c4.x, maxY, c4.z).endVertex();
        bufferBuilder.pos(c1.x, maxY, c1.z).endVertex();
        bufferBuilder.pos(c1.x, minY, c1.z).endVertex();
        bufferBuilder.pos(c4.x, minY, c4.z).endVertex();

        bufferBuilder.pos(c1.x, minY, c1.z).endVertex();
        bufferBuilder.pos(c2.x, minY, c2.z).endVertex();
        bufferBuilder.pos(c3.x, minY, c3.z).endVertex();
        bufferBuilder.pos(c4.x, minY, c4.z).endVertex();

        bufferBuilder.pos(c4.x, maxY, c4.z).endVertex();
        bufferBuilder.pos(c3.x, maxY, c3.z).endVertex();
        bufferBuilder.pos(c2.x, maxY, c2.z).endVertex();
        bufferBuilder.pos(c1.x, maxY, c1.z).endVertex();

        tessellator.draw();
    }

}
