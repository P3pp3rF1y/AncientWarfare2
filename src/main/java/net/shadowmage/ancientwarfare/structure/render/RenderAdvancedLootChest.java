package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;

public class RenderAdvancedLootChest extends RenderLootInfo<TileAdvancedLootChest> {
	private TileEntityChestRenderer chestRenderer = new TileEntityChestRenderer();

	@Override
	public void render(TileAdvancedLootChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		if (te.getClass() == TileAdvancedLootChest.class) {
			chestRenderer.render(te, x, y, z, partialTicks, destroyStage, alpha);
		}
	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcher) {
		super.setRendererDispatcher(rendererDispatcher);
		chestRenderer.setRendererDispatcher(rendererDispatcher);
	}
}
