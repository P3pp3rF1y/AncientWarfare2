package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.BlockStateMatcher;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public class CropFarmRegistry {
	private static final Map<BlockStateMatcher, IBlockState> tillableBlocks = new HashMap<>();
	//TODO add likely additional registry to keep a list of plantable blocks in addition to just those that can be tilled
	private static final Set<BlockStateMatcher> plantableBlocks = new HashSet<>();

	public static boolean isTillable(IBlockState state) {
		return tillableBlocks.keySet().stream().anyMatch(m -> m.test(state));
	}

	public static IBlockState getTilledState(IBlockState tillable) {
		return tillableBlocks.entrySet().stream().filter(e -> e.getKey().test(tillable)).map(Map.Entry::getValue).findFirst().orElse(tillable);
	}

	public static boolean isPlantable(IBlockState state) {
		return plantableBlocks.stream().anyMatch(matcher -> matcher.test(state));
	}

	public static class Parser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "tillable_blocks";
		}

		@Override
		public void parse(JsonObject json) {
			try {
				JsonArray tillables = JsonUtils.getJsonArray(json, "tillable_mapping");

				for (JsonElement t : tillables) {
					JsonObject tillableMapping = JsonUtils.getJsonObject(t, "");

					BlockStateMatcher tillableState = JsonHelper.getBlockStateMatcher(tillableMapping, "tillable");
					IBlockState tilledState = JsonHelper.getBlockState(tillableMapping, "tilled");

					tillableBlocks.put(tillableState, tilledState);
					plantableBlocks.add(JsonHelper.getBlockStateMatcher(tillableMapping, "tilled"));
				}
			}
			catch (JsonParseException e) {
				AncientWarfareCore.log.error("Error parsing tillables: \n" + e.getMessage());
			}
			catch (MissingResourceException e) {
				AncientWarfareCore.log.error(e.getMessage());
			}
		}
	}
}
