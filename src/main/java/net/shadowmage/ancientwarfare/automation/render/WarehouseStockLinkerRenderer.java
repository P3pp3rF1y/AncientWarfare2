package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import javax.annotation.Nonnull;
import java.util.List;

public class WarehouseStockLinkerRenderer extends TileEntitySpecialRenderer<TileWarehouseStockLinker> {

	public WarehouseStockLinkerRenderer() {

	}

	@Override
	public void render(TileWarehouseStockLinker tile, double x, double y, double z, float partialTick, int destroyStage, float alpha) {
		EnumFacing d = EnumFacing.VALUES[tile.getBlockMetadata()].getOpposite();
		float r = getRotationFromDirection(d);

		GlStateManager.enableRescaleNormal();
		RenderTools.setFullColorLightmap();
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.translate(0.5f, 1.f, 0.5f);//translate the point to the top-center of the block
		GlStateManager.rotate(r, 0, 1, 0);//rotate for rotation
		GlStateManager.translate(0.5f, 0, 0.5f);//translate to top-left corner
		GlStateManager.translate(0, -0.125f, -0.127f);//move out and down for front-face of sign
		renderSignContents(tile);

		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	float getRotationFromDirection(EnumFacing d) {
		switch (d) {
			case NORTH:
				return 180.f;
			case SOUTH:
				return 0.f;
			case EAST:
				return 90.f;
			case WEST:
				return 270.f;
			default:
				return 0.f;
		}
	}

	/*
	 * matrix should be setup so that 0,0 is upper-left-hand corner of the sign-board, with a
	 * transformation of 1 being 1 BLOCK
	 */

	private void renderSignContents(TileWarehouseStockLinker tile) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, -0.002f);//move out from block face slightly, for depth-buffer/z-fighting
		GlStateManager.scale(-1, -1, -1);//rescale for gui rendering axis flip
		GlStateManager.scale(0.0050f, 0.0050f, 0.0050f);//this scale puts it at 200 units(pixels) per block
		GlStateManager.scale(1f, 1f, 0.0001f);//squash Z axis for 'flat' rendering of 3d blocks/items..LOLS
		FontRenderer fr = getFontRenderer();
		GlStateManager.disableLighting();
		ItemStack filterItem;
		WarehouseStockFilter filter;
		String name = "";
		List<WarehouseStockFilter> filters = tile.getFilters();
		int max = filters.size();
		if (10 < max) {
			max = 10;
		}
		for (int i = 0; i < max; i++) {
			filter = filters.get(i);
			filterItem = filter.getFilterItem();
			if (!filterItem.isEmpty()) {
				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(filter.getFilterItem(), 0 + 24, i * 18 + 14);
			}
			name = filterItem.isEmpty() ? "Empty Filter" : filterItem.getDisplayName();
			if (name.length() > 20) {
				name = name.substring(0, 20);
			}
			fr.drawString(name, 20 + 12 + 12, i * 18 + 4 + 14, 0xffffffff);
			name = String.valueOf(filter.getQuantity());
			fr.drawString(name, 200 - 25 - fr.getStringWidth(name), i * 18 + 4 + 14, 0xffffffff);
		}
		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
	}
}
