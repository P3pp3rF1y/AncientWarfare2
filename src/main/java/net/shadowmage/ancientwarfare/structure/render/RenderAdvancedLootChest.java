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
		//noinspection ConstantConditions
		if ((Minecraft.getMinecraft().player.capabilities.isCreativeMode || Minecraft.getMinecraft().playerController.getCurrentGameType() == GameType.SPECTATOR)
				&& rendererDispatcher.cameraHitResult != null && te.getPos().equals(rendererDispatcher.cameraHitResult.getBlockPos()) && te.getLootTable() != null) {
			setLightmapDisabled(true);
			drawNameplate(te, te.getLootRolls() + " x " + te.getLootTable(), x, y, z, 12);
			setLightmapDisabled(false);
		}

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
