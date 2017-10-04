package net.shadowmage.ancientwarfare.structure.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.lib.vec.uv.IconTransformation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.BaseStationRenderer;

public class DraftingStationRenderer extends BaseStationRenderer {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":structure/drafting_station", "normal");
	public static final DraftingStationRenderer INSTANCE = new DraftingStationRenderer();

	private static CCModel model;
	public static TextureAtlasSprite sprite;
	private static IconTransformation iconTransform;

	static {
		model = OBJParser.parseModels(new ResourceLocation(AncientWarfareCore.modID, "models/block/structure/drafting_station.obj"), 7, new RedundantTransformation())
				.entrySet().iterator().next().getValue().backfacedCopy().computeNormals();
	}

	private DraftingStationRenderer() {}

	public static void setSprite(TextureAtlasSprite textureAtlasSprite) {
		sprite = textureAtlasSprite;
		iconTransform = new IconTransformation(sprite);
	}

	@Override
	protected CCModel getModel() {
		return model;
	}

	@Override
	protected IconTransformation getIconTransform() {
		return iconTransform;
	}
}
