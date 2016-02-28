package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import org.lwjgl.opengl.GL11;

public class RenderWindmillControl extends TileEntitySpecialRenderer implements IItemRenderer {

    private final ResourceLocation texture;
    private final ModelBaseAW model;
    private final ModelPiece outputGear;

    public RenderWindmillControl() {
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/windmill_control.m2f"));
        texture = new ResourceLocation("ancientwarfare", "textures/model/automation/windmill_control.png");
        outputGear = model.getPiece("outputGear");
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float delta) {
        GL11.glPushMatrix();
        bindTexture(texture);
        GL11.glTranslated(x + 0.5d, y, z + 0.5d);

        TileTorqueBase windmillControl = (TileTorqueBase) te;
        float outRotation = -windmillControl.getClientOutputRotation(windmillControl.getPrimaryFacing(), delta);
        renderModel(outRotation, windmillControl.getPrimaryFacing().ordinal());
        GL11.glPopMatrix();
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        bindTexture(texture);
        GL11.glTranslated(0.5d, 0, 0.5d);
        renderModel(0, 2);
        GL11.glPopMatrix();
    }

    protected void renderModel(float outR, int face) {
        float[] rot = ITorque.forgeDiretctionToRotationMatrix[face];
        if (rot[0] != 0) {
            GL11.glRotatef(rot[0], 1, 0, 0);
        }
        else if (rot[1] != 0) {
            GL11.glRotatef(rot[1], 0, 1, 0);
        }
        outputGear.setRotation(0, 0, outR);
        model.renderModel();
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

}
