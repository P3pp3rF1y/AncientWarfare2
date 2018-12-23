package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.tile.TEGateProxy;

public class GateProxyRenderer extends TileEntitySpecialRenderer<TEGateProxy> {
	private RenderGateHelper renderGateHelper = null;

	@Override
	public void render(TEGateProxy te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (!te.doesRender() || !initRenderHelper()) {
			return;
		}

		BlockPos pos = te.getPos();
		te.getGate().ifPresent(gate -> renderGateHelper.doRender(gate,
				x + gate.posX - pos.getX(),
				y + gate.posY - pos.getY(),
				z + gate.posZ - pos.getZ(),
				0, partialTicks));
	}

	private boolean initRenderHelper() {
		if (renderGateHelper != null) {
			return true;
		}
		RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

		//noinspection ConstantConditions
		if (renderManager == null) {
			return false;
		}
		renderGateHelper = new RenderGateHelper(renderManager);
		return true;
	}

	@Override
	public boolean isGlobalRenderer(TEGateProxy te) {
		return true;
	}
}
