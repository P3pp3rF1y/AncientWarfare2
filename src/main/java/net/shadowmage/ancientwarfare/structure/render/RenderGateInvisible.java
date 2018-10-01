package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

import javax.annotation.Nullable;

public class RenderGateInvisible extends Render<EntityGate> {
	public RenderGateInvisible(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	public void doRender(EntityGate entity, double x, double y, double z, float entityYaw, float partialTicks) {
		//invisible
	}

	@Nullable
	@Override
	protected ResourceLocation getEntityTexture(EntityGate entity) {
		return null;
	}
}
