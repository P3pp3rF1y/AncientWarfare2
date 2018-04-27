package net.shadowmage.ancientwarfare.core.registry;

import com.google.gson.JsonObject;

public interface IRegistryDataParser {
	String getName();

	void parse(JsonObject json);
}
