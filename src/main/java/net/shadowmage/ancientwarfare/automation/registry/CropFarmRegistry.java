package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropBreakOnly;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropDefault;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropGourd;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropKeepBottom;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropMatureDefined;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropStem;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.CropTall;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.ICrop;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.ItemStackMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CropFarmRegistry {
	private static final Map<BlockStateMatcher, IBlockState> tillableBlocks = new HashMap<>();
	private static final Set<BlockStateMatcher> soilBlocks = new HashSet<>();
	private static final ICrop DEFAULT_CROP = new CropDefault();
	private static List<ICrop> crops = new ArrayList<>();

	static {
		registerCrop(new CropDefault());
		registerCrop(new CropGourd());
		registerCrop(new CropStem());
	}

	public static boolean isTillable(IBlockState state) {
		return tillableBlocks.keySet().stream().anyMatch(m -> m.test(state));
	}

	public static IBlockState getTilledState(IBlockState tillable) {
		return tillableBlocks.entrySet().stream().filter(e -> e.getKey().test(tillable)).map(Map.Entry::getValue).findFirst().orElse(tillable);
	}

	public static boolean isSoil(IBlockState state) {
		return soilBlocks.stream().anyMatch(matcher -> matcher.test(state));
	}

	public static void registerCrop(ICrop crop) {
		//adding to start of the list so that the last registered is always the first one processed, allowing for compatibility crops to be processed before default one
		crops.add(0, crop);
	}

	public static ICrop getCrop(IBlockState state) {
		return crops.stream().filter(h -> h.matches(state)).findFirst().orElse(DEFAULT_CROP);
	}

	public static ICrop getCrop(ItemStack stack) {
		return crops.stream().filter(h -> h.matches(stack)).findFirst().orElse(DEFAULT_CROP);
	}

	public static class TillableParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "tillable_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray tillables = JsonUtils.getJsonArray(json, "tillable_mapping");

			for (JsonElement t : tillables) {
				JsonObject tillableMapping = JsonUtils.getJsonObject(t, "");

				BlockStateMatcher tillableState = JsonHelper.getBlockStateMatcher(tillableMapping, "tillable");
				IBlockState tilledState = JsonHelper.getBlockState(tillableMapping, "tilled");

				tillableBlocks.put(tillableState, tilledState);
				soilBlocks.add(JsonHelper.getBlockStateMatcher(tillableMapping, "tilled"));
			}
		}
	}

	public static class CropParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "crop_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray crops = JsonUtils.getJsonArray(json, "crops");

			for (JsonElement t : crops) {
				JsonObject crop = JsonUtils.getJsonObject(t, "");
				registerCrop(getCrop(crop));
			}
		}

		private static ICrop getCrop(JsonObject crop) {
			BlockStateMatcher stateMatcher = JsonHelper.getBlockStateMatcher(crop, "crop");
			String type = JsonUtils.getString(crop, "type");
			JsonObject properties = crop.has("properties") ? JsonUtils.getJsonObject(crop, "properties") : new JsonObject();
			switch (type) {
				case "tall":
					return TallParser.parse(stateMatcher, properties);
				case "keep_bottom":
					return KeepBottomParser.parse(stateMatcher);
				case "mature_defined":
					return MatureDefinedParser.parse(stateMatcher, JsonHelper.getBlockState(crop, "crop"), crop);
				case "break_only":
					return BreakOnlyParser.parse(stateMatcher, JsonHelper.getItemStackMatcher(crop, "item"));
				default:
					return DEFAULT_CROP;
			}
		}

		private static class TallParser {
			public static ICrop parse(BlockStateMatcher stateMatcher, JsonObject properties) {
				return new CropTall(stateMatcher, JsonUtils.getInt(properties, "height"));
			}
		}

		private static class KeepBottomParser {
			public static ICrop parse(BlockStateMatcher stateMatcher) {
				return new CropKeepBottom(stateMatcher);
			}
		}

		private static class MatureDefinedParser {
			public static ICrop parse(BlockStateMatcher stateMatcher, IBlockState state, JsonObject crop) {
				return new CropMatureDefined(stateMatcher, JsonHelper.getPropertyStateMatcher(state, crop, "mature"));
			}
		}

		private static class BreakOnlyParser {
			public static ICrop parse(BlockStateMatcher stateMatcher, ItemStackMatcher stackMatcher) {
				return new CropBreakOnly(stateMatcher, stackMatcher);
			}
		}
	}

	public static class SoilParser implements IRegistryDataParser {

		@Override
		public String getName() {
			return "soil_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray plantables = JsonUtils.getJsonArray(json, "soils");

			for (JsonElement t : plantables) {
				soilBlocks.add(JsonHelper.getBlockStateMatcher(JsonUtils.getJsonObject(t, "")));
			}
		}
	}
}
