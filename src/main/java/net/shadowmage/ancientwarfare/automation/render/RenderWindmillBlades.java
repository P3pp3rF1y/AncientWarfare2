/*
package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderWindmillBlades extends TileEntitySpecialRenderer implements IItemRenderer {

    private final ResourceLocation texture, cubeTexture;
    private final ModelBaseAW model, cube;
    private final ModelPiece windmillShaft, blade, bladeJoint, bladeShaft;

    public RenderWindmillBlades() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/windmill_blade.m2f"));
        cube = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/cube.m2f"));
        windmillShaft = model.getPiece("windmillShaft");
        blade = model.getPiece("blade");
        bladeJoint = model.getPiece("bladeJoint");
        bladeShaft = model.getPiece("bladeShaft");
        texture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_blade.png");
        cubeTexture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_blade_block.png");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta) {
        TileWindmillBlade blade = (TileWindmillBlade) te;
        if (blade.isControl) {
            GlStateManager.pushMatrix();
            bindTexture(texture);
            GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d);
            renderModel(-blade.getRotation(delta), blade.windmillDirection, (blade.windmillSize - 1) / 2);
            GlStateManager.popMatrix();
        } else if (blade.controlPos == null) {
            GlStateManager.pushMatrix();
            bindTexture(cubeTexture);
            GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d);
            cube.renderModel();
            GlStateManager.popMatrix();
        }
    }

    protected void renderModel(float bladeRotatation, int face, int height) {
        float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
        if (rot[0] != 0) {
            GlStateManager.rotate(rot[0], 1, 0, 0);
        }
        else if (rot[1] != 0) {
            GlStateManager.rotate(rot[1], 0, 1, 0);
        }

        float textureWidth = model.textureWidth();
        float textureHeight = model.textureHeight();
        GlStateManager.rotate(bladeRotatation, 0, 0, 1);
        windmillShaft.render(model.textureWidth(), model.textureHeight());

        for (int i = 0; i < 4; i++) {
            GlStateManager.rotate(90, 0, 0, 1);
            bladeShaft.render(textureWidth, textureHeight);
            for (int k = 1; k < height; k++) {
                blade.render(textureWidth, textureHeight);
                if (k == height - 1) {
                    bladeJoint.render(textureWidth, textureHeight);
                }
                else {
                    GlStateManager.translate(0, 1, 0);
                }
            }
            GlStateManager.translate(0, 2 - height, 0);
        }
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GlStateManager.pushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        bindTexture(texture);
        GlStateManager.translate(0.5f, -0.25f, 0.5f);
        GlStateManager.enableRescaleNormal();
        GlStateManager.scale(0.35f, 0.35f, 0.35f);

        float textureWidth = model.textureWidth();
        float textureHeight = model.textureHeight();
        windmillShaft.setRotation(0, 0, 0);
        windmillShaft.render(textureWidth, textureHeight);
        int height = 5;

        bladeShaft.render(textureWidth, textureHeight);
        for (int k = 1; k < height; k++) {
            blade.render(textureWidth, textureHeight);
            if (k == height - 1) {
                bladeJoint.render(textureWidth, textureHeight);
            }
            else {
                GlStateManager.translate(0, 1, 0);
            }
        }
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

}
*/
