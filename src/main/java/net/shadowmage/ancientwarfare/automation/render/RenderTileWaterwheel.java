package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheel;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelLoader;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderTileWaterwheel extends TileEntitySpecialRenderer implements IItemRenderer {

    private float[][] gearboxRotationMatrix = new float[6][];
    ModelBaseAW model;
    ModelPiece waterwheel, outputGear;
    ResourceLocation tex;

    public RenderTileWaterwheel() {
        this.tex = new ResourceLocation("ancientwarfare", "textures/model/automation/waterwheel.png");
        ModelLoader loader = new ModelLoader();
        model = loader.loadModel(getClass().getResourceAsStream("/assets/ancientwarfare/models/automation/waterwheel.m2f"));
        waterwheel = model.getPiece("waterwheelSpindle");
        outputGear = model.getPiece("outputGear");
        gearboxRotationMatrix[0] = new float[]{-90, 0, 0};//d
        gearboxRotationMatrix[1] = new float[]{90, 0, 0};//u
        gearboxRotationMatrix[2] = new float[]{0, 0, 0};//n
        gearboxRotationMatrix[3] = new float[]{0, 180, 0};//s
        gearboxRotationMatrix[4] = new float[]{0, 90, 0};//w
        gearboxRotationMatrix[5] = new float[]{0, 270, 0};//e
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick) {
        GL11.glPushMatrix();
        TileWaterwheel wheel = (TileWaterwheel) te;

        ForgeDirection d = wheel.getPrimaryFacing();
        GL11.glTranslated(x + 0.5d, y, z + 0.5d);

        float rot[] = gearboxRotationMatrix[d.ordinal()];
        if (rot[0] != 0) {
            GL11.glRotatef(rot[0], 1, 0, 0);
        }
        if (rot[1] != 0) {
            GL11.glRotatef(rot[1], 0, 1, 0);
        }
        if (rot[2] != 0) {
            GL11.glRotatef(rot[2], 0, 0, 1);
        }
        bindTexture(tex);

        waterwheel.setRotation(0, 0, (float) getRotation(wheel.wheelRotation, wheel.prevWheelRotation, partialTick));
        waterwheel.setVisible(wheel.validSetup);
        outputGear.setRotation(0, 0, -wheel.getClientOutputRotation(wheel.getPrimaryFacing(), partialTick));
        model.renderModel();

        GL11.glPopMatrix();
    }

    private double getRotation(double rotation, double prevRotation, float delta) {
        double rd = rotation - prevRotation;
        return (prevRotation + rd * delta);
    }

//private void drawPointAtCurrentOrigin()
//  {
//  //debug point rendering
//  GL11.glDisable(GL11.GL_TEXTURE_2D);
//  GL11.glDisable(GL11.GL_LIGHTING);
//  GL11.glColor4f(1.f, 0.f, 0.f, 1.f);
//  GL11.glPointSize(10.f);
//  GL11.glBegin(GL11.GL_POINTS);
//  GL11.glVertex3f((float)0, (float)0, (float)0);
//  GL11.glEnd();
//  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
//  GL11.glEnable(GL11.GL_TEXTURE_2D);
//  GL11.glEnable(GL11.GL_LIGHTING);
//  }

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
        GL11.glPushMatrix();
        GL11.glTranslatef(0.5f, 0, 0.5f);
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        GL11.glPushAttrib(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        bindTexture(tex);
        waterwheel.setRotation(0, 0, 0);
        outputGear.setRotation(0, 0, 0);
        model.renderModel();
        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

}
