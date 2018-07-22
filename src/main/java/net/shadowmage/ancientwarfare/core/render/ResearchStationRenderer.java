package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ResearchStationRenderer extends RotatableBlockRenderer {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":research_station", "normal");
	public static final ResearchStationRenderer INSTANCE = new ResearchStationRenderer();

	private ResearchStationRenderer() {
		super("core/research_station.obj");
	}
}
