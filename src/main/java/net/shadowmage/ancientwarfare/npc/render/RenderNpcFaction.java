package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.AbstractHorse;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;

public class RenderNpcFaction extends RenderNpcBase<NpcFaction> {
	public RenderNpcFaction(RenderManager renderManager) {
		super(renderManager);
	}

	@Override
	protected void preRenderCallback(NpcFaction npc, float partialTickTime) {
		float scale = npc.getRenderSizeModifier();
		float widthScale = npc.getWidthModifier();
		if (MathUtils.epsilonEquals(scale, 1.0f) && MathUtils.epsilonEquals(widthScale, 1.0f)) {
			return;
		}
		GlStateManager.scale(widthScale, scale, widthScale);
		if (npc.isRiding() && scale < 1f && !(npc.getRidingEntity() instanceof AbstractHorse)) {
			float ratio = (1f - scale) * (1f - scale);
			GlStateManager.translate(0, -3f * ratio, 0);
		}
	}
}
