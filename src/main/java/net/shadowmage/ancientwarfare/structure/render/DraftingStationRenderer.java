package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.BaseBakery;

public class DraftingStationRenderer extends BaseBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":structure/drafting_station", "normal");
	public static final DraftingStationRenderer INSTANCE = new DraftingStationRenderer();

	private DraftingStationRenderer() {
		super("structure/drafting_station.obj");
	}
}
