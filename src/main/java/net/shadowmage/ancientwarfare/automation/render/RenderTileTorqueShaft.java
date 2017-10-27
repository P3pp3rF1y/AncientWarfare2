package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.core.interfaces.ITorque.ITorqueTile;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.io.IOException;

public class RenderTileTorqueShaft extends TileEntitySpecialRenderer {

    private final float[][] gearboxRotationMatrix = new float[6][];
    private final ModelBaseAW model;
    private final ModelPiece inputHead, outputHead, shaft, gearbox;
    private final ResourceLocation[] textures = new ResourceLocation[3];

    public RenderTileTorqueShaft(ResourceLocation light, ResourceLocation med, ResourceLocation heavy) {
        this.textures[0] = light;
        this.textures[1] = med;
        this.textures[2] = heavy;
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/torque_shaft.m2f"));
        inputHead = model.getPiece("southShaft");
        outputHead = model.getPiece("northShaft");
        shaft = model.getPiece("shaft");
        gearbox = model.getPiece("gearBox");

        gearboxRotationMatrix[0] = new float[]{-90, 0, 0};//d
        gearboxRotationMatrix[1] = new float[]{90, 0, 0};//u
        gearboxRotationMatrix[2] = new float[]{0, 0, 0};//n
        gearboxRotationMatrix[3] = new float[]{0, 180, 0};//s
        gearboxRotationMatrix[4] = new float[]{0, 90, 0};//w
        gearboxRotationMatrix[5] = new float[]{0, 270, 0};//e
    }

    @Override
    public void render(TileEntity te, double x, double y, double z, float delta, int destroyStage, float alpha) {
        try {
            model.exportOBJ("torque_shaft.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5d, y + 0.5d, z + 0.5d);

        TileTorqueShaft shaft = (TileTorqueShaft) te;
        EnumFacing d = shaft.getPrimaryFacing();
        float[] rotations = gearboxRotationMatrix[d.ordinal()];
        if (rotations[0] != 0) {
            GlStateManager.rotate(rotations[0], 1, 0, 0);
        }
        if (rotations[1] != 0) {
            GlStateManager.rotate(rotations[1], 0, 1, 0);
        }
        if (rotations[2] != 0) {
            GlStateManager.rotate(rotations[2], 0, 0, 1);
        }

        this.inputHead.setVisible(shaft.prev() == null);
        this.gearbox.setVisible(shaft.prev() == null);
        this.outputHead.setVisible(shaft.next() == null);

        float rotation = shaft.getClientOutputRotation(d, delta);
        this.shaft.setRotation(0, 0, -rotation);
        this.outputHead.setRotation(0, 0, -rotation);

        if (shaft.prev() == null)//no prev shaft, render gearbox and input head at either shaft rpm or input rpm
        {
            ITorqueTile itt = shaft.getTorqueCache()[d.getOpposite().ordinal()];
            if (itt != null && itt.canOutputTorque(d) && itt.useOutputRotation(null)) {
                rotation = itt.getClientOutputRotation(d, delta);
            }
            inputHead.setRotation(0, 0, -rotation);
        }

        bindTexture(textures[te.getBlockMetadata() % textures.length]);
        model.renderModel();
        GlStateManager.popMatrix();
    }

/*
    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5f, 0.5f, 0.5f);

        float[] rotations = gearboxRotationMatrix[1];//render as if facing upwards for items
        if (rotations[0] != 0) {
            GlStateManager.rotate(rotations[0], 1, 0, 0);
        }
        if (rotations[1] != 0) {
            GlStateManager.rotate(rotations[1], 0, 1, 0);
        }
        if (rotations[2] != 0) {
            GlStateManager.rotate(rotations[2], 0, 0, 1);
        }
        this.inputHead.setVisible(true);
        this.gearbox.setVisible(true);
        this.outputHead.setVisible(true);

        this.shaft.setRotation(0, 0, 0);
        this.outputHead.setRotation(0, 0, 0);
        this.inputHead.setRotation(0, 0, 0);

        bindTexture(textures[item.getItemDamage() % textures.length]);
        model.renderModel();
        GlStateManager.popMatrix();
    }
*/


}
