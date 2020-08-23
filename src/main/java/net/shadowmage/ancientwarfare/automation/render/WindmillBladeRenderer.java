package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.shadowmage.ancientwarfare.automation.block.BlockWindmillBlade;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.render.property.CoreProperties;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class WindmillBladeRenderer extends AnimatedBlockRenderer {

	public static final ModelResourceLocation MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + ":automation/windmill_blade", "normal");
	public static final WindmillBladeRenderer INSTANCE = new WindmillBladeRenderer();

	private static final PerspectiveProperties MODEL_PROPERTIES;

	static {
		TRSRTransformation thirdPerson = TransformUtils.create(0, 2.5f, 0, 75, 225, 0, 0.375f);
		ImmutableMap.Builder<ItemCameraTransforms.TransformType, TRSRTransformation> defaultBlockBuilder = ImmutableMap.builder();
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GUI, TransformUtils.create(0, 0, 0, 30, 225, 90, 0.320f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.GROUND, TransformUtils.create(0, 3, 0, 0, 0, 0, 0.125f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIXED, TransformUtils.create(0, 0, 0, 0, 0, 0, 0.25f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, thirdPerson);
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, TransformUtils.flipLeft(thirdPerson));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, TransformUtils.create(0, 0, 0, 0, 135, 0, 0.2f));
		defaultBlockBuilder.put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, TransformUtils.create(0, 0, 0, 0, 135, 0, 0.2f));
		MODEL_PROPERTIES = new PerspectiveProperties(new CCModelState(defaultBlockBuilder.build()), true, true);
	}

	private final Collection<CCModel> bladeShaft;
	private final Collection<CCModel> windmillShaft;
	private final Collection<CCModel> blade;
	private final Collection<CCModel> bladeJoint;
	private final CCModel cube;
	public TextureAtlasSprite cubeSprite;
	private IconTransformation cubeIconTransform;

	private WindmillBladeRenderer() {
		super("automation/windmill_blade.obj");
		cube = OBJParser.parseModels(new ResourceLocation(AncientWarfareCore.MOD_ID, "models/block/automation/windmill_blade_cube.obj"), 7, new RedundantTransformation()).values().iterator().next().backfacedCopy();
		bladeShaft = removeGroups(s -> s.startsWith("bladeShaft."));
		windmillShaft = transformModels(removeGroups(s -> s.startsWith("windmillShaft.")), new Translation(0d, 0.5d, 0d));
		blade = removeGroups(s -> s.startsWith("blade."));
		bladeJoint = removeGroups(s -> s.startsWith("bladeJoint."));
	}

	private Collection<CCModel> transformModels(Collection<CCModel> groups, Transformation transform) {
		return groups.stream().map(e -> e.copy().apply(transform)).collect(Collectors.toSet());
	}

	public void setCubeSprite(TextureAtlasSprite sprite) {
		this.cubeSprite = sprite;
		cubeIconTransform = new IconTransformation(this.cubeSprite);
	}

	@Override
	public List<BakedQuad> bakeQuads(@Nullable EnumFacing face, IExtendedBlockState state) {
		if (state.getValue(BlockWindmillBlade.FORMED) && (!state.getValue(AutomationProperties.DYNAMIC) || !state.getValue(AutomationProperties.IS_CONTROL))) {
			return Collections.emptyList(); //formed blade doesn't have static rendering and only control gets rendered
		}

		return super.bakeQuads(face, state);
	}

	@Override
	protected void renderBlockModels(Collection<CCModel> modelGroups, CCRenderState ccrs, EnumFacing face, IExtendedBlockState state) {
		if (!state.getValue(BlockWindmillBlade.FORMED)) {
			cube.render(ccrs, cubeIconTransform);
		} else {
			super.renderBlockModels(modelGroups, ccrs, face, state);
		}
	}

	@Override
	protected Collection<CCModel> applyModelTransforms(Collection<CCModel> modelGroups, EnumFacing face, IExtendedBlockState state) {
		if (!state.getValue(BlockWindmillBlade.FORMED)) {
			return Collections.singleton(cube);
		}

		if (!state.getValue(AutomationProperties.DYNAMIC) || !state.getValue(AutomationProperties.IS_CONTROL)) {
			return Collections.emptySet();
		}

		EnumFacing frontFacing = state.getValue(CoreProperties.UNLISTED_HORIZONTAL_FACING);
		int height = (state.getValue(AutomationProperties.HEIGHT) - 1) / 2;
		float rotation = state.getValue(AutomationProperties.ROTATION);

		return transformBlades(frontFacing, height, rotation);
	}

	@Override
	protected IconTransformation getIconTransform(IExtendedBlockState state) {
		return !state.getValue(BlockWindmillBlade.FORMED) ? cubeIconTransform : iconTransform;
	}

	private Set<CCModel> transformBlades(EnumFacing frontFacing, int height, float rotation) {
		Set<CCModel> transformedGroups = Sets.newHashSet();
		Vector3 center = new Vector3(8d / 16d, 8d / 16d, 8d / 16d);
		Transformation baseRotation = new Rotation(rotation, 0, 0, 1).at(center);
		transformedGroups.addAll(rotateModels(windmillShaft, frontFacing, baseRotation));

		for (int i = 0; i < 4; i++) {
			Transformation bladeTransform = new Rotation(((float) i) * Trig.PI / 2f, 0, 0, 1).at(center).with(baseRotation);
			if (i < 2) {
				transformedGroups.addAll(rotateModels(bladeShaft, frontFacing, bladeTransform));
			}
			for (int k = 1; k < height; k++) {
				transformedGroups.addAll(rotateModels(blade, frontFacing, bladeTransform));
				if (k == height - 1) {
					transformedGroups.addAll(rotateModels(bladeJoint, frontFacing, bladeTransform));
				} else {
					bladeTransform = new Translation(0, 1, 0).with(bladeTransform);
				}
			}
		}
		return transformedGroups;
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		return WorldTools.getTile(access, pos, TileWindmillBlade.class).map(t -> getWindmillState(state, t)).orElse(state);
	}

	private IExtendedBlockState getWindmillState(IExtendedBlockState state, TileWindmillBlade blade) {
		return state.withProperty(BlockWindmillBlade.FORMED, blade.isFormed())
				.withProperty(AutomationProperties.IS_CONTROL, false)
				.withProperty(AutomationProperties.HEIGHT, 0)
				.withProperty(AutomationProperties.ROTATION, 0f)
				.withProperty(CoreProperties.UNLISTED_HORIZONTAL_FACING, EnumFacing.NORTH)
				.withProperty(AutomationProperties.DYNAMIC, false);
	}

	@Override
	public PerspectiveProperties getModelProperties(ItemStack stack) {
		return MODEL_PROPERTIES;
	}

	@Override
	protected void renderItemModels(CCRenderState ccrs, ItemStack stack) {
		transformBlades(EnumFacing.NORTH, 2, 0).forEach(m -> m.render(ccrs, iconTransform));
	}
}