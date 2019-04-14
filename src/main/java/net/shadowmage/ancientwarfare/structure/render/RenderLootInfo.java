package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.GameType;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;

public class RenderLootInfo<T extends TileEntity & ISpecialLootContainer> extends TileEntitySpecialRenderer<T> {
	@Override
	public void render(T te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		//noinspection ConstantConditions
		if ((Minecraft.getMinecraft().player.capabilities.isCreativeMode || Minecraft.getMinecraft().playerController.getCurrentGameType() == GameType.SPECTATOR)
				&& rendererDispatcher.cameraHitResult != null && te.getPos().equals(rendererDispatcher.cameraHitResult.getBlockPos()) && te.getLootSettings().hasLoot()) {
			te.getLootSettings().getLootTableName().ifPresent(lt -> {
				setLightmapDisabled(true);
				drawNameplate(te, te.getLootSettings().getLootRolls() + " x " + lt.toString(), getNameplateOffsetX(te, x), y, getNameplateOffsetZ(te, z), 12);
				setLightmapDisabled(false);
			});
		}
	}

	protected double getNameplateOffsetZ(T te, double z) {
		return z;
	}

	protected double getNameplateOffsetX(T te, double x) {
		return x;
	}
}
