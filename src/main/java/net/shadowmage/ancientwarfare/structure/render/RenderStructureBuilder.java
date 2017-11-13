package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;
import org.lwjgl.opengl.GL11;

public class RenderStructureBuilder extends TileEntitySpecialRenderer<TileStructureBuilder> {

    @Override
    public void render(TileStructureBuilder builder, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        RenderTools.setFullColorLightmap();
        if (builder.clientBB != null) {
            GlStateManager.pushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GlStateManager.translate(x, y, z);
            BlockPos min = builder.clientBB.min;
            BlockPos max = builder.clientBB.max;
            if (max == null) {
                max = min;
            }
            if (min != null) {
                renderBoundingBox(builder.getPos(), min, max, 1.f, 1.f, 1.f, 0.f);
            }
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    private void renderBoundingBox(BlockPos pos, BlockPos min, BlockPos max, float r, float g, float b, float expansion) {
        GlStateManager.disableLighting();
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        AxisAlignedBB bb = new AxisAlignedBB(min, max.add(1,1,1 ));
        if (expansion != 0.f) {
            bb = bb.expand(expansion, expansion, expansion);
        }
        bb = bb.offset(-pos.getX(), - pos.getY(), -pos.getZ());
        RenderTools.drawOutlinedBoundingBox2(bb, 1.f, 1.f, 1.f, 0.0625f);
        GlStateManager.enableLighting();
    }
}
