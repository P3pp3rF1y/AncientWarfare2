/*
package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
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
                GL11.glPushMatrix();
                GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
                GL11.glTranslated(var2, var4, var6);
                BlockPosition min = worksite.getWorkBoundsMin();
                BlockPosition max = worksite.getWorkBoundsMax();
                if (max == null) {
                    max = min;
                }
                if (min != null) {
                    renderBoundingBox(var1.xCoord, var1.yCoord, var1.zCoord, min, max, 1.f, 1.f, 1.f, 0.f);
                }
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }

    private void renderBoundingBox(int x, int y, int z, BlockPosition min, BlockPosition max, float r, float g, float b, float expansion) {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
        AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(min.x, min.y, min.z, max.x + 1, max.y + 1, max.z + 1);
        if (expansion != 0.f) {
            bb = bb.expand(expansion, expansion, expansion);
        }
        bb.offset(-x, -y, -z);
        RenderTools.drawOutlinedBoundingBox2(bb, 1.f, 1.f, 1.f, 0.0625f);
        GL11.glEnable(GL11.GL_LIGHTING);
    }

}
*/
