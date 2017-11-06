package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.io.IOException;

public class RenderTileFlywheelStorage extends TileEntitySpecialRenderer {

    private final ModelBaseAW smallModel, largeModel;
    private final ResourceLocation smallTex[] = new ResourceLocation[3], largeTex[] = new ResourceLocation[3];

    private final ModelPiece spindleSmall, upperShroudSmall, lowerShroudSmall, flywheelExtensionSmall, lowerWindowSmall, upperWindowSmall, caseBarsSmall;
    private final ModelPiece spindleLarge, upperShroudLarge, lowerShroudLarge, flywheelExtensionLarge, lowerWindowLarge, upperWindowLarge, caseBarsLarge;

    public RenderTileFlywheelStorage() {
        ModelLoader loader = new ModelLoader();

        smallTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_light.png");
        smallTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_medium.png");
        smallTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_small_heavy.png");

        largeTex[0] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_light.png");
        largeTex[1] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_medium.png");
        largeTex[2] = new ResourceLocation("ancientwarfare", "textures/model/automation/flywheel_large_heavy.png");

        smallModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_small.m2f"));
        spindleSmall = smallModel.getPiece("spindle");
        upperShroudSmall = smallModel.getPiece("shroudUpper");
        lowerShroudSmall = smallModel.getPiece("shroudLower");
        flywheelExtensionSmall = smallModel.getPiece("flywheelExtension");
        lowerWindowSmall = smallModel.getPiece("windowLower");
        upperWindowSmall = smallModel.getPiece("windowUpper");
        caseBarsSmall = smallModel.getPiece("caseBars");


        largeModel = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/flywheel_large.m2f"));
        spindleLarge = largeModel.getPiece("spindle");
        upperShroudLarge = largeModel.getPiece("shroudUpper");
        lowerShroudLarge = largeModel.getPiece("shroudLower");
        flywheelExtensionLarge = largeModel.getPiece("flywheelExtension");
        lowerWindowLarge = largeModel.getPiece("windowLower");
        upperWindowLarge = largeModel.getPiece("windowUpper");
        caseBarsLarge = largeModel.getPiece("caseBars");

        try {
            smallModel.exportOBJ("flywheel_small.obj");
            largeModel.exportOBJ("flywheel_large.obj");
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float delta, int destroyStage, float alpha) {
        TileFlywheelStorage storage = (TileFlywheelStorage) te;
        if (storage.controllerPos == null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5d, y, z + 0.5d);
            renderSmallModel(te.getBlockMetadata(), 1, 0, MinecraftForgeClient.getRenderPass());
            GlStateManager.popMatrix();
        } else if (storage.isControl) {
            int pass = MinecraftForgeClient.getRenderPass();
            GlStateManager.pushMatrix();
            float rotation = (float) getRotation(storage.rotation, storage.lastRotationDiff, delta);
            if (storage.setHeight > 0) {
                GlStateManager.translate(x + 0.5d, y, z + 0.5d);
                if (storage.setWidth > 1) {
                    renderLargeModel(te.getBlockMetadata(), storage.setHeight, -rotation, pass);
                } else {
                    renderSmallModel(te.getBlockMetadata(), storage.setHeight, -rotation, pass);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    protected void renderSmallModel(int type, int height, float rotation, int pass) {
        bindTexture(smallTex[type]);
        GlStateManager.pushMatrix();
        if (pass == 0) {
            spindleSmall.setRotation(0, rotation, 0);
            spindleSmall.setVisible(true);
            caseBarsSmall.setVisible(true);
            upperWindowSmall.setVisible(false);
            lowerWindowSmall.setVisible(false);
        } else {
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 0.25f);
            caseBarsSmall.setVisible(false);
            spindleSmall.setVisible(false);
            lowerWindowSmall.setVisible(true);
            flywheelExtensionSmall.setVisible(false);
            upperShroudSmall.setVisible(false);
            lowerShroudSmall.setVisible(false);
        }
        for (int i = 0; i < height; i++) {
            if (pass == 0) {
                flywheelExtensionSmall.setVisible(i < height - 1);//at every level less than highest
                upperShroudSmall.setVisible(i == height - 1);//at highest level
                lowerShroudSmall.setVisible(i == 0);//at ground level
                smallModel.renderModel();
            } else {
                upperWindowSmall.setVisible(i < height - 1);
                smallModel.renderModel();
            }
            GlStateManager.translate(0, 1, 0);
        }
        if (pass == 1) {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
    }

    protected void renderLargeModel(int type, int height, float rotation, int pass) {
        bindTexture(largeTex[type]);
        GlStateManager.pushMatrix();
        if (pass == 0) {
            spindleLarge.setRotation(0, rotation, 0);
            spindleLarge.setVisible(true);
            caseBarsLarge.setVisible(true);
            upperWindowLarge.setVisible(false);
            lowerWindowLarge.setVisible(false);
        } else {
            GlStateManager.enableBlend();
            GlStateManager.color(1, 1, 1, 0.25f);
            caseBarsLarge.setVisible(false);
            spindleLarge.setVisible(false);
            lowerWindowLarge.setVisible(true);
            flywheelExtensionLarge.setVisible(false);
            upperShroudLarge.setVisible(false);
            lowerShroudLarge.setVisible(false);
        }
        for (int i = 0; i < height; i++) {
            if (pass == 0) {
                flywheelExtensionLarge.setVisible(i < height - 1);//at every level less than highest
                upperShroudLarge.setVisible(i == height - 1);//at highest level
                lowerShroudLarge.setVisible(i == 0);//at ground level
                largeModel.renderModel();
            } else {
                upperWindowLarge.setVisible(i < height - 1);
                largeModel.renderModel();
            }
            GlStateManager.translate(0, 1, 0);
        }
        if (pass == 1) {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.disableBlend();
        }
        GlStateManager.popMatrix();
    }

    private double getRotation(double rotation, double prevRotation, float delta) {
        double rd = rotation - prevRotation;
        return (prevRotation + rd * delta);
    }
}
