package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManualContentRegistry {
	private ManualContentRegistry() {}

	private static Map<String, List<IContentElement>> categoryContents = new HashMap<>();

	public static List<IContentElement> getCategoryContent(String category) {
		return categoryContents.get(category);
	}

	public static class ManualContentParser implements IRegistryDataParser {
		private static final IContentElement EMPTY_ELEMENT = new IContentElement() {};

		@Override
		public String getName() {
			return "manual_content";
		}

		@Override
		public void parse(JsonObject json) {
			String category = JsonUtils.getString(json, "category");
			List<IContentElement> contents = new ArrayList<>();
			categoryContents.put(category, contents);

			JsonArray elements = JsonUtils.getJsonArray(json, "content");

			for (JsonElement el : elements) {
				JsonObject elementJson = JsonUtils.getJsonObject(el, "");
				contents.add(parseElement(elementJson));
			}
		}

		private IContentElement parseElement(JsonObject elementJson) {
			String contentType = JsonUtils.getString(elementJson, "content_type");
			switch (contentType) {
				case "text":
					return TextElement.parse(elementJson);
			}
			return EMPTY_ELEMENT;
		}
	}
}
