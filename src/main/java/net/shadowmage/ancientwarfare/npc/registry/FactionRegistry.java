package net.shadowmage.ancientwarfare.npc.registry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;

import java.util.HashSet;
import java.util.Set;

public class FactionRegistry {
	private FactionRegistry() {}

	private static Set<FactionDefinition> factions = new HashSet<>();

	public static Set<FactionDefinition> getFactions() {
		return factions;
	}

	public static class FactionParser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "factions";
		}

		@Override
		public void parse(JsonObject json) {
			JsonArray factionsArray = JsonUtils.getJsonArray(json, "factions");

			for (JsonElement e : factionsArray) {
				JsonObject faction = JsonUtils.getJsonObject(e, "faction");
				factions.add(new FactionDefinition(JsonUtils.getString(faction, "name"), Integer.parseInt(JsonUtils.getString(faction, "color"), 16)));
			}
		}
	}
}
