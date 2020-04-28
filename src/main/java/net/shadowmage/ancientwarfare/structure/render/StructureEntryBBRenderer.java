package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.event.IBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureEntry;
import net.shadowmage.ancientwarfare.structure.gamedata.StructureMap;
import net.shadowmage.ancientwarfare.structure.template.build.StructureBB;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class StructureEntryBBRenderer {
	public static final String SHOW_BBS_TAG = AncientWarfareStructure.MOD_ID + ":showBBs";
	private static final int BB_RENDER_RANGE = 200;

	@SubscribeEvent
	public void handleRenderLastEvent(RenderWorldLastEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		renderStructureBoundingBoxes(evt, mc);
	}

	private void renderStructureBoundingBoxes(RenderWorldLastEvent evt, Minecraft mc) {
		EntityPlayerSP player = mc.player;
		if (!player.getTags().contains(SHOW_BBS_TAG)) {
			return;
		}
		WorldClient world = mc.world;

		StructureMap map = AWGameData.INSTANCE.getPerWorldData(world, StructureMap.class);
		Collection<StructureEntry> structuresNear = map.getEntriesNear(world, player.getPosition().getX(), player.getPosition().getZ(), BB_RENDER_RANGE / 16, true, new ArrayList<>());
		for (StructureEntry structure : structuresNear) {
			StructureBB bb = structure.getBB();
			if (bb.getCenter().distanceSq(player.getPosition()) < (BB_RENDER_RANGE * BB_RENDER_RANGE)) {
				IBoxRenderer.Util.renderBoundingBox(player, bb.min, bb.max, evt.getPartialTicks(), Color.BLUE);
			}
		}
	}
}
