package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;

import java.util.ArrayList;
import java.util.List;

public class TableOfContentsElement implements IContentElement {
	private final List<TableOfContentsItem> items;

	public TableOfContentsElement(List<TableOfContentsItem> contentItems) {
		items = contentItems;
	}

	public static TableOfContentsElement parse(JsonObject elementJson) {
		JsonArray contents = JsonUtils.getJsonArray(elementJson, "items");

		ArrayList<TableOfContentsItem> tocItems = new ArrayList<>();
		for (JsonElement e : contents) {
			JsonObject contentItem = JsonUtils.getJsonObject(e, "");
			tocItems.add(new TableOfContentsItem(JsonUtils.getString(contentItem, "text"), JsonUtils.getString(contentItem, "category_link")));
		}

		return new TableOfContentsElement(tocItems);
	}

	public List<TableOfContentsItem> getItems() {
		return items;
	}

	public static class TableOfContentsItem {
		private final String text;
		private final String category;

		private TableOfContentsItem(String text, String category) {
			this.text = text;
			this.category = category;
		}

		public String getText() {
			return text;
		}

		public String getCategory() {
			return category;
		}
	}
}
