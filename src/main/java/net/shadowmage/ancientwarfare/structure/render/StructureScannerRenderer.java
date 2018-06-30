package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

public class StructureScannerRenderer extends TileEntitySpecialRenderer<TileStructureScanner> {
	@Override
	public void render(TileStructureScanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Minecraft mc = Minecraft.getMinecraft();
		mc.entityRenderer.disableLightmap();
		ItemStack scanner = te.getScannerInventory().getStackInSlot(0);
		if (!scanner.isEmpty()) {
			((IBoxRenderer) scanner.getItem()).renderBox(mc.player, scanner, partialTicks);
		}
		mc.entityRenderer.enableLightmap();
	}
}
