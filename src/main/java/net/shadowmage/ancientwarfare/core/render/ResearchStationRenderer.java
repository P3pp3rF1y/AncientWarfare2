package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ResearchStationRenderer implements ISimpleBlockBakery {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.modID + ":research_station", "normal");
	public static final ResearchStationRenderer INSTANCE = new ResearchStationRenderer();

	private static CCModel model;
	public static TextureAtlasSprite sprite;
	private static IconTransformation iconTransform;

	static {
		model = OBJParser.parseModels(new ResourceLocation(AncientWarfareCore.modID, "models/block/research_station.obj"), 7, new RedundantTransformation())
				.entrySet().iterator().next().getValue().backfacedCopy().computeNormals();
	}

	private ResearchStationRenderer() {}

	public static void setSprite(TextureAtlasSprite textureAtlasSprite) {
		sprite = textureAtlasSprite;
		iconTransform = new IconTransformation(sprite);
	}

	@Nonnull
	@Override
	public List<BakedQuad> bakeQuads(@Nullable EnumFacing face, IExtendedBlockState state) {
		if (face != null) {
			return Collections.emptyList();
		}

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		model.copy().apply(Rotation.quarterRotations[(state.getValue(BlockRenderProperties.UNLISTED_FACING).getHorizontalIndex() + 2) & 3].at(Vector3.center)).render(ccrs, iconTransform);

		buffer.finishDrawing();
		return buffer.bake();
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		return state;
	}

	@Nonnull
	@Override
	public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {
		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		model.render(ccrs, iconTransform);

		buffer.finishDrawing();
		return buffer.bake();
	}
}
