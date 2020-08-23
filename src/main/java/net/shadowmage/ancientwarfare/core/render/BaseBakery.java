package net.shadowmage.ancientwarfare.core.render;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakery.generation.ISimpleBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public abstract class BaseBakery implements ISimpleBlockBakery {
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		return state;
	}

	protected Map<String, CCModel> groups;
	public TextureAtlasSprite sprite;
	protected IconTransformation iconTransform;

	protected BaseBakery(String modelPath) {
		groups = OBJParser.parseModels(new ResourceLocation(AncientWarfareCore.MOD_ID, "models/block/" + modelPath), 7, getBaseTransformation());

		for (Map.Entry<String, CCModel> group : groups.entrySet()) {
			group.setValue(group.getValue().backfacedCopy().computeNormals());
		}
	}

	protected Transformation getBaseTransformation() {
		return new RedundantTransformation();
	}

	public void setSprite(TextureAtlasSprite textureAtlasSprite) {
		sprite = textureAtlasSprite;
		iconTransform = new IconTransformation(sprite);
	}

	private static final PerspectiveProperties MODEL_PROPERTIES;

	static {
		TRSRTransformation thirdPerson = TransformUtils.create(0, 2.5f, 0, 75, 225, 0, 0.375f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> defaultBlockBuilder = ImmutableMap.builder();
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(0, 0, 0, 30, 225, 0, 0.625f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.25f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0, 0, 0, 0, 0, 0, 0.5f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0, 0, 0, 0, 135, 0, 0.4f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0, 0, 0, 0, 135, 0, 0.4f));
		MODEL_PROPERTIES = new PerspectiveProperties(new CCModelState(defaultBlockBuilder.build()), true, true);
	}

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

		Collection<CCModel> transformedGroups = applyModelTransforms(groups.values(), face, state);

		renderBlockModels(transformedGroups, ccrs, face, state);

		buffer.finishDrawing();
		return buffer.bake();
	}

	protected Collection<CCModel> applyModelTransforms(Collection<CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		return modelGroups;
	}

	protected void renderBlockModels(Collection<CCModel> modelGroups, CCRenderState ccrs, EnumFacing face, IExtendedBlockState state) {
		renderAllModels(modelGroups, ccrs, state);
	}

	private void renderAllModels(Collection<CCModel> modelGroups, CCRenderState ccrs, IExtendedBlockState state) {
		for (CCModel group : modelGroups) {
			group.render(ccrs, getIconTransform(state));
		}
	}

	private void renderAllModels(CCRenderState ccrs, ItemStack stack) {
		for (CCModel group : groups.values()) {
			group.render(ccrs, getIconTransform(stack));
		}
	}

	protected IconTransformation getIconTransform(IExtendedBlockState state) {
		return iconTransform;
	}

	protected IconTransformation getIconTransform(ItemStack stack) {
		return iconTransform;
	}

	protected void renderItemModels(CCRenderState ccrs, ItemStack stack) {
		renderAllModels(ccrs, stack);
	}

	@Override
	public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {
		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		renderItemModels(ccrs, stack);

		buffer.finishDrawing();
		return buffer.bake();
	}

	@Override
	public PerspectiveProperties getModelProperties(ItemStack stack) {
		return MODEL_PROPERTIES;
	}
}
