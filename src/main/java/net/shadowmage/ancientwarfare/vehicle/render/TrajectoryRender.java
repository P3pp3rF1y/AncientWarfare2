package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.renderer.GlStateManager;
import net.shadowmage.ancientwarfare.core.util.Trig;
import org.lwjgl.opengl.GL11;


public class TrajectoryRender {

    public static void renderTrajectory(double dx, double dy, double dz, double vx, double vy, double vz) {
        GlStateManager.color(1.f, 0, 0, 1.f);
        GlStateManager.glBegin(GL11.GL_LINE_STRIP);
        double x = 0, y = 0, z = 0;//position
        double my = vy;
        GL11.glVertex3d(x + dx, y + dy, z + dz);
        do {
            x += vx;
            y += my;
            z += vz;
            GL11.glVertex3d(x + dx, y + dy, z + dz);
            my -= Trig.gravityTick;
        }
        while (y > 0);
        GlStateManager.glEnd();
    }

}
