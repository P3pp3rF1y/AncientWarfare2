package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.util.JsonUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ManualContentRegistry {
	private ManualContentRegistry() {}

	private static Map<String, List<IContentElement>> englishCategoryContents = new HashMap<>();
	private static Map<String, List<IContentElement>> categoryContents = new HashMap<>();

	public static List<IContentElement> getCategoryContent(String category) {
		if (categoryContents.containsKey(category)) {
			return categoryContents.get(category);
		} else if (englishCategoryContents.containsKey(category)) {
			return englishCategoryContents.get(category);
		}
		return Collections.emptyList();
	}

	public static void clearContents() {
		englishCategoryContents.clear();
		categoryContents.clear();
	}

	public static class ManualContentParser implements IRegistryDataParser {
		private static final IContentElement EMPTY_ELEMENT = new IContentElement() {};

		@Override
		public String getName() {
			return "manual_content";
		}

		@Override
		public void parse(JsonObject json) {
			String lang = JsonUtils.getString(json, "lang").toLowerCase();
			String currentLang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode();
			if (!lang.equals("en_us") && !lang.equals(currentLang)) {
				return;
			}
			String category = JsonUtils.getString(json, "category");
			List<IContentElement> contents = new ArrayList<>();
			if (lang.equals("en_us") && !currentLang.equals(lang)) {
				englishCategoryContents.put(category, contents);
			} else {
				categoryContents.put(category, contents);
			}

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
				case "heading":
					return HeadingElement.parse(elementJson);
				case "toc_list":
					return TableOfContentsElement.parse(elementJson);
				case "image":
					return ImageElement.parse(elementJson);
				case "item":
					return ItemElement.parse(elementJson);
				default:
					return EMPTY_ELEMENT;
			}
		}
	}
}
