package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelController;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.io.IOException;

public class RenderTileTorqueFlywheelController extends TileEntitySpecialRenderer {

    private float[][] gearboxRotationMatrix = new float[6][];
    private final ModelBaseAW controllerModel;
    private final ModelPiece controlInput, controlOutput, controlSpindle;

    private final ResourceLocation texture[] = new ResourceLocation[3];//1, tex2, tex3;

    public RenderTileTorqueFlywheelController() {
        texture[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_light.png");
        texture[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_medium.png");
        texture[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_controller_heavy.png");

        ModelLoader loader = new ModelLoader();
        controllerModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_controller.m2f"));
        try {
            controllerModel.exportOBJ("flywheel_controller.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }
        controlInput = controllerModel.getPiece("inputGear");
        controlOutput = controllerModel.getPiece("outputGear");
        controlSpindle = controllerModel.getPiece("spindle");

        gearboxRotationMatrix[0] = new float[]{-90, 0, 0};//d
        gearboxRotationMatrix[1] = new float[]{90, 0, 0};//u
        gearboxRotationMatrix[2] = new float[]{0, 0, 0};//n
        gearboxRotationMatrix[3] = new float[]{0, 180, 0};//s
        gearboxRotationMatrix[4] = new float[]{0, 90, 0};//w
        gearboxRotationMatrix[5] = new float[]{0, 270, 0};//e

    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float delta, int destroyStage, float alpha) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5d, y, z + 0.5d);

        TileFlywheelController flywheel = (TileFlywheelController) te;

        ITorqueTile[] neighbors = flywheel.getTorqueCache();
        EnumFacing d = flywheel.getPrimaryFacing();

        float outputRotation = flywheel.getClientOutputRotation(d, delta);
        float inputRotation = outputRotation;
        float flywheelRotation = flywheel.getFlywheelRotation(delta);

        ITorqueTile inputNeighbor = neighbors[d.getOpposite().ordinal()];
        if (inputNeighbor != null && inputNeighbor.canOutputTorque(d) && inputNeighbor.useOutputRotation(d.getOpposite())) {
            inputRotation = inputNeighbor.getClientOutputRotation(d.getOpposite(), delta);
        }

        bindTexture(texture[te.getBlockMetadata() % texture.length]);
        renderModel(outputRotation, inputRotation, flywheelRotation, d.ordinal());
        GlStateManager.popMatrix();
    }

    protected void renderModel(float outR, float inR, float wheelR, int face) {
        float[] rot = gearboxRotationMatrix[face];
        if (rot[0] != 0) {
            GlStateManager.rotate(rot[0], 1, 0, 0);
        }
        if (rot[1] != 0) {
            GlStateManager.rotate(rot[1], 0, 1, 0);
        }
        if (rot[2] != 0) {
            GlStateManager.rotate(rot[2], 0, 0, 1);
        }
        controlInput.setRotation(0, 0, -inR);
        controlOutput.setRotation(0, 0, -outR);
        controlSpindle.setRotation(0, -wheelR, 0);
        controllerModel.renderModel();
    }
}
