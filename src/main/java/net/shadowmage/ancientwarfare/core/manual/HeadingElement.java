package net.shadowmage.ancientwarfare.core.manual;

import com.google.gson.JsonObject;
import net.minecraft.util.JsonUtils;

public class HeadingElement implements IContentElement {
	private final String text;
	private final int level;

	private HeadingElement(String text, int level) {
		this.text = text;
		this.level = level;
	}

	public String getText() {
		return text;
	}

	public int getLevel() {
		return level;
	}

	public static HeadingElement parse(JsonObject elementJson) {
		return new HeadingElement(JsonUtils.getString(elementJson, "text"), JsonUtils.getInt(elementJson, "level"));
	}
}
