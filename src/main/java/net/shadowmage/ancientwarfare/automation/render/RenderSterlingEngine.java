package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import net.shadowmage.ancientwarfare.core.util.Trig;

public class RenderSterlingEngine extends TileEntitySpecialRenderer<TileStirlingGenerator> {

    private final ModelBaseAW model;
    private final ResourceLocation texture;
    private final ModelPiece flywheel, pistonCrank, pistonCrank2, flywheel_arm, piston_arm, piston_arm2;

    public RenderSterlingEngine() {
        ModelLoader loader = new ModelLoader();
        texture = new ResourceLocation("ancientwarfare:textures/model/automation/stirling_generator.png");
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/sterling_engine.m2f"));
        flywheel = model.getPiece("flywheel2");
        pistonCrank = model.getPiece("piston_crank");
        pistonCrank2 = model.getPiece("piston_crank2");

        flywheel_arm = model.getPiece("flywheel_arm");
        piston_arm = model.getPiece("piston_arm");
        piston_arm2 = model.getPiece("piston_arm2");
    }

    @Override
    public void render(TileStirlingGenerator tt, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
/*                try {
            model.exportOBJ("test.obj");
        } catch (IOException e) {

        }*/

        EnumFacing d = tt.getPrimaryFacing();
        float baseRotation = d == EnumFacing.SOUTH ? 180.f : d == EnumFacing.WEST ? 270.f : d == EnumFacing.EAST ? 90.f : 0.f;

        float rotation = -(tt.getClientOutputRotation(d, partialTick));

        GlStateManager.pushMatrix();

//  GlStateManager.enableRescaleNormal();
        GlStateManager.translate(x + 0.5d, y, z + 0.5d);
        GlStateManager.rotate(-baseRotation, 0, 1, 0);
        bindTexture(texture);

        flywheel.setRotation(0, 0, rotation);
        pistonCrank2.setRotation(0, 0, rotation);
        flywheel_arm.setRotation(0, 0, -rotation);

        calculateArmAngle1(-rotation);
        calculateArmAngle2(-rotation - 90);
        pistonCrank.setRotation(0, 0, -rotation);
        piston_arm.setRotation(0, 0, rotation + armAngle);
        piston_arm2.setRotation(0, 0, rotation + armAngle2);
        model.renderModel();
//  GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }

    float pistonPos, armAngle, pistonPos2, armAngle2;

    private void calculateArmAngle1(float crankAngle) {
        float ra = crankAngle * Trig.TORADIANS;
        float crankDistance = 1.f;//side a
        float crankLength = 9.f;//side b
        calculatePistonPosition1(ra, crankDistance, crankLength);
    }

    private void calculatePistonPosition1(float crankAngleRadians, float radius, float length) {
        float cA = MathHelper.cos(crankAngleRadians);
        float sA = MathHelper.sin(crankAngleRadians);
        pistonPos = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

        float bx = sA * radius;
        float by = cA * radius;
        float cx = 0;
        float cy = pistonPos;

        float rlrA = (float) Math.atan2(cx - bx, cy - by);
        armAngle = rlrA * Trig.TODEGREES;
    }

    private void calculateArmAngle2(float crankAngle) {
        float ra = crankAngle * Trig.TORADIANS;
        float crankDistance = 1.f;//side a
        float crankLength = 7.f;//side b
        calculatePistonPosition2(ra, crankDistance, crankLength);
    }

    private void calculatePistonPosition2(float crankAngleRadians, float radius, float length) {
        float cA = MathHelper.cos(crankAngleRadians);
        float sA = MathHelper.sin(crankAngleRadians);
        pistonPos2 = radius * cA + MathHelper.sqrt(length * length - radius * radius * sA * sA);

        float bx = sA * radius;
        float by = cA * radius;
        float cx = 0;
        float cy = pistonPos2;

        float rlrA = (float) Math.atan2(cx - bx, cy - by);
        armAngle2 = rlrA * Trig.TODEGREES;
    }
}
