package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableDefault;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableGourd;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableKeepBottom;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableStem;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.HarvestableTall;
import net.shadowmage.ancientwarfare.automation.tile.worksite.cropfarm.IHarvestable;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CropFarmRegistry {
	private static final Map<BlockStateMatcher, IBlockState> tillableBlocks = new HashMap<>();
	//TODO add likely additional registry to keep a list of plantable blocks in addition to just those that can be tilled
	private static final Set<BlockStateMatcher> plantableBlocks = new HashSet<>();
	private static final IHarvestable DEFAULT_HARVESTABLE = new HarvestableDefault();
	private static List<IHarvestable> harvestables = new ArrayList<>();

	static {
		registerHarvestable(new HarvestableDefault());
		registerHarvestable(new HarvestableGourd());
		registerHarvestable(new HarvestableStem());
	}

	public static boolean isTillable(IBlockState state) {
		return tillableBlocks.keySet().stream().anyMatch(m -> m.test(state));
	}

	public static IBlockState getTilledState(IBlockState tillable) {
		return tillableBlocks.entrySet().stream().filter(e -> e.getKey().test(tillable)).map(Map.Entry::getValue).findFirst().orElse(tillable);
	}

	public static boolean isPlantable(IBlockState state) {
		return plantableBlocks.stream().anyMatch(matcher -> matcher.test(state));
	}

	public static void registerHarvestable(IHarvestable harvestable) {
		//adding to start of the list so that the last registered is always the first one processed, allowing for compatibility harvestables to be processed before default one
		harvestables.add(0, harvestable);
	}

	public static IHarvestable getHarvestable(IBlockState state) {
		return harvestables.stream().filter(h -> h.matches(state)).findFirst().orElse(DEFAULT_HARVESTABLE);
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
				plantableBlocks.add(JsonHelper.getBlockStateMatcher(tillableMapping, "tilled"));
			}
		}
	}

	public static class HarvestableParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "harvestable_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray tillables = JsonUtils.getJsonArray(json, "harvestable");

			for (JsonElement t : tillables) {
				JsonObject harvestable = JsonUtils.getJsonObject(t, "");
				registerHarvestable(getHarvestable(harvestable));
			}
		}

		private static IHarvestable getHarvestable(JsonObject harvestable) {
			BlockStateMatcher stateMatcher = JsonHelper.getBlockStateMatcher(harvestable, "block");
			String type = JsonUtils.getString(harvestable, "type");
			JsonObject properties = harvestable.has("properties") ? JsonUtils.getJsonObject(harvestable, "properties") : new JsonObject();
			switch (type) {
				case "tall":
					return TallParser.parse(stateMatcher, properties);
				case "keep_bottom":
					return KeepBottomParser.parse(stateMatcher, properties);
				default:
					return DEFAULT_HARVESTABLE;
			}
		}

		private static class TallParser {

			public static IHarvestable parse(BlockStateMatcher stateMatcher, JsonObject properties) {
				return new HarvestableTall(stateMatcher, JsonUtils.getInt(properties, "height"));
			}
		}

		private static class KeepBottomParser {
			public static IHarvestable parse(BlockStateMatcher stateMatcher, JsonObject properties) {
				return new HarvestableKeepBottom(stateMatcher);
			}
		}
	}
}
