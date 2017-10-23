package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.io.IOException;

public class RenderWindmillControl extends TileEntitySpecialRenderer {

    private final ResourceLocation texture;
    private final ModelBaseAW model;
    private final ModelPiece outputGear;

    public RenderWindmillControl() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/windmill_control.m2f"));
        texture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_generator.png");
        outputGear = model.getPiece("outputGear");
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float delta, int destroyStage, float alpha) {
        try {
            model.exportOBJ("windmill_generator.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        GlStateManager.pushMatrix();
        bindTexture(texture);
        GlStateManager.translate(x + 0.5d, y, z + 0.5d);

        TileTorqueBase windmillControl = (TileTorqueBase) te;
        float outRotation = -windmillControl.getClientOutputRotation(windmillControl.getPrimaryFacing(), delta);
        renderModel(outRotation, windmillControl.getPrimaryFacing().ordinal());
        GlStateManager.popMatrix();
    }

    protected void renderModel(float outR, int face) {
        float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
        if (rot[0] != 0) {
            GlStateManager.rotate(rot[0], 1, 0, 0);
        }
        else if (rot[1] != 0) {
            GlStateManager.rotate(rot[1], 0, 1, 0);
        }
        outputGear.setRotation(0, 0, outR);
        model.renderModel();
    }
}
