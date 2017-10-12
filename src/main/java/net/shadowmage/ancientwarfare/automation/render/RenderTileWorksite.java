/*
package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockPos;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;

public class RenderTileWorksite extends TileEntitySpecialRenderer {

    public RenderTileWorksite() {

    }

    @Override
    public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
        if(var1 instanceof IBoundedSite) {
            IBoundedSite worksite = (IBoundedSite) var1;
            if (AWAutomationStatics.renderWorkBounds.getBoolean()) {
                RenderTools.setFullColorLightmap();
                GlStateManager.pushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GlStateManager.translate(var2, var4, var6);
                BlockPos min = worksite.getWorkBoundsMin();
                BlockPos max = worksite.getWorkBoundsMax();
                if (max == null) {
                    max = min;
                }
                if (min != null) {
                    renderBoundingBox(var1.x, var1.y, var1.z, min, max, 1.f, 1.f, 1.f, 0.f);
                }
                GlStateManager.popAttrib();
                GlStateManager.popMatrix();
            }
        }
    }

    private void renderBoundingBox(int x, int y, int z, BlockPos min, BlockPos max, float r, float g, float b, float expansion) {
        GlStateManager.disableLighting();
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        AxisAlignedBB bb = new AxisAlignedBB(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        if (expansion != 0.f) {
            bb = bb.expand(expansion, expansion, expansion);
        }
        bb.offset(-x, -y, -z);
        RenderTools.drawOutlinedBoundingBox2(bb, 1.f, 1.f, 1.f, 0.0625f);
        GlStateManager.enableLighting();
    }

}
*/
