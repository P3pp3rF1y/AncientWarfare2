package net.shadowmage.ancientwarfare.automation.registry;

import com.google.gson.JsonObject;

public interface IRegistryDataParser {
	String getName();

	void parse(JsonObject json);
}
