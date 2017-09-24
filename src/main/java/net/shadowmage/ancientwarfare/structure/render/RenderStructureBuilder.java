package net.shadowmage.ancientwarfare.structure.render;

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
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glTranslated(x, y, z);
            BlockPos min = builder.clientBB.min;
            BlockPos max = builder.clientBB.max;
            if (max == null) {
                max = min;
            }
            if (min != null) {
                renderBoundingBox(builder.getPos(), min, max, 1.f, 1.f, 1.f, 0.f);
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }

    private void renderBoundingBox(BlockPos pos, BlockPos min, BlockPos max, float r, float g, float b, float expansion) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
        AxisAlignedBB bb = new AxisAlignedBB(min, max.add(1,1,1 ));
        if (expansion != 0.f) {
            bb = bb.expand(expansion, expansion, expansion);
        }
        bb.offset(-pos.getX(), - pos.getY(), -pos.getZ());
        RenderTools.drawOutlinedBoundingBox2(bb, 1.f, 1.f, 1.f, 0.0625f);
        GL11.glEnable(GL11.GL_LIGHTING);
    }
}
