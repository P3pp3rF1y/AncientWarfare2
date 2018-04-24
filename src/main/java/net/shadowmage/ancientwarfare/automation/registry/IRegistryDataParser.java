package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonElement;

public interface IRegistryDataParser {
	String getName();

	void parse(JsonElement json);
}
