package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.vehicle.collision.OBB;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehiclePart;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleTurreted;
import org.lwjgl.opengl.GL11;

public class VehicleBBRender extends Render {

    AxisAlignedBB renderOBB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
    AxisAlignedBB renderAABB = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);

    AxisAlignedBB testCollisionBB = AxisAlignedBB.getBoundingBox(1, 0, 1, 2, 1, 2);

    public VehicleBBRender() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float tick) {
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
            renderAABB.setBB(part.getEntityBoundingBox());
            renderAABB.offset(x1, y1, z1);
            renderAABB.offset(-part.posX, -part.posY, -part.posZ);
            RenderTools.drawOutlinedBoundingBox2(renderAABB, 1, 0, 0, 0.0625f);
        }

        GL11.glColor4f(.75f, .75f, .75f, 0.75f);
        renderAABB.setBB(entity.getEntityBoundingBox());
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
            Vec3d turretOffset = veh.getTurretOffset();
            Vec3d launchVelocity = veh.getLaunchVelocity();
            TrajectoryRender.renderTrajectory(turretOffset.x, turretOffset.y, turretOffset.z, launchVelocity.x, launchVelocity.y, launchVelocity.z);
            GL11.glPopMatrix();
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
