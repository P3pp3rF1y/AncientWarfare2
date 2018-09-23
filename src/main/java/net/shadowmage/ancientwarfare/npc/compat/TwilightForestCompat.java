package net.shadowmage.ancientwarfare.npc.compat;

import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.shadowmage.ancientwarfare.core.compat.ICompat;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import java.lang.reflect.Field;

public class TwilightForestCompat implements ICompat {
	@Override
	public String getModId() {
		return "twilightforest";
	}

	@Override
	public void init() {
		int dimensionId;
		try {
			Field dimensionField = ReflectionHelper.findField(Class.forName("twilightforest.TFConfig"), "dimension");
			Field dimensionIdField = ReflectionHelper.findField(Class.forName("twilightforest.TFConfig$Dimension"), "dimensionID");
			dimensionId = dimensionIdField.getInt(dimensionField.get(null));
		}
		catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException e) {
			AncientWarfareNPC.LOG.error("Error initiating Twilight Forest compatibility: ", e);
			return;
		}
		WorldTools.registerDimensionDaytimeLogic(dimensionId, world -> world.getWorldTime() % 24000 < 12000);
	}
}
