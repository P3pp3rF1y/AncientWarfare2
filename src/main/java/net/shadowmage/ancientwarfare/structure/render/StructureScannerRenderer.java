package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureScanner;

public class StructureScannerRenderer extends TileEntitySpecialRenderer<TileStructureScanner> {
	@Override
	public void render(TileStructureScanner te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		super.render(te, x, y, z, partialTicks, destroyStage, alpha);

		Minecraft mc = Minecraft.getMinecraft();
		mc.entityRenderer.disableLightmap();
		ItemStack scanner = te.getScannerInventory().getStackInSlot(0);
		if (te.getBoundsActive() && !scanner.isEmpty() && ItemStructureScanner.readyToExport(scanner)) {
			((IBoxRenderer) scanner.getItem()).renderBox(mc.player, EnumHand.MAIN_HAND, scanner, partialTicks);
		}
		mc.entityRenderer.enableLightmap();
	}

	@Override
	public boolean isGlobalRenderer(TileStructureScanner te) {
		return true;
	}
}
