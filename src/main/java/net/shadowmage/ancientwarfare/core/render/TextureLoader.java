package net.shadowmage.ancientwarfare.core.render;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class TextureLoader {

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
		EngineeringStationRenderer.setSprite(evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.modID + ":model/core/tile_engineering_station")));
	}
}
