package net.shadowmage.ancientwarfare.automation.render;

import codechicken.lib.colour.ColourRGBA;
import codechicken.lib.lighting.LightModel;
import codechicken.lib.model.bakery.generation.ILayeredBlockBakery;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.buffer.BakingVertexBuffer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.RedundantTransformation;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.IconTransformation;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.automation.block.TorqueTier;
import net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static net.shadowmage.ancientwarfare.automation.render.property.AutomationProperties.IS_CONTROL;

@SideOnly(Side.CLIENT)
public class FlywheelStorageRenderer implements ILayeredBlockBakery, ITESRRenderer {
	private static final String FLYWHEEL_STORAGE_REGISTRY_PATH = ":automation/flywheel_storage";
	public static final ModelResourceLocation LIGHT_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + FLYWHEEL_STORAGE_REGISTRY_PATH, "small_light");
	public static final ModelResourceLocation MEDIUM_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + FLYWHEEL_STORAGE_REGISTRY_PATH, "small_medium");
	public static final ModelResourceLocation HEAVY_MODEL_LOCATION = new ModelResourceLocation(AncientWarfareCore.MOD_ID + FLYWHEEL_STORAGE_REGISTRY_PATH, "small_heavy");

	public static final FlywheelStorageRenderer INSTANCE = new FlywheelStorageRenderer();

	private Collection<CCModel> spindleSmall;
	private Collection<CCModel> upperShroudSmall;
	private Collection<CCModel> lowerShroudSmall;
	private Collection<CCModel> flywheelExtensionSmall;
	private Collection<CCModel> lowerWindowSmall;
	private Collection<CCModel> upperWindowSmall;
	private Collection<CCModel> caseBarsSmall;
	private Collection<CCModel> spindleLarge;
	private Collection<CCModel> upperShroudLarge;
	private Collection<CCModel> lowerShroudLarge;
	private Collection<CCModel> flywheelExtensionLarge;
	private Collection<CCModel> lowerWindowLarge;
	private Collection<CCModel> upperWindowLarge;
	private Collection<CCModel> caseBarsLarge;

	private Map<Pair<Boolean, TorqueTier>, IconTransformation> iconTransformations = Maps.newHashMap();
	private Map<Pair<Boolean, TorqueTier>, TextureAtlasSprite> sprites = Maps.newHashMap();

	public void setSprite(boolean large, TorqueTier tier, TextureAtlasSprite sprite) {
		sprites.put(new ImmutablePair<>(large, tier), sprite);
		iconTransformations.put(new ImmutablePair<>(large, tier), new IconTransformation(sprite));
	}

	public TextureAtlasSprite getSprite(TorqueTier tier) {
		return sprites.get(new ImmutablePair<>(false, tier));
	}

	private IconTransformation getIconTransformation(boolean large, TorqueTier tier) {
		return iconTransformations.get(new ImmutablePair<>(large, tier));
	}

	private FlywheelStorageRenderer() {
		Map<String, CCModel> smallModel = loadModel("flywheel_small.obj");
		flywheelExtensionSmall = removeGroups(smallModel, s -> s.startsWith("spindle.flywheelExtension."));
		spindleSmall = removeGroups(smallModel, s -> s.startsWith("spindle."));
		upperShroudSmall = removeGroups(smallModel, s -> s.startsWith("shroudUpper."));
		lowerShroudSmall = removeGroups(smallModel, s -> s.startsWith("shroudLower."));
		lowerWindowSmall = setAlpha(removeGroups(smallModel, s -> s.startsWith("windowLower.")), 0.25d);
		upperWindowSmall = setAlpha(removeGroups(smallModel, s -> s.startsWith("windowUpper.")), 0.25d);
		caseBarsSmall = removeGroups(smallModel, s -> s.startsWith("caseBars."));

		Map<String, CCModel> largeModel = loadModel("flywheel_large.obj");
		flywheelExtensionLarge = removeGroups(largeModel, s -> s.startsWith("spindle.flywheelExtension."));
		spindleLarge = removeGroups(largeModel, s -> s.startsWith("spindle."));
		upperShroudLarge = removeGroups(largeModel, s -> s.startsWith("shroudUpper."));
		lowerShroudLarge = removeGroups(largeModel, s -> s.startsWith("shroudLower."));
		lowerWindowLarge = setAlpha(removeGroups(largeModel, s -> s.startsWith("windowLower.")), 0.25d);
		upperWindowLarge = setAlpha(removeGroups(largeModel, s -> s.startsWith("windowUpper.")), 0.25d);
		caseBarsLarge = removeGroups(largeModel, s -> s.startsWith("caseBars."));
	}

	private Map<String, CCModel> loadModel(String modelName) {
		Map<String, CCModel> ret = OBJParser.parseModels(new ResourceLocation(AncientWarfareCore.MOD_ID, "models/block/automation/" + modelName), 7, new RedundantTransformation());

		for (Map.Entry<String, CCModel> group : ret.entrySet()) {
			group.setValue(group.getValue().backfacedCopy().computeNormals());
		}

		return ret;
	}

	private Collection<CCModel> removeGroups(Map<String, CCModel> objGroups, Function<String, Boolean> filter) {
		Set<CCModel> ret = Sets.newHashSet();

		Iterator<Map.Entry<String, CCModel>> iterator = objGroups.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, CCModel> entry = iterator.next();

			if (filter.apply(entry.getKey())) {
				ret.add(entry.getValue());
				iterator.remove();
			}
		}

		return ret;
	}

	private Collection<CCModel> transformModels(Collection<CCModel> groups, Transformation transform) {
		return groups.stream().map(e -> e.copy().apply(transform)).collect(Collectors.toSet());
	}

	@Override
	public List<BakedQuad> bakeItemQuads(@Nullable EnumFacing face, ItemStack stack) {
		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		TorqueTier tier = TorqueTier.byMetadata(stack.getMetadata());

		HashSet<CCModel> transformedModels = Sets.newHashSet();
		transformedModels.addAll(getTransformedModels(BlockRenderLayer.SOLID, false, true, 0f, 1));
		transformedModels.addAll(getTransformedModels(BlockRenderLayer.SOLID, false, false, 0f, 1));
		transformedModels.addAll(getTransformedModels(BlockRenderLayer.TRANSLUCENT, false, false, 0f, 1));
		renderModels(transformedModels, ccrs, false, tier);

		buffer.finishDrawing();
		return buffer.bake();
	}

	@Override
	public List<BakedQuad> bakeLayerFace(@Nullable EnumFacing face, BlockRenderLayer layer, IExtendedBlockState state) {
		if (face != null || !state.getValue(AutomationProperties.IS_CONTROL)) {
			return Collections.emptyList();
		}

		BakingVertexBuffer buffer = BakingVertexBuffer.create();
		buffer.begin(7, DefaultVertexFormats.ITEM);
		CCRenderState ccrs = CCRenderState.instance();
		ccrs.reset();
		ccrs.bind(buffer);

		boolean largeModel = state.getValue(AutomationProperties.WIDTH) > 1;
		TorqueTier tier = state.getValue(AutomationProperties.TIER);

		renderModels(getTransformedModels(layer, state), ccrs, largeModel, tier);

		buffer.finishDrawing();
		return buffer.bake();
	}

	private Set<CCModel> getTransformedModels(BlockRenderLayer layer, IExtendedBlockState state) {
		if (!state.getValue(IS_CONTROL)) {
			return Collections.emptySet();
		}

		boolean largeModel = state.getValue(AutomationProperties.WIDTH) > 1;
		boolean displayDynamicParts = state.getValue(AutomationProperties.DYNAMIC);
		float rotation = state.getValue(AutomationProperties.ROTATION);
		int height = state.getValue(AutomationProperties.HEIGHT);

		return getTransformedModels(layer, largeModel, displayDynamicParts, rotation, height);
	}

	private Set<CCModel> getTransformedModels(BlockRenderLayer layer, boolean largeModel, boolean displayDynamicParts, float rotation, int height) {
		Set<CCModel> transformedGroups = Sets.newHashSet();
		Collection<CCModel> spindle = largeModel ? spindleLarge : spindleSmall;
		Collection<CCModel> flywheelExtension = largeModel ? flywheelExtensionLarge : flywheelExtensionSmall;
		Collection<CCModel> caseBars = largeModel ? caseBarsLarge : caseBarsSmall;
		Collection<CCModel> lowerWindow = largeModel ? lowerWindowLarge : lowerWindowSmall;
		Collection<CCModel> upperShroud = largeModel ? upperShroudLarge : upperShroudSmall;
		Collection<CCModel> lowerShroud = largeModel ? lowerShroudLarge : lowerShroudSmall;
		Collection<CCModel> upperWindow = largeModel ? upperWindowLarge : upperWindowSmall;

		if (displayDynamicParts) {
			if (layer == BlockRenderLayer.SOLID) {
				Transformation rotationTransform = new Rotation(rotation, 0, 1, 0).at(Vector3.center);
				for (int i = 0; i < height; i++) {
					Translation translation = new Translation(0, i, 0);
					transformedGroups.addAll(transformModels(spindle, translation.with(rotationTransform)));
					if (i < height - 1) {
						transformedGroups.addAll(transformModels(flywheelExtension, translation.with(rotationTransform)));//at every level less than highest
					}
				}
			}
		} else {
			for (int i = 0; i < height; i++) {
				Translation translation = new Translation(0, i, 0);
				if (layer == BlockRenderLayer.SOLID) {
					transformedGroups.addAll(transformModels(caseBars, translation));
					if (i == height - 1) {
						transformedGroups.addAll(transformModels(upperShroud, translation));//at highest level
					}
					if (i == 0) {
						transformedGroups.addAll(transformModels(lowerShroud, translation));//at ground level
					}
				} else {
					transformedGroups.addAll(transformModels(lowerWindow, translation));
					if (i < height - 1) {
						transformedGroups.addAll(transformModels(upperWindow, translation));
					}
				}
			}
		}

		return transformedGroups;
	}

	private Collection<CCModel> setAlpha(Collection<CCModel> models, double alpha) {
		models.forEach(m -> m.setColour(new ColourRGBA(1, 1, 1, alpha).pack()));
		return models;
	}

	@Override
	public IExtendedBlockState handleState(IExtendedBlockState state, IBlockAccess access, BlockPos pos) {
		Optional<TileFlywheelStorage> te = WorldTools.getTile(access, pos, TileFlywheelStorage.class);
		boolean isControl = true;
		int height = 1;
		int width = 1;

		if (te.isPresent()) {
			TileFlywheelStorage storage = te.get();

			isControl = storage.isControl;
			width = storage.setWidth;
			height = storage.setHeight;
		}

		IExtendedBlockState updatedState = state.withProperty(AutomationProperties.DYNAMIC, false);
		updatedState = updatedState.withProperty(AutomationProperties.IS_CONTROL, isControl);
		updatedState = updatedState.withProperty(AutomationProperties.HEIGHT, height);
		updatedState = updatedState.withProperty(AutomationProperties.WIDTH, width);
		updatedState = updatedState.withProperty(AutomationProperties.ROTATION, 0f);

		return updatedState;
	}

	private void renderModels(Collection<CCModel> modelGroups, CCRenderState ccrs, boolean large, TorqueTier tier) {
		for (CCModel group : modelGroups) {
			group.render(ccrs, getIconTransformation(large, tier));
		}
	}

	@Override
	public void renderTransformedBlockModels(CCRenderState ccrs, IExtendedBlockState state) {
		TextureUtils.bindBlockTexture();

		boolean largeModel = state.getValue(AutomationProperties.WIDTH) > 1;
		TorqueTier tier = state.getValue(AutomationProperties.TIER);

		Set<CCModel> modelGroups = getTransformedModels(BlockRenderLayer.SOLID, state);

		for (CCModel group : modelGroups) {
			group.render(ccrs, LightModel.standardLightModel, getIconTransformation(largeModel, tier));
		}
	}
}
