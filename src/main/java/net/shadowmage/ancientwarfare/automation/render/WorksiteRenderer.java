package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import org.lwjgl.opengl.GL11;

public class WorksiteRenderer extends TileEntitySpecialRenderer<TileWorksiteBase> {

	public WorksiteRenderer() {

	}

	@Override
	public void render(TileWorksiteBase worksite, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (worksite instanceof IBoundedSite) {
			IBoundedSite boundedSite = (IBoundedSite) worksite;
			if (AWAutomationStatics.renderWorkBounds.getBoolean()) {
				RenderTools.setFullColorLightmap();
				GlStateManager.pushMatrix();
				GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
				GlStateManager.translate(x, y, z);
				BlockPos min = boundedSite.getWorkBoundsMin();
				BlockPos max = boundedSite.getWorkBoundsMax();
				if (max == null) {
					max = min;
				}
				if (min != null) {
					renderBoundingBox(worksite.getPos(), min, max, 1.f, 1.f, 1.f, 0.f);
				}
				GlStateManager.popAttrib();
				GlStateManager.popMatrix();
			}
		}
	}

	private void renderBoundingBox(BlockPos pos, BlockPos min, BlockPos max, float r, float g, float b, float expansion) {
		GlStateManager.disableLighting();
		GlStateManager.color(1.f, 1.f, 1.f, 1.f);
		AxisAlignedBB bb = new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX() + 1, max.getY() + 1, max.getZ() + 1);
		if (expansion != 0.f) {
			bb = bb.expand(expansion, expansion, expansion);
		}
		bb = bb.offset(-pos.getX(), -pos.getY(), -pos.getZ());
		RenderTools.drawOutlinedBoundingBox2(bb, 1.f, 1.f, 1.f, 0.0625f);
		GlStateManager.enableLighting();
	}

}
