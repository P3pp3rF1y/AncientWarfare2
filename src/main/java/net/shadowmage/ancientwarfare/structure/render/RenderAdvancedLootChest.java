package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityChestRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.GameType;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;

public class RenderAdvancedLootChest extends TileEntitySpecialRenderer<TileAdvancedLootChest> {
	private TileEntityChestRenderer chestRenderer = new TileEntityChestRenderer();

	@Override
	public void render(TileAdvancedLootChest te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (Minecraft.getMinecraft().player.capabilities.isCreativeMode || Minecraft.getMinecraft().playerController.getCurrentGameType() == GameType.SPECTATOR) {
			super.render(te, x, y, z, partialTicks, destroyStage, alpha);
		}

		chestRenderer.render(te, x, y, z, partialTicks, destroyStage, alpha);
	}

	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcher) {
		super.setRendererDispatcher(rendererDispatcher);
		chestRenderer.setRendererDispatcher(rendererDispatcher);
	}
}
