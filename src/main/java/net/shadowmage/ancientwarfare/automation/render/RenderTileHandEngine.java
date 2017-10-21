package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandCrankedGenerator;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.io.IOException;

public class RenderTileHandEngine extends TileEntitySpecialRenderer<TileHandCrankedGenerator> {

    private final ResourceLocation texture;
    private final ModelBaseAW model;
    private final ModelPiece outputGear, inputGear;

    public RenderTileHandEngine() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/hand_engine.m2f"));
        outputGear = model.getPiece("outputGear");
        inputGear = model.getPiece("inputGear");
        texture = new ResourceLocation("ancientwarfare", "textures/model/automation/hand_engine.png");
    }

    @Override
    public void render(TileHandCrankedGenerator generator, double x, double y, double z, float delta, int destroyStage, float alpha) {
        try {
            model.exportOBJ("hand_generator.obj");
        }
        catch(IOException e) {
            e.printStackTrace();
        }

        GlStateManager.pushMatrix();
        bindTexture(texture);
        GlStateManager.translate(x + 0.5d, y, z + 0.5d);

        float outRotation = -generator.getClientOutputRotation(generator.getPrimaryFacing(), delta);
        float inRotation = -generator.getClientOutputRotation(EnumFacing.UP, delta);//top side, not technically an 'output' rotation, but i'm lazy and not making a new method for it
        renderModel(inRotation, outRotation, generator.getPrimaryFacing().ordinal());
        GlStateManager.popMatrix();
    }

    protected void renderModel(float inR, float outR, int face) {
        float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
        if (rot[0] != 0) {
            GlStateManager.rotate(rot[0], 1, 0, 0);
        }
        else if (rot[1] != 0) {
            GlStateManager.rotate(rot[1], 0, 1, 0);
        }
        outputGear.setRotation(0, 0, outR);
        inputGear.setRotation(0, inR, 0);
        model.renderModel();
    }
}
