package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class EngineeringStationRenderer extends RotatableStationRenderer {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":engineering_station", "normal");
	public static final EngineeringStationRenderer INSTANCE = new EngineeringStationRenderer();

	private EngineeringStationRenderer() {
		super("core/engineering_station.obj");
	}
}
